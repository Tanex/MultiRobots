package nu.tanex.engine.resources;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class GameSettings {

    private int numPlayersToStartGame;
    public int getNumPlayersToStartGame() { return this.numPlayersToStartGame; }
    public void setNumPlayersToStartGame(int numPlayersToStartGame) { this.numPlayersToStartGame = numPlayersToStartGame; }

    private int numInitialRobots;
    public int getNumInitialRobots() { return this.numInitialRobots; }
    public void setNumInitialRobots(int numInitialRobots) { this.numInitialRobots = numInitialRobots; }

    private int numAdditionalRobotsPerLevel;
    public int getNumAdditionalRobotsPerLevel() { return this.numAdditionalRobotsPerLevel; }
    public void setNumAdditionalRobotsPerLevel(int numAdditionalRobotsPerLevel) { this.numAdditionalRobotsPerLevel = numAdditionalRobotsPerLevel; }

    private int numInitialRubble;
    public int getNumInitialRubble() { return this.numInitialRubble; }
    public void setNumInitialRubble(int numInitialRubble) { this.numInitialRubble = numInitialRubble; }

    private CollisionBehaviour collisionBehaviour;
    public CollisionBehaviour getCollisionBehaviour() { return this.collisionBehaviour; }
    public void setCollisionBehaviour(CollisionBehaviour collisionBehaviour) { this.collisionBehaviour = collisionBehaviour; }

    private int numSafeTeleportsAwarded;
    public int getNumSafeTeleportsAwarded() { return this.numSafeTeleportsAwarded; }
    public void setNumSafeTeleportsAwarded(int numSafeTeleportsAwarded) { this.numSafeTeleportsAwarded = numSafeTeleportsAwarded; }

    private int numAttacksAwarded;
    public int getNumAttacksAwarded() { return this.numAttacksAwarded; }
    public void setNumAttacksAwarded(int numAttacksAwarded) { this.numAttacksAwarded = numAttacksAwarded; }

    private AttackBehaviour attackBehaviour;
    public AttackBehaviour getAttackBehaviour() { return this.attackBehaviour; }
    public void setAttackBehaviour(AttackBehaviour attackBehaviour) { this.attackBehaviour = attackBehaviour; }

    private RobotAiMode robotAiMode;
    public RobotAiMode getRobotAiMode() { return this.robotAiMode; }
    public void setRobotAiMode(RobotAiMode robotAiMode) { this.robotAiMode = robotAiMode; }
    
    public GameSettings(){
    }
}
