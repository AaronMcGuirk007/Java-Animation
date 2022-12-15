import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * THe Bubble class that makes Bubbles! Which is used for the BubbleBlower
 * class.
 * 
 * @author Aaron McGuirk, Ethan Bartlett, Jason Macutek
 * @version Spring 2022
 */

public class Bubble extends Thread {

    private Random rand = new Random();

    private Point startPoint;

    private int size;

    private double ySpeed;
    private double xSpeed;
    private double popTimer;
    private double timer;

    private Color bColor;

    private boolean done;
    private boolean expanded;
    private boolean popped;

    private JComponent panel;

    /**
     * Constructs a new Bubble!
     * 
     * @param startPoint the point where the Bubble will be placed.
     * @param panel      the panel which the Bubble is placed on.
     */
    public Bubble(Point startPoint, JComponent panel) {
        this.startPoint = startPoint;
        this.panel = panel;
        bColor = new Color(173, 216, 230, 100);
        size = 0;
        ySpeed = 0;
        xSpeed = 0;
        popTimer = (double) rand.nextInt(20);
        timer = 0;
        done = false;
        expanded = false;
        popped = false;
    }

    /**
     * This method draws the Bubbles of the panel and pops the
     * bubble when it ends
     * 
     * @param g the Graphical interface that draws the Bubbles.
     */
    public void paint(Graphics g) {
        int diameter = size;
        int radius = size / 2;

        try {

            // Bubble popping animation
            if (popped == true) {

                g.setColor(bColor);
                g.fillOval(startPoint.x - radius, startPoint.y - radius, diameter, diameter);
                sleep(10);
                g.setColor(Color.BLACK);
                g.drawOval(startPoint.x - radius, startPoint.y - radius, diameter, diameter);
                sleep(10);
                done = true;
                panel.repaint();

            } else {

                g.setColor(bColor);
                g.fillOval(startPoint.x - radius, startPoint.y - radius, diameter, diameter);
                g.setColor(Color.BLACK);
                g.drawOval(startPoint.x - radius, startPoint.y - radius, diameter, diameter);

            }
        } catch (InterruptedException e) {
        }
    }

    /**
     * Return whether the Bubble has popped and or left the screen.
     * 
     * @return whether the ball has completed its fall to the bottom
     */
    public boolean done() {
        return done;
    }

    /**
     * Sets the expanded Bubble loose on the screen.
     */
    public void isBubble() {
        this.expanded = true;
    }

    /**
     * Blows up the Bubble!
     */
    public void expandBubble() {
        size += 1;
    }

    /**
     * Sets up the program to pops the Bubble!
     */
    public void popBubble() {
        bColor = new Color(173, 216, 230, 0);
        ;
        popped = true;
    }

    // the run method is what runs in this object's thread for the
    // time it is "alive"
    @Override
    public void run() {

        // once the bubble object is created the bubble is blown up until the user
        // releases their mouse button.
        while (expanded == false) {
            try {
                sleep(10);
                expandBubble();
                panel.repaint();
            } catch (InterruptedException e) {
            }
        }

        // delay once the bubble done being blown up
        try {
            sleep(10);
        } catch (InterruptedException e) {
        }

        while (startPoint.y < panel.getHeight() || startPoint.y < panel.getWidth()) {

            // every 10 ms or so, we move the coordinates of the bubble
            // by a few pixel to emulate wind
            try {
                sleep(20);
                startPoint.translate((int) xSpeed, (int) ySpeed);

                // this emulates the bubbles movement in the air
                int checkNext = rand.nextInt(2);
                if (checkNext == 0) {
                    ySpeed += rand.nextDouble();
                    xSpeed += rand.nextDouble();
                } else if (checkNext == 1) {
                    ySpeed -= rand.nextDouble();
                    xSpeed -= rand.nextDouble();
                }

                // the random timer for when the bubble pops
                if (timer < popTimer) {
                    timer += 0.05;
                } else {
                    break;
                }
            } catch (InterruptedException e) {
            }

            panel.repaint();
        }

        popBubble();
        panel.repaint();
    }
}