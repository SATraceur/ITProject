/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcclient;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
//import org.tc33.jheatchart.HeatChart;

/**
 *
 * @author Anonymous
 */
public class Panel extends JPanel {

    private BufferedImage img;
    private Graphics2D g2;
    private HeatChart HC;
    private Matrix m;
    private boolean running = false;

    public Panel() {
        Point[] sensorCoordinates = {
            new Point(3, 1),
            new Point(6+2, 1),
            new Point(2, 2),
            new Point(7+2, 2),
            new Point(3, 3),
            new Point(6+2, 3),
            new Point(1, 4),
            new Point(8+2, 4),
            new Point(2, 6),
            new Point(7+2, 6),
            new Point(3, 9),
            new Point(6+2, 9)};

        m = new Matrix(25, 25);

        // Declare sensor locations within LED matrix.  
        m.initSensors(sensorCoordinates);
        running = true;
    }

    public void start() {

        while (running) {
            update();
            draw();
            repaint();

            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                ex.toString();
            }

        }

    }

    public void update() {   
        m.refreshMatrix();
        m.updateMatrix();  
        // Have to update HeatChart by making a new one....
 //       HC.setZValues(m.returnData());
        
        HC = new HeatChart(m.returnData());
        HC.setCellSize(new Dimension(15, 15));
        HC.setHighValueColour(Color.RED);
        HC.setLowValueColour(Color.GREEN);
        HC.setColourScale(0.5);
        HC.setShowXAxisValues(false);
        HC.setShowYAxisValues(false);
        HC.setAxisColour(Color.WHITE);
    }

    public void draw() {
        img = (BufferedImage) HC.getChartImage();
        AffineTransform transform = new AffineTransform();
        // Need to rotate the image because...... reasons.....
        transform.rotate(3.14159 / 2, img.getWidth() / 2, img.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        img = op.filter(img, null);
        // But wait, there's more! Also need to flip that shit like a burger....
        img = flipHorizontally(img);
        g2 = (Graphics2D) img.getGraphics();
    }

    public static BufferedImage flipHorizontally(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = newImage.createGraphics();
        gg.drawImage(image, image.getHeight(), 0, -image.getWidth(), image.getHeight(), null);
        gg.dispose();
        return newImage;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

}

