package nu.tanex.server.core;

import nu.tanex.core.aggregates.RobotList;
import nu.tanex.core.aggregates.RubbleList;
import nu.tanex.core.data.*;
import nu.tanex.core.exceptions.GameException;
import nu.tanex.core.exceptions.TargetPlayerDeadException;
import nu.tanex.core.resources.*;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.resources.GameState;

import java.util.HashMap;
import java.util.Optional;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Game {
    //region Member variables
    private GameSettings settings;
    private ClientList players;
    private RobotList robots;
    private RubbleList rubblePiles;
    private int level;
    //endregion

    //region Get-/Setters

    /**
     * Gets the settings that are loaded for this game
     *
     * @return loaded settings
     */
    public GameSettings getSettings() { return settings; }

    /**
     * Get the number of players that are playing this game
     *
     * @return amount of players
     */
    public int getNumPlayers() { return players.size(); }

    /**
     * Get a list of all the players that are playing in this game
     *
     * @return list of players
     */
    public ClientList getPlayers() { return players; }

    /**
     * Get which level the game is currently on
     *
     * @return games level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the amount of robots that are alive in the game
     *
     * @return number of robots.
     */
    public int getNumRobots() {
        return robots.size();
    }

    /**
     * get the amount of rubble present in the game
     * @return amount of rubble
     */
    public int getNumRubble() {
        return rubblePiles.size();
    }

    /**
     * Get a string detailing the dimensions of the grid in this game.
     *
     * @return grid dimensions width:height
     */
    public String getGridDimensions() {
        return settings.getGridWidth() + ":" + settings.getGridHeight();
    }
    //endregion

    //region Constructors
    /**
     * Initializes the game object and loads default settings from file if available.
     */
    public Game() {
        settings = new GameSettings();
        players = new ClientList();
        robots = new RobotList();
        rubblePiles = new RubbleList();
        level = 0;
        settings.loadSettingsFromFile(Resources.GAME_DEFAULT_CONFIG);
    }
    //endregion

    //region Public methods
    /**
     * Handles letting all robots perform their moves for a turn.
     */
    public void handleRobotsTurn(){
        for (Robot robot : robots){
            if (settings.getRobotAiMode() == RobotAiMode.ChaseClosest){
                Point closestPlayerPos = null;
                closestPlayerPos = getClosestPlayersPos(robot);
                if (closestPlayerPos == null)
                    return; //Last player committed suicide
                try { moveGameObject(robot, robot.calculateMovement(closestPlayerPos), true);} catch (TargetPlayerDeadException e) {}
            }
            else {
                try {
                    moveGameObject(robot, robot.calculateMovement(null), true);
                } catch (TargetPlayerDeadException te) {
                    Optional<Client> newPlayer = players.stream().filter(Client::isAlive).findAny();
                    if (newPlayer.isPresent())
                        robot.setTargetPlayer(newPlayer.get());
                    try { moveGameObject(robot, robot.calculateMovement(null), true); } catch (TargetPlayerDeadException e) {}
                }
            }
        }
    }

    /**
     * Go through all the objects in the game world and check if any are occupying the same space
     * and if they are resolve the collision.
     */
    public void checkForCollisions(){
        HashMap<Point,GameObject> spaces = new HashMap<>();
        players.forEach(p -> checkForCollision(p, spaces));
        robots.forEach(r -> checkForCollision(r, spaces));
        rubblePiles.forEach(r -> checkForCollision(r, spaces));

        players.stream().filter(p -> !p.isAlive()).forEach(p -> p.sendMessage("YouDied"));
        robots.removeIf(p -> !p.isAlive());
    }

    /**
     * Check the games state to see if anyone has won.
     *
     * @see GameState
     * @return the games state.
     */
    public GameState checkGameState(){
        if (robots.size() == 0)
            return GameState.PlayersWon;
        else if (players.stream().allMatch(p -> !p.isAlive()))
            return GameState.RobotsWon;
        else
            return GameState.Running;
    }

    /**
     * Add players to the game
     *
     * @param players player to add
     */
    public void addPlayers(ClientList players){
        for (Client player : players){
            if(!this.players.contains(player))
                this.players.add(player);
        }
    }

    /**
     * Perform initial setup
     * @throws GameException
     */
    public void createGameGrid() throws GameException {
        //Make sure that at least 10% of the GameGrid is going to be empty
        if (players.size() + settings.getNumInitialRobots() + settings.getNumInitialRubble()
                > settings.getGridHeight()*settings.getGridWidth()*Resources.MAX_GRID_FILL_ALLOWED)
            throw new GameException("Grid overfilled: " + (players.size() + settings.getNumInitialRobots() + settings.getNumInitialRubble())
                    + " objects for " + (settings.getGridHeight()*settings.getGridWidth()) + "size grid.");
        players.forEach(this::initPlayer);
        generateGrid(settings.getNumInitialRobots());
    }

    /**
     * Randomly places a {@code GameObject} into the grid, will not overwrite anything already in the grid.
     *
     * @param gameObject Object that should be placed in the grid.
     */
    public void randomlyPlaceGameObject(GameObject gameObject) {
        Point point = new Point();
        //Keep generating new x,y values until the object is successfully placed in the grid
        while (!placeGameObject(gameObject, Point.newRandomPoint(settings.getGridWidth(), settings.getGridHeight()), false))
            ;
    }

    /**
     * Moves a game object in the game world to a new point
     *
     * @param gameObject GameObject that should be moved
     * @param point The point to place the game object at
     * @param overWriteExisting if it is allowed to create a collision through the moving
     * @return if the object was successfully placed
     */
    public boolean moveGameObject(GameObject gameObject, Point point, boolean overWriteExisting){
        if (point.getX() < 0 || point.getX() >= settings.getGridWidth() ||
                point.getY() < 0 || point.getY() >= settings.getGridHeight()) {
            return false;
        }
        return placeGameObject(gameObject, point, overWriteExisting);
    }

    /**
     * Progress the game to the next level generating a new game board.
     */
    public void nextLevel() {
        level++;
        for (Player player : players){
            if (player.isAlive()){
                player.addAttacks(settings.getNumAttacksAwarded());
                player.addRandomTeleports(settings.getNumRandomTeleportsAwarded());
                player.addSafeeleports(settings.getNumSafeTeleportsAwarded());
                player.addScore(25);
            }
            //else
                //player.setAlive(true);
        }
        generateGrid(settings.getNumInitialRobots() + settings.getNumAdditionalRobotsPerLevel() * level);
    }

    /**
     * Let a Client object perform an action in the game.
     *
     * @see PlayerAction
     * @param client Player performing the action.
     * @param playerAction action to perform.
     */
    public void playerPerformAction(Client client, PlayerAction playerAction) {
        switch (playerAction) {
            case Attack:
                if (client.getNumAttacks() < 1)
                    break;
                client.takeAttacks(1);
                for (Robot robot : robots) {
                    if (robot.getPoint().isWithinOneMove(client.getPoint())) {
                        client.addScore(10);
                        robot.setAlive(false);
                        if (settings.getPlayerAttacks() == PlayerAttacks.KillOne)
                            break;
                    }
                }
                break;
            case MoveUp:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.Up), true);
                break;
            case MoveRight:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.Right), true);
                break;
            case MoveLeft:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.Left), true);
                break;
            case MoveDown:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.Down), true);
                break;
            case MoveUpRight:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.UpRight), true);
                break;
            case MoveUpLeft:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.UpLeft), true);
                break;
            case MoveDownRight:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.DownRight), true);
                break;
            case MoveDownLeft:
                moveGameObject(client, client.getPoint().getPointInDirection(Direction.DownLeft), true);
                break;
            case Wait:
                break;
            case RandomTeleport:
                if (client.getNumRandomTeleports() < 1)
                    break;
                client.takeRandomTeleports(1);
                randomlyPlaceGameObject(client);
                break;
            case SafeTeleport:
                if (client.getNumSafeTeleports() < 1)
                    break;
                // TODO: 2016-01-20 smarter system for this
                do{
                    randomlyPlaceGameObject(client);
                } while(!isPlayerSafe(client));
                client.takeSafeTeleports(1);
                break;
        }
        System.out.println(client + " performed action: " + playerAction);
    }
    //endregion

    //region Private methods
    /**
     * Places a @code GameObject in the @code GameGrid at point (x, y).
     *
     * @param gameObject        Object that should be placed in the grid.
     * @param point             Point where the @code GameObject should be placed.
     * @param overWriteExisting Whether to overwrite any existing object at (x, y).
     * @return Indicates if the object was successfully placed in the grid or not.
     */
    private boolean placeGameObject(GameObject gameObject, Point point, boolean overWriteExisting) {
        //Space is occupied and we shouldn't overwrite -> abort
        if (!overWriteExisting && isOccupied(point))
            return false;

        gameObject.getPoint().set(point);
        return true;
    }


    private void initPlayer(Client player){
        player.setNumAttacks(settings.getNumAttacksAwarded());
        player.setNumRandomTeleports(settings.getNumRandomTeleportsAwarded());
        player.setNumSafeTeleports(settings.getNumSafeTeleportsAwarded());
        player.setScore(0);
    }

    private Point getClosestPlayersPos(Robot robot) {
        Point targetPoint = null;
        double distance = 0.0;
        for (Client player : players) {
            double distanceToPlayer = robot.getPoint().distanceTo(player.getPoint());
            if (distanceToPlayer < Resources.MAX_CHASE_DISTANCE && distanceToPlayer > distance && player.isAlive())
                targetPoint = player.getPoint();
        }
        return targetPoint;
    }

    private boolean isOccupied(Point point){
        for (Player player : players)
            if (player.getPoint().equals(point))
                return true;
        for (Rubble rubble : rubblePiles)
            if (rubble.getPoint().equals(point))
                return true;
        for (Robot robot : robots)
            if (robot.getPoint().equals(point))
                return true;
        return false;
    }

    private void handleCollision(GameObject object1, GameObject object2){
        switch (object1.getCollisionBehaviour().collideWith(object2)) {
            case NA: // TODO: 2015-12-21 throw exception....
                break;
            case Object1Death:
                object1.setAlive(false);
                break;
            case Object2Death:
                object2.setAlive(false);
                break;
            case RobotCollision:
                if (settings.getRobotCollisions() == RobotCollisions.Merge){
                    object1.setAlive(false);
                }
                else {//if (settings.getRobotCollisions() == RobotCollisions.Rubble)
                    Rubble spawnedRubble = new Rubble();
                    placeGameObject(spawnedRubble, object1.getPoint(), true);
                    rubblePiles.add(spawnedRubble);
                    object1.setAlive(false);
                    object2.setAlive(false);
                }
                break;
            case PlayerCollision:
                moveGameObject(object1, object1.getPoint().getPointInRandomDirection(), true);
                moveGameObject(object2, object2.getPoint().getPointInRandomDirection(), true);
                break;
        }
    }

    private void checkForCollision(GameObject gameObject, HashMap<Point,GameObject> spaces) {
        //If there is collision
        if (spaces.containsKey(gameObject.getPoint()))
            handleCollision(gameObject, spaces.get(gameObject.getPoint()));
        else
            spaces.put(gameObject.getPoint(),gameObject);
    }

    private void generateGrid(int initialRobots){
        robots.clear();
        rubblePiles.clear();
        players.forEach(p -> p.setAlive(true));

        //Create robots
        for (int i = 0; i < initialRobots; i++) {
            if(settings.getRobotAiMode() == RobotAiMode.TargetPlayer)
                robots.add(new Robot(players.get(i % players.size())));
            else
                robots.add(new Robot());
        }
        for (int i = 0; i < settings.getNumInitialRubble(); i++)
            rubblePiles.add(new Rubble());

        //Randomly place all the robots and players
        robots.forEach(this::randomlyPlaceGameObject);
        players.forEach(this::randomlyPlaceGameObject);
        rubblePiles.forEach(this::randomlyPlaceGameObject);
    }

    private boolean isPlayerSafe(Client client) {
        for (Robot robot : robots) {
            if (client.getPoint().isWithinOneMove(robot.getPoint()))
                return false;
        }
        return true;
    }
    //endregion

    //region Object overrides
    @Override
    public String toString() {
        StringBuilder rows[] = new StringBuilder[settings.getGridHeight()];
        //Fill all rows with empty spaces
        for (int i = 0; i < settings.getGridHeight(); i++) {
            rows[i] = new StringBuilder("");
            for (int j = 0; j < settings.getGridWidth(); j++)
                rows[i].append(".");
            rows[i].append(">");
        }
        for (Rubble rubble : rubblePiles)
            rows[rubble.getPoint().getY()].setCharAt(rubble.getPoint().getX(), rubble.toString().charAt(0));
        for (Robot robot : robots)
                rows[robot.getPoint().getY()].setCharAt(robot.getPoint().getX(), robot.toString().charAt(0));
        for (int i = 0; i < players.size(); i++)
            rows[players.get(i).getPoint().getY()].replace(players.get(i).getPoint().getX(), players.get(i).getPoint().getX() + 1, "[" + Integer.toString(i) + "]");
        //rows[players.get(i).getPoint().getY()].setCharAt(players.get(i).getPoint().getX(), (char)('0' + i));

        String str = "";
        for (StringBuilder row : rows)
            str += row;
        return str;
    }
    //endregion
}
