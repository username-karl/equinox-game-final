import java.util.Random;

public class GameState {
    
    //Lose
    public boolean gameOver=false;

    //Scoring
    public int score;

    //Monies
    public int money;

    public int getMoney() {
        return money;
    }

    //Enemies
    public long enemyCount;  //Enemies to defeat
    public long enemySlain;  //Enemies defeated
    
    //Stage
    public Stage currentStage;

    public GameState(int score,int money){
        this.score=score;
        this.money=money;
    }

    public GameState(){
    }



    public void dropLoot(){
        //Money drop
                Random random = new Random();
                int rndm=random.nextInt(3);
                switch(rndm){
                    case 1:
                        money += 10;
                    break;
                    case 2:
                        money += 5;
                    break;
                    case 0:
                        money+=0;
                    break;
                    default:
                        money+=0;
                    break;
                }
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
