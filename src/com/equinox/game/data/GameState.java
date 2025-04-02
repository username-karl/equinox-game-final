package com.equinox.game.data;

import java.util.Random;
import com.equinox.game.data.Stage;

public class GameState {
    
    //Lose
    public boolean gameOver=false;

    //Scoring
    public int score;

    //Monies
    public int money=500;

    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
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