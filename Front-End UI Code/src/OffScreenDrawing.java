
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * OffScreenDrawing draws the grid for the MissionControlGUI GUI
 * @author Corey Short, Phuc Nguyen, Khoa Tran
 * 5/25/14
 */
public class OffScreenDrawing extends JPanel {

	/** Creates new form OffScreenDrawing */
	public OffScreenDrawing() {
		initComponents();
		setBackground(Color.black);
		System.out.println(" OffScreen Drawing constructor ");
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (offScreenImage == null)
		{
			makeImage();
		}
		g.drawImage(offScreenImage, 0, 0, this);  //Writes the Image to the screen
	}

	/**
	 * Create the offScreenImage, 
	 */
	public void makeImage() {
		System.out.println("OffScreenGrid  makeImage() called");
		imageWidth = getSize().width;// size from the panel
		imageHeight = getSize().height;
		yOrigin = imageHeight - 50;
		robotPrevX = xpixel(0);
		robotPrevY = ypixel(0);
		offScreenImage = createImage(imageWidth, imageHeight);// the container can make an image
		try {Thread.sleep(500);}
		catch(Exception e){};
		System.out.print("Off Screen Grid  create image ----- " );
		System.out.println( offScreenImage == null);
		if(offScreenImage == null)
		{
			//			System.out.println("Null image" );
			offScreenImage =  createImage(imageWidth, imageHeight);
		}
		osGraphics = (Graphics2D) offScreenImage.getGraphics();
		osGraphics.setColor(getBackground());
		osGraphics.fillRect(0, 0, imageWidth, imageHeight);// erase everything
		drawGrid();
	}

