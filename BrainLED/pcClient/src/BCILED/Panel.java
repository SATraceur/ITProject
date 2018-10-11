package BCILED;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Hardware Group
 */
public class Panel extends JPanel {

    private BufferedImage img;
    private Graphics2D g2;
    private HeatChart HC;

    /**
     *Construct the display panel
     * @param x
     * @param y
     */
    public Panel(int x, int y) {

        // Decided to leave sensor positions in here as they do not need to be dynamically modified by the user
        int xScale = 0, yScale = 0;
        Point[] sensorCoordinates = {
            new Point(5 + xScale, 0 + yScale), // AF3
            new Point(10 + xScale, 0 + yScale), // AF4
            new Point(4 + xScale, 1 + yScale), // F7
            new Point(6 + xScale, 1 + yScale), // F3
            new Point(9 + xScale, 1 + yScale), // F4
            new Point(11 + xScale, 1 + yScale), // F8
            new Point(5 + xScale, 2 + yScale), // FC5
            new Point(10 + xScale, 2 + yScale), // FC6
            new Point(4 + xScale, 3 + yScale), // T7
            new Point(11 + xScale, 3 + yScale), // T8
            new Point(5 + xScale, 5 + yScale), // P7
            new Point(10 + xScale, 5 + yScale), // P8
            new Point(6 + xScale, 7 + yScale), // O1
            new Point(9 + xScale, 7 + yScale) // O2
        };

        // Create new heatchart
        HC = new HeatChart(x, y, sensorCoordinates);
        // Default to 1x1 cells
        HC.setCellSize(new Dimension(1, 1));
    }

    

    /**
     * Changes cell size and updates matrix
     * @param d (2 dimensions)
     */
    public void setCellSize(Dimension d) {
        HC.setCellSize(d);
        this.update();
    }

   

    /**
     * Reset all matrix values to 0 and update sensor values
     * @param values
     */
    public void setHeatMapSensorValues(double[] values) {
        HC.refreshMatrix();
        HC.setSensorValues(values);
    }

    

    /**
     * Update all cells of the matrix based on the sensor values and reconstruct image
     */
    public void update() {
        HC.updateMatrix();
        this.draw();
        this.repaint();

        try {
            Thread.sleep(200); // TODO: Change this at some stage 
        } catch (InterruptedException ex) {
            ex.toString();
        }

    }

    /**
     * return map size
     * @return
     */
    public Dimension getMapSize() {
        return HC.getChartSize();
    }

    private void draw() {
        img = (BufferedImage) HC.getChartImage();
        g2 = (Graphics2D) img.getGraphics();
    }

    /**
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

}
