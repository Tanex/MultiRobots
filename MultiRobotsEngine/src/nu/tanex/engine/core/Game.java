package nu.tanex.engine.core;

import nu.tanex.engine.aggregates.GameGrid;
import nu.tanex.engine.aggregates.PlayerList;
import nu.tanex.engine.aggregates.RobotList;
import nu.tanex.engine.data.Player;
import nu.tanex.engine.data.Robot;
import nu.tanex.engine.data.Rubble;
import nu.tanex.engine.exceptions.GameException;
import nu.tanex.engine.resources.GameSettings;
import nu.tanex.engine.resources.RobotAiMode;

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
    private PlayerList players;
    private RobotList robots;
    private GameGrid gameGrid;
    private int turn;
    //endregion

    //region Constructors
    public Game() {
        settings = new GameSettings();
        players = new PlayerList();
        robots = new RobotList();
        turn = 1;
    }
    //endregion

    //region Public methods
    public void addPlayers(Player[] players){
        for (Player player : players){
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

        //Create a new gameGrid
        gameGrid = new GameGrid(settings.getGridWidth(), settings.getGridHeight());

        //Create robots
        for (int i = 0; i < settings.getNumInitialRobots(); i++) {
            if(settings.getRobotAiMode() == RobotAiMode.TargetPlayer)
                robots.add(new Robot(players.get(i % players.size())));
            else
                robots.add(new Robot());
        }

        //Randomly place all the robots and players
        robots.forEach(r -> gameGrid.randomlyPlaceGameObject(r));
        players.forEach(p -> gameGrid.randomlyPlaceGameObject(p));

        for (int i = 0; i < settings.getNumInitialRubble(); i++)
            gameGrid.randomlyPlaceGameObject(new Rubble());
    }

    //endregion

    public GameSettings getSettings() { return settings; }
}
