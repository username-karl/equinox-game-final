import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
public class EquinoxGameLogic extends JPanel implements ActionListener, KeyListener {


    //INIT
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
    Image laserBlue;
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
    //Bullets
    ArrayList<Bullet> bulletArray;
    int bulletWidth = tileSize/8;   //Bullet size width
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10; //Bullet movespeed



    //Timer
    Timer gameLoop;
    //Scoring
    int score;
    //Lose
    boolean gameOver=false;

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
        //Misc
         laserBlue = new ImageIcon(getClass().getResource("./img/laserBlue.png")).getImage();





         //Ship
         ship = new ShipUser(shipX,shipY,shipWidth,shipHeight,shipImg);

         //Enemy Array Group
         enemyArray = new ArrayList<Enemy>();

         //Bullets
         bulletArray= new ArrayList<Bullet>();


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
            Enemy enemy=enemyArray.get(i);
            if(enemy.isAlive()){
                g.drawImage(enemy.img,enemy.getX(),enemy.getY(),enemy.getWidth(),enemy.getHeight(),null);
            }
        }

        g.setColor(Color.white);
        for(int i=0;i<bulletArray.size();i++){
            Block bullet = bulletArray.get(i);
            if(!bullet.isUsed()){
                g.drawImage(laserBlue,bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight(),null);
//                g.drawRect(bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight());
//                g.fillRect(bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight());
            }
        }

        //Draw Score
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial",Font.PLAIN,24));
        if(gameOver){
            g.drawString("Game Over: "+String.valueOf(score),10,35);
        }else{
            g.drawString(String.valueOf(score),10,35);
        }
    }
    //MOVE
    public void moveGame(){
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
                //Lose condition
                if(enemy.getY()>=ship.getY()){
                    gameOver=true;
                }
            }
        }

        //Bullets
        for(int i=0; i <bulletArray.size();i++){
            Bullet bullet = bulletArray.get(i);
            bullet.setY(bullet.getY()+bulletVelocityY);

            //Bullet collision check
            for(int j=0;j<enemyArray.size();j++){
                Enemy enemy = enemyArray.get(j);
                if(!bullet.isUsed()&&enemy.isAlive()&&detectCollision(bullet,enemy)){
                    bullet.setUsed(true);
                    enemy.setAlive(false);
                    enemyCount--;
                    score+=100;
                }
            }
        }

        //Optimizations
        while(bulletArray.size()>0&&(bulletArray.get(0).isUsed() || bulletArray.get(0).getY()<0)){
            bulletArray.remove(0);//Removes the first element of the array
        }

        //Next wave of enemies
        if(enemyCount==0){
            //Wave clear points
            score+=enemyRows*enemyColumns*100;

            //Increase the number of aliens in columns and rows by 1
            enemyColumns=Math.min(enemyColumns+1,columns/2-2);  //cap column at 16/2 -2 =6
            enemyRows=Math.min(enemyRows+1, rows-6); //Cap row at 16-6 = 10
            enemyArray.clear();
            bulletArray.clear();
            createEnemies();


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

    public boolean detectCollision(Block a, Block b){
         return a.getX()<b.getX()+b.getWidth()&&    //entity a's top left corner doesn't reach b's top right corner
                 a.getX()+a.getWidth()>b.getX()&&   //entity a's top right corner passes b's top left corner
                 a.getY()<b.getY()+b.getHeight()&&  //entity a's top left corner doesn't reach b's bottom left corner
                 a.getY()+a.getHeight()>b.getY();   //entity a's bottom left corner passes b's top let corner
    }





    //
    @Override
    public void actionPerformed(ActionEvent e) {

         moveGame();
        repaint();
        if(gameOver){
            gameLoop.stop();
        }
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
         }else if(e.getKeyCode()==KeyEvent.VK_SPACE){
             Bullet bullet = new Bullet(ship.getX()+ship.getWidth()*15/32,ship.getY(),bulletWidth,bulletHeight,laserBlue);
             bulletArray.add(bullet);
         }
    }
}
