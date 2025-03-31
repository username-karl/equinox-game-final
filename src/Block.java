import java.awt.*;
import java.util.ArrayList;

public class Block {

    private int x;
    private int y;
    private int width;
    private int height;
    public Image img;
    //Setters & Getters

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setUsed(boolean used) {
    }

    public boolean isUsed() {
        return false;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y){
        this.y=y;
    }
    public void setWidth(int width){
        this.width=width;
    }
    public void setHeight(int height){
        this.height=height;
    }



    public Block(int x, int y, int width, int height, Image img){
        this.x = x;
        this.y = y;
        this.width=width;
        this.height=height;
        this.img=img;
    }
}

class ShipUser extends Block {

    private boolean isDev;
    //SETTERS AND GETTERS
    public boolean isDev() {
        return isDev;
    }
    public void setDev(boolean dev) {
        isDev = dev;
    }

    public ShipUser(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
}

class Enemy extends Block{

    protected int enemyVelocityX;
    private boolean alive =true;
    private boolean isMiniBoss;
    private boolean isBoss;
    private boolean moveDown = false;
    //SETTERS AND GETTERS
    public boolean isBoss() {
        return isBoss;
    }
    public void setBoss(boolean boss) {
        isBoss = boss;
    }
    public boolean isMiniBoss() {
        return isMiniBoss;
    }
    public void setMiniBoss(boolean miniBoss) {
        isMiniBoss = miniBoss;
    }
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public boolean isMoveDown() {
        return moveDown;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    public Enemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        super(x, y, width, height, img);
        this.enemyVelocityX = enemyVelocityX;
    }

    public void move(int boardWidth, int enemyWidth, int enemyHeight) {
        setX(getX() + enemyVelocityX);

        if (getX() + enemyWidth >= boardWidth || getX() <= 0) {
            enemyVelocityX *= -1;
            setX(getX() + enemyVelocityX * 2);
            setMoveDown(true);
        }
    }
    public void moveDown(int enemyHeight){
        if(isMoveDown()){
            setY(getY() + enemyHeight);
            setMoveDown(false);
        }
    }
}
class FastEnemy extends Enemy{
    public FastEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        super(x, y, width, height, img, enemyVelocityX);
        this.enemyVelocityX = enemyVelocityX * 2; // Double the speed
    }
}
class ShootingEnemy extends Enemy{
    private int shootCooldown = 100; // Adjust as needed
    private int currentCooldown = 0;
    public ShootingEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        super(x, y, width, height, img, enemyVelocityX);
    }
    public void shoot(ArrayList<EnemyBullet> enemyBulletArray, Image enemyBulletImg, int bulletWidth, int bulletHeight) {
        if (currentCooldown <= 0) {
            EnemyBullet bullet = new EnemyBullet(getX() + getWidth() / 2, getY() + getHeight(), bulletWidth, bulletHeight, enemyBulletImg);
            enemyBulletArray.add(bullet);
            currentCooldown = shootCooldown;
        } else {
            currentCooldown--;
        }
    }
}
class Miniboss extends Enemy{
    Image minibossImg;
    private int hitpoints = 20;
    public Miniboss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image minibossImg) {
        super(x, y, width, height, img, enemyVelocityX);
        this.minibossImg=minibossImg;
        setMiniBoss(true);
    }
    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }
}

class Bullet extends Block{
    private boolean used = false;
    public Bullet(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);

    }
    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isUsed() {
        return used;
    }

}
class EnemyBullet extends Bullet{
    private int bulletVelocityY = 10;
    public EnemyBullet(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
    public void move(){
        setY(getY() + bulletVelocityY);
    }
}

class TacticalQ extends Bullet{
    public TacticalQ(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
}

class TacticalE extends Bullet{
    public TacticalE(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
}

class SpecialMove extends Bullet{
    public SpecialMove(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
}