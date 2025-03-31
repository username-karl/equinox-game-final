/*
High Priority
TODO: Enemy Behaviors(Enemy types) Mini boss, Boss Fight
TODO: Refactor Code
TODO: Ability Interface for Abilities
TODO: Constants
MID PRIORITY
TODO: Constants
TODO: Start Menu
TODO: Different Levels
TODO: Integrate Story
TODO: Sound Effects


 */
public class GameState {

    //Lose
    public boolean gameOver=false;

    //Scoring
    public int score;

    //Enemies
    public int enemyCount;  //Enemies to defeat

    //Stage
    public Stage currentStage;

}

class Stage {
    private int stageNumber;
    private int currentWave;
    private int totalWaves;
    private boolean minibossSpawned = false;

    public Stage(int stageNumber, int totalWaves) {
        this.stageNumber = stageNumber;
        this.totalWaves = totalWaves;
        this.currentWave = 1;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public void setTotalWaves(int totalWaves) {
        this.totalWaves = totalWaves;
    }

    public boolean isMinibossSpawned() {
        return minibossSpawned;
    }

    public void setMinibossSpawned(boolean minibossSpawned) {
        this.minibossSpawned = minibossSpawned;
    }
}
