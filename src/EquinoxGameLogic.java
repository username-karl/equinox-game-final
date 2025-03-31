import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
public class EquinoxGameLogic extends JPanel implements ActionListener, KeyListener {
    //INITIALIZATIONS
    //BOARD
    int tileSize =32;
    int rows =16;
    int columns =16;
    int boardWidth = tileSize*columns;
    int boardHeight = tileSize*rows;

    //Image
    Image shipImg;
    Image enemyImgVar1;
    Image enemyImgVar2;
    Image enemyImgVar3;
    Image world1BG;
    ArrayList<Image> enemyImgArray;

    //Ship
    int shipWidth = tileSize*2; //64px
    int shipHeight = tileSize;  //32px
    int shipX = tileSize*columns/2 - tileSize;
    int shipY = boardHeight-tileSize*2;
    int shipVelocityX=tileSize;     //Move Speed

    ShipUser ship;

    //Enemies
    ArrayList<Enemy> enemyArray;
    int enemyWidth = tileSize*2;
    int enemyHeight = tileSize;
    int enemyX=tileSize;
    int enemyY=tileSize;
    int enemyVelocityX=1;
    int enemyRows = 2;
    int enemyColumns = 3;
    int enemyCount = 0; //enemies to defeat



    //Timer
    Timer gameLoop;


     EquinoxGameLogic(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.DARK_GRAY);

        setFocusable(true);
        addKeyListener(this);


         //Image loading
            //Creatures
         shipImg = new ImageIcon(getClass().getResource("./img/protagtest.png")).getImage();
         enemyImgVar1 = new ImageIcon(getClass().getResource("./img/monstertestvar1.png")).getImage();
         enemyImgVar2 = new ImageIcon(getClass().getResource("./img/monstertestvar2.png")).getImage();
         enemyImgVar3 = new ImageIcon(getClass().getResource("./img/monstertestvar3.png")).getImage();

         enemyImgArray = new ArrayList<Image>();
         enemyImgArray.add(enemyImgVar1);
         enemyImgArray.add(enemyImgVar2);
         enemyImgArray.add(enemyImgVar3);
        //  //Maps
         world1BG = new ImageIcon(getClass().getResource("./img/world1BG.png")).getImage();

         //Ship
         ship = new ShipUser(shipX,shipY,shipWidth,shipHeight,shipImg);
         enemyArray = new ArrayList<Enemy>();

         //Game timer
         gameLoop = new Timer(1000/60,this);
         createEnemies();
         gameLoop.start();
    }

    //Draw assets
    public void paintComponent(Graphics g){
         super.paintComponent(g);

        if (world1BG != null) {
            g.drawImage(world1BG, 0, 0, boardWidth, boardHeight, this);
        }
         draw(g);
    }
    //DRAW
    public void draw(Graphics g){

         //Draw Ship
         g.drawImage(ship.img, ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight(),null);

         //Aliens
        for(int i=0;i<enemyArray.size();i++){
            Block enemy=enemyArray.get(i);
            if(enemy.isAlive()){
                g.drawImage(enemy.img,enemy.getX(),enemy.getY(),enemy.getWidth(),enemy.getHeight(),null);
            }
        }
    }
    //MOVE
    public void moveEnemy(){
         //ENEMIES
        for(int i=0; i<enemyArray.size();i++){
            Block enemy = enemyArray.get(i);
            if(enemy.isAlive()){
                enemy.setX(enemy.getX()+enemyVelocityX);

                if(enemy.getX()+enemyWidth>=boardWidth||enemy.getX()<=0){
                    enemyVelocityX *=-1;
                    enemy.setX(enemy.getX()+enemyVelocityX*2);

                    //Move Enemies down by one row
                    for(int j=0; j<enemyArray.size();j++){

                        enemyArray.get(j).setY(enemyArray.get(j).getY()+enemyHeight);
                    }
                }
            }
        }
    }

    //Create enemies
    public void createEnemies(){
         Random random = new Random();
         for(int r=0; r<enemyRows; r++){
             for(int c=0; c<enemyColumns; c++){
                 int randomImgIndex = random.nextInt(enemyImgArray.size());
                 Enemy enemy = new Enemy(
                         enemyX + c*enemyWidth,
                         enemyY + r*enemyHeight,
                         enemyWidth,
                         enemyHeight,
                         enemyImgArray.get(randomImgIndex)
                 );
                 enemyArray.add(enemy);
             }
         }
         enemyCount = enemyArray.size();
    }

    //
    @Override
    public void actionPerformed(ActionEvent e) {

         moveEnemy();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
         if(e.getKeyCode()==KeyEvent.VK_A && ship.getX() - shipVelocityX >=0){
             ship.setX(ship.getX()-shipVelocityX);
         }
         else if(e.getKeyCode()==KeyEvent.VK_D && ship.getX() + ship.getWidth() + shipVelocityX <= boardWidth){
             ship.setX(ship.getX() + shipVelocityX);
         }
    }
}
