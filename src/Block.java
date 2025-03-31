import java.awt.*;

public class Block {

    private int x;
    private int y;
    private int width;
    private int height;
    public Image img;
    private boolean alive =true;   //Used for aliens
    private boolean used =false; //Used for bullets
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isUsed() {
        return used;
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

    private boolean isMiniBoss;
    private boolean isBoss;
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

    public Enemy(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
}

class Bullet extends Block{
    public Bullet(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);

    }

}