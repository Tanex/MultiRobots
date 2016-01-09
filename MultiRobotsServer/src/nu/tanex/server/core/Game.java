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
    //How full is the GameGrid allowed to be (given in percentage)
    public static final double MAX_GRID_FILL_ALLOWED = 0.9;

    //region Member variables
    private GameSettings settings;
    private ClientList players;
    private RobotList robots;
    private RubbleList rubblePiles;
    private int level;
    //endregion

    //region Get-/Setters
    public GameSettings getSettings() { return settings; }
    public int getNumPlayers() { return players.size(); }
    public ClientList getPlayers() { return players; }
    //endregion

    //region Constructors
    public Game() {
        settings = new GameSettings();
        players = new ClientList();
        robots = new RobotList();
        rubblePiles = new RubbleList();
        level = 0;
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

    private Point getClosestPlayersPos(Robot robot) {
        Point targetPoint = null;
        double distance = 0.0;
        for (Client player : players) {
            if (robot.getPoint().distanceTo(player.getPoint()) > distance && player.isAlive())
                targetPoint = player.getPoint();
        }
        return targetPoint;
    }

    public void checkForCollisions(){
        HashMap<Point,GameObject> spaces = new HashMap<>();
        players.forEach(p -> checkForCollision(p, spaces));
        robots.forEach(r -> checkForCollision(r, spaces));
        rubblePiles.forEach(r -> checkForCollision(r, spaces));

        players.stream().filter(p -> !p.isAlive()).forEach(p -> p.sendMessage("YouDied"));
        robots.removeIf(p -> !p.isAlive());
    }

    public GameState checkGameState(){
        if (robots.size() == 0)
            return GameState.PlayersWon;
        else if (players.stream().allMatch(p -> !p.isAlive()))
            return GameState.RobotsWon;
        else
            return GameState.Running;
    }

    public void addPlayers(ClientList players){
        for (Client player : players){
            if(!this.players.contains(player))
                this.players.add(player);
        }
    }

    public void createGameGrid() throws GameException {
        //Make sure that at least 10% of the GameGrid is going to be empty
        if (players.size() + settings.getNumInitialRobots() + settings.getNumInitialRubble()
                > settings.getGridHeight()*settings.getGridWidth()*MAX_GRID_FILL_ALLOWED)
            throw new GameException("Grid overfilled: " + (players.size() + settings.getNumInitialRobots() + settings.getNumInitialRubble())
                    + " objects for " + (settings.getGridHeight()*settings.getGridWidth()) + "size grid.");

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

    public boolean moveGameObject(GameObject gameObject, Point point, boolean overWriteExisting){
        if (point.getX() < 0 && point.getX() >= settings.getGridWidth() &&
                point.getY() <0 && point.getY() >= settings.getGridHeight()){
            return false;
        }
        return placeGameObject(gameObject, point, overWriteExisting);
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
        for (int i = 0; i < players.size(); i++)
            rows[players.get(i).getPoint().getY()].setCharAt(players.get(i).getPoint().getX(), (char)('0' + i));
        for (Rubble rubble : rubblePiles)
            rows[rubble.getPoint().getY()].setCharAt(rubble.getPoint().getX(), rubble.toString().charAt(0));
        for (Robot robot : robots)
                rows[robot.getPoint().getY()].setCharAt(robot.getPoint().getX(), robot.toString().charAt(0));

        String str = "";
        for (StringBuilder row : rows)
            str += row;
        return str;
    }

    public void nextLevel() {
        level++;
        for (Player player : players){
            if (player.isAlive())
                ;//Player rewards per level
            else
                player.setAlive(true);
        }
        generateGrid(settings.getNumInitialRobots() + settings.getNumAdditionalRobotsPerLevel() * level);
    }

    public void playerPerformAction(Client client, PlayerAction playerAction) {
        switch (playerAction) {
            case Attack:
                for (Robot robot : robots) {
                    if (robot.getPoint().isWithinOneMove(client.getPoint())) {
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
                randomlyPlaceGameObject(client);
                break;
            case SafeTeleport:
                do{
                    randomlyPlaceGameObject(client);
                } while(!isPlayerSafe(client));
                break;
        }
        System.out.println(client + " performed action: " + playerAction);
    }

    private boolean isPlayerSafe(Client client) {
        for (Robot robot : robots) {
            if (client.getPoint().isWithinOneMove(robot.getPoint()))
                return false;
        }
        return true;
    }

    //endregion
}
