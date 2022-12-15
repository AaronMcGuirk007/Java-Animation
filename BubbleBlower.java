import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * THe animated BubbleBlower class that implements the Bubble class and allows
 * the user to blow Bubbles.
 * 
 * @author Aaron McGuirk, Ethan Bartlett, Jason Macutek
 * @version Spring 2022
 */

public class BubbleBlower extends MouseAdapter implements Runnable {

    private ArrayList<Bubble> bubbles = new ArrayList<>();

    private Bubble newBubble;

    private int click;

    private Point currentMouse;

    private JPanel panel;

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 600;

    // this method is called by the paintComponent method of
    // the anonymous extension of JPanel, to keep that method
    // from getting too long
    protected void redraw(Graphics g) {
        // draw all of the bubbles in the list
        // remove the bubbles that are done and popped
        int i = 0;
        while (i < bubbles.size()) {
            Bubble b = bubbles.get(i);
            if (b.done()) {
                b.paint(g);
                bubbles.remove(i);
            } else {
                b.paint(g);
                i++;
            }
        }
    }

    /**
     * The run method to set up the graphical user interface
     */
    @Override
    public void run() {

        // set up the GUI "look and feel" which should match
        // the OS on which we are running
        JFrame.setDefaultLookAndFeelDecorated(true);

        // create a JFrame in which we will build our very
        // tiny GUI, and give the window a name
        JFrame frame = new JFrame("BubbleBlower");
        frame.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        // tell the JFrame that when someone closes the
        // window, the application should terminate
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // initializes the variables
        currentMouse = null;
        newBubble = null;
        click = 0;

        // JPanel with a paintComponent method
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {

                // first, we should call the paintComponent method we are
                // overriding in JPanel
                super.paintComponent(g);

                // redraw our main scene
                redraw(g);

                // adds the centered text to the screen when no bubbles are present
                int stringWidth = g.getFontMetrics().stringWidth("Press and hold the mouse to blow a bubble, release to let it free!") / 2;
                g.setFont(new Font("Calibri", 10 ,15));
                int stringOffset = g.getFontMetrics().getAscent();
                if(bubbles.isEmpty()){
                    g.drawString("Press and hold the mouse to blow a bubble, release to let it free!",  getWidth()/2 - stringWidth - stringOffset, getHeight()/2);
                }
            }
        };

        frame.add(panel);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);

        // display the window we've created
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates the Bubble object where the user clicks
     */
    @Override
    public void mousePressed(MouseEvent e) {
        click++;
        if(click == 1) {
        currentMouse = e.getPoint();
        newBubble = new Bubble(currentMouse, panel);
        bubbles.add(newBubble);
        newBubble.start();
        panel.repaint();
        }
    }

    /**
     * Stops the blowing up of the Bubble and releases it
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        newBubble.isBubble();
        panel.repaint();
        click = 0;
    }

    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new BubbleBlower());
      }

}