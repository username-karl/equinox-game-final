public class GameState {

    //Lose
    public boolean gameOver=false;

    //Scoring
    public int score;

    //Enemies
    public int enemyCount;  //Enemies to defeat

    //Stage
    public Stage currentStage;

    public GameState(int score){
        this.score=score;
    }

    public GameState(){
    }

}

class Stage {
    private int stageNumber;
    private int currentWave;
    private int totalWaves;
    private boolean specialEnemySpawned = false;

    public Stage(int stageNumber, int totalWaves) {
        this.stageNumber = stageNumber;
        this.totalWaves = totalWaves;
        this.currentWave = 1;
    }
    //SETTERS AND GETTERS
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

    public boolean isSpecialEnemySpawned() {
        return specialEnemySpawned;
    }

    public void setSpecialEnemySpawned(boolean specialEnemySpawned) {
        this.specialEnemySpawned = specialEnemySpawned;
    }
}
