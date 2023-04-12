import java.awt.*;
import java.awt.event.*;
import java.nio.channels.ClosedByInterruptException;
import java.security.DrbgParameters.Reseed;
import java.util.*;
import java.util.concurrent.Delayed;

import javax.script.ScriptEngine;
import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.text.StyledEditorKit;

import org.w3c.dom.css.RGBColor;
import org.w3c.dom.events.Event;

/**
 * This program is supposed to create a game that is a mimic of ski ball but
 * more simplistic
 *
 * @author Aaron McGuirk, Ethan Bartlett
 * @version Spring 2022
 */
public class SkiBall extends MouseAdapter implements Runnable, ActionListener {

  // Instance variables/constants
  private JPanel gamePanel;
  private JPanel sideBar;
  private JPanel clearPage;

  private JLabel defMsg;
  private JLabel points;
  private JLabel shotsLeft;
  private JLabel totalPoints;
  private JLabel skiTitle;
  private JLabel highScore;

  private JButton startGame;
  private JButton restartGame;

  private int numPoints;
  private int numShotsLeft;
  private int numHighScore;

  private Target outer;
  private Target inner;
  private Target center;
  private Target small;

  private Balls defaultBalls;

  private Point click;
  private Point drag;
  private Point release;

  static final int LANE_WIDTH = 400;
  static final int LANE_HEIGHT = 800;
  static final int FOUL_WIDTH = 600;
  static final int FOUL_HEIGHT = 500;
  static final int POWER_LEVEL = 4;

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
    JFrame frame = new JFrame("SkiBall Game");
    frame.setPreferredSize(new Dimension(700, 800));

    // tell the JFrame that when someone closes the
    // window, the application should terminate
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // creates the openeing start game page
    startGame = new JButton("Start Game");
    startGame.setFont(new Font("MonoSpaced", Font.BOLD, 40));
    clearPage = new JPanel(new BorderLayout());
    clearPage.setSize(700, 800);
    clearPage.setBackground(Color.WHITE);
    clearPage.add(startGame, BorderLayout.CENTER);
    frame.add(clearPage);

    // sets the counters for the game
    numPoints = 0;
    numShotsLeft = 10;
    numHighScore = 0;

    // creates the Target rings with their respected points and colors
    outer = new Target(200, 200, 100, 10, new Color(179, 25, 66));
    inner = new Target(200, 200, 60, 20, new Color(255, 255, 255));
    center = new Target(200, 200, 25, 30, new Color(10, 49, 54));
    small = new Target(200, 120, 20, 50, new Color(218, 165, 32));

    // generates the balls for the game
    defaultBalls = new Balls();

    // creates the instances for the mouse interactions
    click = null;
    drag = null;
    release = null;

