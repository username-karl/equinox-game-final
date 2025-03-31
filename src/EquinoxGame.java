import javax.swing.*;
public class EquinoxGame {
    public static void main(String[] args){

        //JFRAME VARIABLES
        int tileSize =32;
        int rows=16;
        int columns =16;
        int boardWidth = tileSize*columns;
        int boardHeight = tileSize*rows;


        //JFrame main
        JFrame frame = new JFrame("EquinoxGame");
        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Game Screen
        EquinoxGameLogic equinox = new EquinoxGameLogic();
        frame.add(equinox);
        frame.pack();
        equinox.requestFocus();
        frame.setVisible(true);



    }
}