	/**
	 * Draws the grid for MissionControlGUI
	 */
	public void drawGrid() {
		int xmin = -292;
		int xmax = 278;
		int xSpacing = 30;
		int ymax = 238;
		int ySpacing = 30;
		int count = 0;
		osGraphics.setColor(Color.green); // Set the line color
		for (int y = 0; y <= ymax; y += ySpacing)
		{
			if (count == 1) {
				y = 12;
				osGraphics.drawLine(xpixel(xmin), ypixel(y), xpixel(xmax), ypixel(y));
				osGraphics.setColor(Color.green);
			}
			if (count == 8) {
				osGraphics.setColor(Color.red);
				BasicStroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT,
										BasicStroke .JOIN_BEVEL, 0, new float[]{9}, 0);
				osGraphics.setStroke(dashed);
				osGraphics.drawLine(xpixel(xmin), ypixel(y+16), xpixel(xmax), ypixel(y+16));
				osGraphics.setColor(Color.green);
				osGraphics.setStroke(new BasicStroke());
				
			}
			osGraphics.drawLine(xpixel(xmin), ypixel(y), xpixel(xmax), ypixel(y));//horizontal lines
			count++;
		}
		count = 0;
		for (int x = xmin; x <= xmax; x += xSpacing)
		{
			if (count == 10) {
				x = 0;
				osGraphics.setColor(Color.red);
				BasicStroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT,
										BasicStroke .JOIN_BEVEL, 0, new float[]{9}, 0);
				osGraphics.setStroke(dashed);
				osGraphics.drawLine(xpixel(x), ypixel(0), xpixel(x), ypixel(ymax));
				osGraphics.setColor(Color.green);
				osGraphics.setStroke(new BasicStroke());
				x=-22;
			}
			osGraphics.drawLine(xpixel(x), ypixel(0), xpixel(x), ypixel(ymax));// vertical lines
			count++;
		}
		count = 0;
		osGraphics.setColor(Color.white); //set number color 	
		for (int y = 0; y <= ymax; y += ySpacing) // number the  y axis
		{
			if (count == 1) {
				y = 12;
			}
			if (count == 8) {
				osGraphics.drawString(y+16 + "", xpixel(-305f), ypixel(y+16) + 4);
			}
			osGraphics.drawString(y + "", xpixel(-305f), ypixel(y) + 4);
			count++;
		}
		count = 0;
		for (int x = xmin; x <= xmax; x +=  xSpacing) // number the x axis
		{
			if (count == 10) {
				x = 0;
				osGraphics.drawString("Light Beacon", xpixel(x) - 35, ypixel(-18f));
				x = -22;
			}
			
			osGraphics.drawString(x + "", xpixel(x) - 4, ypixel(-8f));
			count++;
		}
	}

	/**
	 * Clears the screen and redraws a new grid.
	 */
	public void clear() {
		System.out.println(" clear called ");
		osGraphics.setColor(getBackground());
		osGraphics.fillRect(0, 0, imageWidth, imageHeight);// clear the image
		drawGrid();
		repaint();
	}

	/**
	 * Draws the bomb on the GUI after it has been detected and retrieved.
	 */
	public void drawBomb(int x, int y) {
		x = xpixel(x); // coordinates of intersection
		y = ypixel(y);
		osGraphics.setColor(Color.darkGray);
		osGraphics.fillOval(x, y, 6, 6);//bounding rectangle is 10 x 10
		repaint();
	}

	/**
	 * Draws the robot's path during any movement. It is also responsible for
	 * drawing the robots pose and painting over its previous pose as the same color
	 * as the background
	 * @param xx - the x-coordinate of the robot
	 * @param yy - the y-coordinate of the robot
	 * @param heading - the current heading of the robot
	 */
	public void drawRobotPath(int xx, int yy, int heading) {
		int x = xpixel(xx); // coordinates of intersection
		int y = ypixel(yy);
	
		drawGrid(); // for redrawing grid lines
		osGraphics.setColor(Color.blue);
		drawPose(robotPrevX, robotPrevY, robotPrevHeading, Color.black); // erases old pose
		repaint();
		drawPose(x, y, heading, Color.orange); // draws a new pose
		
		if (isRobotPathCalled) {
			osGraphics.drawLine(robotPrevX, robotPrevY, x, y); 
		}
		robotPrevX = x;
		robotPrevY = y;
		isRobotPathCalled = true;
		robotPrevHeading = heading;
		repaint();
	}

	/**
	 * Draws the pose of the robot as a triangle on the GUI.
	 * @param x - the x-coordinate of the robot
	 * @param y - the y-coordinate of the robot
	 * @param heading - the heading of the robot
	 * @param c - the color of the pose to be drawn
	 */
	public void drawPose(int x, int y, int heading, Color c) {
		poseTriangle = new Polygon();
		int newX;
		int newY;
		int radius;
		robotPrevHeading = heading;
		for (int i = 0; i < 3; i++) {
			if (i == 0) {
				radius = 10;
			}
			else {
				radius = 6;
			}
			newX = x + (int) (radius * Math.cos(Math.toRadians(heading + (120 * i))));
			newY = y - (int) (radius * Math.sin(Math.toRadians(heading + (120 * i))));

			poseTriangle.addPoint(newX, newY);
			System.out.println("Point " + i + ": (" + newX + "," + newY + ")");
		}
		osGraphics.setColor(c);
		osGraphics.drawPolygon(poseTriangle);
	}
	
	/**
	 * Draws a wall for the map left and map right methods.
	 * @param xx - the x-coordinate of the wall
	 * @param yy - the y-coordinate of the wall
	 * @param color - the color to draw the wall
	 */
	public void drawWall(int xx, int yy, Color color) {
		int x = xpixel(xx);
		int y = ypixel(yy);
		osGraphics.setColor(color);
		osGraphics.fillOval(x, y, 6, 6);
		repaint();
	}
	
	/**
	 * Draws the standard deviation as an ellipse around the pose, x, y.
	 * @param xx - current pose x
	 * @param yy - current pose y
	 * @param devX - current standard deviation of x
	 * @param devY - current standard deviation of y
	 */
	public void drawStdDev(int xx, int yy, int devX, int devY) {
		int x = xpixel(xx);
		int y = ypixel(yy);
		int sDevX = xpixel(devX);
		int sDevY = ypixel(devY);
		x += 2 * sDevX; // 2 standard deviations
		y += 2 * sDevY; // 2 standard deviations
		osGraphics.setColor(Color.red);
		osGraphics.drawOval(x, y, 6, 6);
	}
	
	
	/**
	 * Draws the crash of the robot on the GUI as a larger red oval
	 * @param xx - the x-coordinate of the crash
	 * @param yy - the y coordinate of the crash
	 */
	public void drawCrash(int xx, int yy) {
		int x = xpixel(xx);
		int y = ypixel(yy);
		osGraphics.setColor(Color.red);
		osGraphics.drawOval(x - 4, y - 4, 8, 8);
	}
	
	/**
	 * Paints the previous location clicked by the mouse on the GUI as the
	 * same color as the background.
	 */
	private void clearPreviousMouseClicked(int x, int y) {
		if(osGraphics == null)System.out.println("null osGraphics");
		osGraphics.setColor(Color.black);
		osGraphics.fillOval(x - 4, y - 4, 6, 6);
		drawGrid();
		repaint(); // redraws grid lines
	}
	
	/**
	 * Draws an oval of the current destination clicked on the GUI.
	 * @param x - the x-coordinate clicked on the GUI
	 * @param y - the y-coordinate clicked on the GUI
	 */
	public void drawMouseClicked(int x, int y) {
		x = xpixel(x);
		y = ypixel(y);
		osGraphics.setColor(Color.blue);
		osGraphics.fillOval(x - 4, y - 4, 6, 6);
		repaint();
	}
	
	public int abs(int a) {
		return (a < 0 ? (-a) : (a));
	}

	/**
	 * Converts float x to pixel x.
	 * Used by all drawing methods.
	 * @param x - float x passed in from the robot to be converted to a pixel that
	 * is represented on the GUI drawing.
	 * @return the xpixel to be drawn on the GUI
	 */
	private int xpixel(float x) {
		return xOrigin + (int) (x * gridSpacing);
	}
	
	/**
	 * Converts float y to pixel y.
	 * Used by all drawing methods.
	 * @param y - float y passed in from the robot to be converted to a pixel that
	 * is represented on the GUI drawing.
	 * @return the ypixel to be drawn on the GUI
	 */
	private int ypixel(float y) {
		return yOrigin - (int) (y * gridSpacing);
	}
	
	private int gridX(int xpix) {
		float x = (xpix - xOrigin)/(1.0f * gridSpacing);
		return Math.round(x);
	}
	
	private int gridY(int ypix) {
		float y = (yOrigin - ypix)/(1.0f * gridSpacing);
		return Math.round(y);
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				formMouseClicked(evt);
			}
		});
		
		FlowLayout layout = new FlowLayout();
		this.setLayout(layout);
	
		clearButton = new JButton("Clear");
		this.add(clearButton);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			clear();
			}
		});
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * Translates a click on the screen to a selection of destination in the 
	 * text fields.
	 * @param event - the event clicked on the GUI
	 */
	private void formMouseClicked(MouseEvent event)//GEN-FIRST:event_formMouseClicked
	{
		clearPreviousMouseClicked(xpixel(destXo), ypixel(destYo));

		destXo = gridX(event.getX());
		destYo = gridY(event.getY());

		textX.setText(destXo + "");
		textY.setText(destYo + "");
		drawMouseClicked(destXo, destYo);
		repaint();
	}//GEN-LAST:event_formMouseClicked

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JButton clearButton;
	// End of variables declaration//GEN-END:variables
	/**
	 *The robot path is drawn and updated on this object. <br>
	 *created by makeImage which is called by paint(); guarantees image always exists before used; 
	 */
	Image offScreenImage;
	/**
	 *width of the drawing area;set by makeImage,used by clearImage
	 */
	int imageWidth;
	/**
	 *height of the drawing are; set by  makeImage,used by clearImage
	 */
	int imageHeight;
	/** 
	 *the graphics context of the image; set by makeImage, used by all methods that draw on the image
	 */
	private Graphics2D osGraphics;
	/**
	 * y origin in pixels
	 */
	public int yOrigin;
	/**
	 * the line spacing of the GUI represented as pixels
	 */
	public final int gridSpacing = 2;
	/**
	 * origin in pixels from corner of drawing area
	 */
	public final int xOrigin = 650;
	/**
	 *robot position ; used by checkContinuity, drawRobotPath
	 */
	private int robotPrevX = xpixel(0);
	/**
	 * robot position; used by checkContinuity, drawRobotPath
	 */
	private int robotPrevY = ypixel(0);
	private int robotPrevHeading = 0;
	
	private int destXo = xpixel(0);
	private int destYo = ypixel(0);
	
	private Polygon poseTriangle = new Polygon();
	
	public JTextField textX;
	public JTextField textY;
	
	public boolean isRobotPathCalled;
	public boolean isDrawWallCalled;
	
}
