import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Ethan Bartlett, Aaron McGuirk, Jason Macutek
 * @version Spring 2022
 */

class FractalSquares {

  // corner points
  private int x, y;
  protected int size;
  private int MIN;

  /**
   * Construct a new squre
   *
   * @param x the x of the top left corner of the base square
   * @param y the y of the tope left corner of the base square
   * @param size the length of one side
   */
  public FractalSquares(int x, int y, int size, int MIN) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.MIN = MIN;
  }

  /** This method is used to change the recurssion size limit when the checkbox is clicked */

  protected void setMin(int val){
    MIN = val;
  }

  /**
   * Draw the FractalSquare on the given Graphics object.
   *
   * @param g the Graphics object on which the Square should be drawn
   */
  public void paint(Graphics g) {
    drawSquare(x, y, size, g);
  }

  protected void drawSquare(int x, int y, int size, Graphics g) {
    g.fillRect(x, y, size, size);
    if (size < MIN) {} else {
      //do the calc for the small ones. Create all of the points for each smaller square

      //top top left
      drawSquare(x, y - (size / 3), size / 3, g);

      //side top left points
      drawSquare(x - (size / 3), y, size / 3, g);

      //top top right
      drawSquare(x + ((2 * size) / 3), y - (size / 3), size / 3, g);

      //top side right
      drawSquare(x + size, y, size / 3, g);

      //bot side right
      drawSquare(x + size, y + (2 * (size / 3)), size / 3, g);

      //bot bot right
      drawSquare(x + ((2 * size) / 3), y + size, size / 3, g);

      //bot bot left
      drawSquare(x, y + size, size / 3, g);

      //bot side left
      drawSquare(x - (size / 3), y + (2 * (size / 3)), size / 3, g);
    }
  }
}

/**
 * A program to draw squares on the screen.
 *
 * @author Ethan Bartlett, Aaron McGurik, Jason Macutek
 * @version Spring 2022
 */

public class Playground extends MouseAdapter implements Runnable{

  private JPanel panel;

  // parts of the outline triangle so far
  private Point pressPos;
  private Point currPos;
  private Point releasePos;
  private boolean direction = false;
  private int difference;
  private int MIN;
  private JPanel controlPanel;
  private JButton clear;
  private Color color;
  private Color recolorColor;
  private JComboBox colorBox;
  private ArrayList<FractalSquares> squares = new ArrayList<>();
  private ArrayList<Color> colors = new ArrayList<>();
  private boolean subsequentColor = false;
  private JCheckBox recolorAll;
  private boolean changeAll = false;
  private JSpinner recurLim;
  private JLabel recurTitle;
  private JCheckBox recurAll;