    // creates the game board and paints the targets and the respected plays
    gamePanel = new JPanel() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // if the player has no shots left to end the game
        if (numShotsLeft > 0) {

          // paints the Target rings
          outer.paint(g);
          inner.paint(g);
          center.paint(g);
          small.paint(g);

          // tracks the users mouse press and hold
          if (click != null && release == null) {
            g.setColor(Color.BLUE);
            g.fillOval(drag.x, drag.y, 8, 8);
          }

          // paints the players ball shot on the board
          if (release != null) {
            g.setColor(Color.BLUE);
            g.fillOval(drag.x, drag.y, 8, 8);

            g.setColor(Color.BLACK);
            g.fillOval(release.x, release.y, 15, 15);

            // throws the ball on the board
            defaultBalls.paint(g);
          }
        } else {

          // if the game is over the player can try again or exit
          gamePanel.setVisible(false);
          JOptionPane.showMessageDialog(frame, "Game Over! You scored " + numPoints + " points!");
          restartGame.setText("Try Again");
        }
      }
    };

    // sets the respected ski ball game size and formats the board
    gamePanel.setSize(LANE_WIDTH, LANE_HEIGHT);
    gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

    // creates the scoring and message panel
    sideBar = new JPanel();
    sideBar.setSize(300, 500);
    sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));

    // gives the window more information such as active scores and number of throws
    // left
    // as well as a reset button to start again
    skiTitle = new JLabel("Ski Ball!");
    skiTitle.setFont(new Font("MonoSpaced", Font.BOLD, 36));
    defMsg = new JLabel("On the board.");
    defMsg.setFont(new Font("SansSerif", Font.BOLD, 17));
    points = new JLabel("0 points");
    points.setFont(new Font("SansSerif", Font.BOLD, 16));
    shotsLeft = new JLabel("Shots left: " + numShotsLeft + "");
    shotsLeft.setFont(new Font("SansSerif", Font.BOLD, 17));
    totalPoints = new JLabel("Total: " + numPoints + "");
    totalPoints.setFont(new Font("SansSerif", Font.BOLD, 17));
    restartGame = new JButton("Restart Game");
    restartGame.setFont(new Font("SansSerif", Font.BOLD, 18));

    highScore = new JLabel("Highest Score: " + numHighScore + " ");
    highScore.setFont(new Font("SansSerif", Font.BOLD, 17));

    // adds the features to the scoring panel
    sideBar.add(skiTitle);
    sideBar.add(defMsg);
    sideBar.add(points);
    sideBar.add(shotsLeft);
    sideBar.add(totalPoints);
    sideBar.add(highScore);
    sideBar.add(restartGame);

    // this allows the window to be organized propertly
    frame.setLayout(null);

    // adds the panels to the frame in their respectful spot
    frame.add(gamePanel);
    gamePanel.setLocation(0, 0);

    frame.add(sideBar);
    sideBar.setLocation(420, 10);

    // creates the listeners for the program
    gamePanel.addMouseListener(this);
    gamePanel.addMouseMotionListener(this);
    startGame.addActionListener(this);
    restartGame.addActionListener(this);

    // locks the game until the user presses Start Game
    gamePanel.setVisible(false);
    sideBar.setVisible(false);

    // display the window we've created
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * This method is a listener for the Start Game and Restart Game
   * button's which give functionality.
   * 
   * @param e the ActionEvent listener.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == startGame) {
      clearPage.setVisible(false);
      gamePanel.setVisible(true);
      sideBar.setVisible(true);
    }

    // resets the game from scratch
    if (e.getSource() == restartGame) {

      // checks to see if there is a new high score
      if (numPoints > numHighScore){
        numHighScore = numPoints;
        highScore.setText("Highest Score: " + numHighScore + " ");
      }

      click = null;
      drag = null;
      release = null;
      numPoints = 0;
      numShotsLeft = 10;
      defMsg.setText("On the board.");
      points.setText("0 points");
      shotsLeft.setText("Shots left: " + numShotsLeft + "");
      totalPoints.setText("Total: " + numPoints + "");
      restartGame.setText("Restart Game");
      gamePanel.setVisible(true);
      gamePanel.repaint();
    }
  }

  /**
   * This method records the players mouse hold and drag when shoot
   * the ball.
   * 
   * @param e the MouseEvent listener.
   */
  @Override
  public void mouseDragged(MouseEvent e) {
    drag = e.getPoint();

    // makes the user is within the foul line
    if (drag.y >= FOUL_HEIGHT) {
      drag = e.getPoint();
      gamePanel.repaint();
    } else {
      drag = null;
    }
  }

  /**
   * This method records the players first mouse press for their
   * current shoot.
   * 
   * @param e the MouseEvent listener.
   */
  @Override
  public void mousePressed(MouseEvent e) {
    click = e.getPoint();

    // makes the user is within the foul line
    if (click.y >= FOUL_HEIGHT) {
      click = e.getPoint();
    } else {
      click = null;
    }
  }

  /**
   * This method records the players mouse release for their
   * current shoot.
   * 
   * @param e the MouseEvent listener.
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    release = e.getPoint();

    // makes the user is within the foul line
    if (release.y >= FOUL_HEIGHT) {
      gamePanel.repaint();
    } else {
      release = null;
    }
  }

  public static void main(String args[]) {
    // The main method is responsible for creating a thread (more
    // about those later) that will construct and show the graphical
    // user interface.
    javax.swing.SwingUtilities.invokeLater(new SkiBall());
  }

  /*
   * This class maintains and controls the creation and structure
   * of the Bullseye in the Ski Ball game.
   */
  class Target {
    private int x = 0;
    private int y = 0;
    private int radius = 0;
    private int points = 0;
    private Color sColor = null;

    /**
     * Constructor for each Bullseye ring.
     * 
     * @param x      the X position on the game board.
     * @param y      the Y position on the game board.
     * @param radius the radius of the ring on the game board.
     * @param points the number of points the ring is worth.
     * @param sColor the color of the ring on the game board.
     */
    public Target(int x, int y, int radius, int points, Color sColor) {
      this.x = x;
      this.y = y;
      this.radius = radius;
      this.points = points;
      this.sColor = sColor;
    }

    /**
     * Gets the Targets radius
     * 
     * @return an Integer representing the radius.
     */
    public int getRadius() {
      return this.radius;
    }

    /**
     * Gets the Targets points
     * 
     * @return an Integer representing the points.
     */
    public int getPoints() {
      return this.points;
    }

    /**
     * This method draws the rings perfectly centered on the game board
     * according to the (X,Y) coordinate it is given.
     * 
     * @param g      the Graphics object being painted.
     * @param x      the X position on the game board.
     * @param y      the Y position on the game board.
     * @param radius the radius of the ring on the game board.
     */
    public void drawCenteredCircle(Graphics g, int x, int y, int radius) {
      int diameter = radius * 2;
      g.setColor(sColor);
      g.fillOval(x - radius, y - radius, diameter, diameter);
      g.setColor(Color.BLACK);
      g.drawOval(x - radius, y - radius, diameter, diameter);
    }

    /**
     * This is the paint method that draws the Bullseye rings on the game board.
     * 
     * @param g the Graphics object being painted.
     */
    public void paint(Graphics g) {
      drawCenteredCircle(g, x, y, radius);
      g.setColor(Color.BLACK);

      // draws the game boundary box
      g.draw3DRect(0, 0, LANE_WIDTH - 1, LANE_HEIGHT - 1, false);

      // draws the foul line
      g.drawLine(0, FOUL_HEIGHT, FOUL_WIDTH, FOUL_HEIGHT);
    }
  }

  /*
   * This class controls the balls being shot onto the game board.
   */
  class Balls {

    /**
     * This method shoots the balls onto the Ski Ball game board.
     * @param g the Graphics object being painted.
     * @param x the X position on the game board.
     * @param y the Y position on the game board.
     * @param radius the radius of the ring on the game board.
     */
    public void shootBall(Graphics g, int x, int y, int radius) {
      int diameter = radius * 2;
      g.setColor(new Color(211, 211, 211));
      g.fillOval(x - radius, y - radius, diameter, diameter);
      g.setColor(Color.BLACK);
      g.drawOval(x - radius, y - radius, diameter, diameter);
    }

    /**
     * This method paints the balls onto the Ski Ball game board according
     *  to the players shot. Which is calculated from two of the points where
     *  they clicked and dragged to release the shot.
     * @param g the Graphics object being painted.
     */
    public void paint(Graphics g) {
      // this gets the change in X and Y according to the first click and the release point
      int dx = POWER_LEVEL * (release.x - click.x);
      int dy = POWER_LEVEL * (release.y - click.y);

      // this calls the shoot ball method according to the calculated trajectory
      shootBall(g, dx, dy, 10);

      int scoredPoints = 0;

      // if the ball lands in the 50 point hole
      int sX = Math.abs(dx - 200);
      int sY = Math.abs(dy - 120);
      int sRad = small.getRadius();

      if (Math.pow(sX, 2) + Math.pow(sY, 2) <= Math.pow(sRad, 2)) {
        scoredPoints = small.getPoints();
        numPoints = numPoints + small.getPoints();
      } else {

        // if the ball lands in the center zone
        int aX = Math.abs(dx - 200);
        int aY = Math.abs(dy - 200);

        int rad = center.getRadius();
        if (Math.pow(aX, 2) + Math.pow(aY, 2) <= Math.pow(rad, 2)) {
          scoredPoints = center.getPoints();
          numPoints = numPoints + center.getPoints();
        } else {

          // if the ball lands in the inner zone
          rad = inner.getRadius();
          if (Math.pow(aX, 2) + Math.pow(aY, 2) <= Math.pow(rad, 2)) {
            scoredPoints = inner.getPoints();
            numPoints = numPoints + inner.getPoints();
          } else {

            // if the ball lands in the outer zone
            rad = outer.getRadius();
            if (Math.pow(aX, 2) + Math.pow(aY, 2) <= Math.pow(rad, 2)) {
              scoredPoints = outer.getPoints();
              numPoints = numPoints + outer.getPoints();
            }
          }
        }
      }

      // negates each shot the player takes
      numShotsLeft--;

      // prompts the user if they made it on the Bullseye or missed.
      if (scoredPoints != 0) {
        points.setText("Nice shot! You scored " + scoredPoints + " points!");
      } else {
        points.setText("Missed. Try again");
      }

      // checks to see if the high score has been beat
      if (numPoints > numHighScore){
        numHighScore = numPoints;
        highScore.setText("Highest Score: " + numHighScore + " ");
      }

      // updates the score panel and sets the player up for a new shot
      shotsLeft.setText("Shots left: " + numShotsLeft + "");
      totalPoints.setText("Total: " + numPoints + "");
      click = null;
      drag = null;
      release = null;
    }
  }
}
