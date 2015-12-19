package nu.tanex.server.core;

import nu.tanex.engine.aggregates.PlayerList;
import nu.tanex.engine.aggregates.RobotList;
import nu.tanex.engine.aggregates.RubbleList;
import nu.tanex.engine.data.*;
import nu.tanex.engine.exceptions.GameException;
import nu.tanex.engine.resources.CollisionBehaviour;
import nu.tanex.engine.resources.GameSettings;
import nu.tanex.engine.resources.RobotAiMode;
import nu.tanex.server.aggregates.ClientList;

import java.util.HashMap;

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
    //endregion

    //region Get-/Setters
    public GameSettings getSettings() { return settings; }
    public int getNumPlayers() { return players.size(); }
    //endregion

    //region Constructors
    public Game() {
        settings = new GameSettings();
        players = new ClientList();
        robots = new RobotList();
        rubblePiles = new RubbleList();
    }
    //endregion

    //region Public methods
    public void handlePlayerTurn(int playerNum){
        // TODO: 2015-12-15 get player input over TCP
        moveGameObject(players.get(playerNum), Point.newRandomPoint(settings.getGridWidth(), settings.getGridHeight()),true);
    }

    public void checkGameState(){
        HashMap<Point,GameObject> spaces = new HashMap<>();
        players.forEach(p -> checkForCollision(p, spaces));
        robots.forEach(r -> checkForCollision(r, spaces));
        rubblePiles.forEach(r -> checkForCollision(r, spaces));

        players.removeIf(p -> !p.isAlive());
        robots.removeIf(p -> !p.isAlive());
    }

    public void addPlayers(Client[] players){
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

        //Create robots
        for (int i = 0; i < settings.getNumInitialRobots(); i++) {
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

    public void moveRobots(){
        for (Robot robot : robots){
            moveGameObject(robot, robot.calculateMovement(null), true);
        }
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

    public void moveGameObject(GameObject gameObject, Point point, boolean overWriteExisting){
        placeGameObject(gameObject, point, overWriteExisting);
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
        Class obj1Class = object1.getClass();
        Class obj2Class = object2.getClass();

        //A player is involved
        if(obj1Class.equals(Player.class))
            object1.setAlive(false);
        else if (obj2Class.equals(Player.class))
            object2.setAlive(false);
        //if no player involved
        else {
            if (obj1Class.equals(Rubble.class))
                object2.setAlive(false);
            else if (obj2Class.equals(Player.class))
                object1.setAlive(false);
            else {
                if (settings.getCollisionBehaviour() == CollisionBehaviour.Merge){
                    object1.setAlive(false);
                }
                else {//if (settings.getCollisionBehaviour() == CollisionBehaviour.Rubble)
                    Rubble spawnedRubble = new Rubble();
                    placeGameObject(spawnedRubble, object1.getPoint(), true);
                    rubblePiles.add(spawnedRubble);
                    object1.setAlive(false);
                    object2.setAlive(false);
                }
            }
        }
    }

    private void checkForCollision(GameObject gameObject, HashMap<Point,GameObject> spaces) {
        //If there is collision
        if (spaces.containsKey(gameObject.getPoint()))
            handleCollision(gameObject, spaces.get(gameObject.getPoint()));
        else
            spaces.put(gameObject.getPoint(),gameObject);
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
            rows[i].append("\n");
        }
        for (Player player : players)
            rows[player.getPoint().getY()].setCharAt(player.getPoint().getX(), player.toString().charAt(0));
        for (Rubble rubble : rubblePiles)
            rows[rubble.getPoint().getY()].setCharAt(rubble.getPoint().getX(), rubble.toString().charAt(0));
        for (Robot robot : robots)
                rows[robot.getPoint().getY()].setCharAt(robot.getPoint().getX(), robot.toString().charAt(0));

        String str = "";
        for (StringBuilder row : rows)
            str += row;
        return str;
    }

    //endregion
}