  public Playground() {
    this.MIN = 10;
    clear = new JButton("Clear");
    color = Color.BLACK;
    recolorColor = color;
    colorBox = new JComboBox();
    recolorAll = new JCheckBox("Recolor All?");
    recurLim = new JSpinner(new SpinnerNumberModel(10, 1, 200, 1));
    recurTitle = new JLabel("Recursion Limit?");
    recurAll = new JCheckBox("Change All?");
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
    JFrame frame = new JFrame("Playground");
    frame.setPreferredSize(new Dimension(800, 800));

    // tell the JFrame that when someone closes the
    // window, the application should terminate
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //adds color options to the combo box

    // JPanel with a paintComponent method
    panel =
      new JPanel(new BorderLayout()) {
        @Override
        public void paintComponent(Graphics g) {
          // first, we should call the paintComponent method we are
          // overriding in JPanel
          super.paintComponent(g);

          // draw square before the mouse is released
          if (direction == true) {
            //get the size
            if (pressPos.x - currPos.x < pressPos.y - currPos.y) {
              difference = currPos.x - pressPos.x;
            } else {
              difference = currPos.y - pressPos.y;
            }
            g.fillRect(pressPos.x, pressPos.y, difference, difference);
          }

          // draw the squares
          int i = 0;
          for (FractalSquares t : squares) {
            if (changeAll) {
              g.setColor(recolorColor);
            } else {
              Color c = colors.get(i);
              g.setColor(c);
              i++;
            }
            t.paint(g);
          }
        }
      };

    recurLim.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e){
            MIN = (int)recurLim.getValue();
        }
    }); 

    recurAll.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (recurAll.isSelected() == true) {
            for(FractalSquares t : squares){
              t.setMin(MIN);
            }
            panel.repaint();
          }
        }
      });
        

    colorBox.addItem("Black");
    colorBox.addItem("Blue");
    colorBox.addItem("Gray");
    colorBox.addItem("Magenta");
    colorBox.addItem("Orange");

    colorBox.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (colorBox.getSelectedItem().equals("Black")) {
            color = Color.BLACK;
            subsequentColor = true;
          } else if (colorBox.getSelectedItem().equals("Blue")) {
            color = Color.BLUE;
            subsequentColor = true;
          } else if (colorBox.getSelectedItem().equals("Gray")) {
            color = Color.GRAY;
            subsequentColor = true;
          } else if (colorBox.getSelectedItem().equals("Magenta")) {
            color = Color.MAGENTA;
          } else if (colorBox.getSelectedItem().equals("Orange")) {
            color = Color.ORANGE;
            subsequentColor = true;
          }
        }
      }
    );

    recolorAll.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (recolorAll.isSelected() == true) {
            changeAll = true;
            if (colorBox.getSelectedItem().equals("Black")) {
              recolorColor = Color.GRAY;
            } else if (colorBox.getSelectedItem().equals("Blue")) {
              recolorColor = Color.BLUE;
            } else if (colorBox.getSelectedItem().equals("Gray")) {
              recolorColor = Color.GRAY;
            } else if (colorBox.getSelectedItem().equals("Magenta")) {
              recolorColor = Color.MAGENTA;
            } else if (colorBox.getSelectedItem().equals("Orange")) {
              recolorColor = Color.ORANGE;
            }
          } else {
            changeAll = false;
          }
          panel.repaint();
        }
      }
    );



    clear.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          squares.clear();
          colors.clear();
          direction = false;
          panel.repaint();
        }
      }
    );

    controlPanel = new JPanel();
    panel.add(controlPanel, BorderLayout.SOUTH);
    controlPanel.add(clear, BorderLayout.SOUTH);
    controlPanel.add(colorBox, BorderLayout.SOUTH);
    controlPanel.add(recolorAll, BorderLayout.SOUTH);
    controlPanel.add(recurLim, BorderLayout.SOUTH);
    controlPanel.add(recurTitle, BorderLayout.SOUTH);
    controlPanel.add(recurAll, BorderLayout.SOUTH);

    frame.add(panel);
    panel.addMouseListener(this);
    panel.addMouseMotionListener(this);

    // display the window we've created
    frame.pack();
    frame.setVisible(true);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    //store the mouse pressed point
    pressPos = e.getPoint();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    //store the point where to mouse was released at
    int d;
    if (pressPos.x - currPos.x < pressPos.y - currPos.y) {
      d = currPos.x - pressPos.x;
    } else {
      d = currPos.y - pressPos.y;
    }

    squares.add(new FractalSquares(pressPos.x, pressPos.y, d, MIN));
    if (!subsequentColor) {
      colors.add(Color.BLACK);
    } else {
      colors.add(color);
    }
    panel.repaint();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    //store the point where to mouse was released at
    panel.repaint();
    currPos = e.getPoint();
    if (currPos.x < pressPos.x || currPos.y < pressPos.y) {
      direction = false;
    } else {
      direction = true;
    }
    panel.repaint();
  }

  public static void main(String args[]) {
    //int min = Integer.parseInt(args[0]);

    javax.swing.SwingUtilities.invokeLater(new Playground());
  }
}
