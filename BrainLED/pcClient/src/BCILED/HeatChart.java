/*  
 *  Copyright 2010 Tom Castle (www.tc33.org)
 *  Licensed under GNU Lesser General Public License
 * 
 *  This file is part of JHeatChart - the heat maps charting api for Java.
 *
 *  JHeatChart is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published 
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This code has been HEAVILY modified from the original JHeatChart class
 *  Changes made include:
    - Removed axis titles, margins and unnessecary getter/setter methods
    - Added an internal matrix structure used to hold information relevant to visualising data
      from an EMOTIVE EPOC+ EEG headset.
    - Completely changed the heatmap scheme to use a multicolour gradient
    - Added capability to drive a strip of APA102 LED's

 */
package BCILED;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author SATraceur
 */
public class HeatChart {

    // Dimensions used for drawing the chart.
    private Dimension cellSize;
    private Dimension chartSize;
    private int x, y;
    // List containing the 14 sensors for the EPOC+.
    private LinkedList<Point> matrixSensorLocations;
    // 2D array to hold all matrix objects containing point data and sensor status.
    private MatrixObject[][] matrixObjects;
    private LEDdriver LED;
    
    /**
     * Constructor for HeatChart - Initializes cell dimensions and adds sensors to the matrix.
     * @param xDimension - Cell X dimension
     * @param yDimension - Cell Y dimension
     * @param sensorLocations - Array of points for sensor locations
     */
    public HeatChart(int xDimension, int yDimension, Point[] sensorLocations) {

        // Define dimensions of a single cell
        this.cellSize = new Dimension(20, 20);

        // Create new matrix
        this.x = xDimension; // # cells in x direction
        this.y = yDimension; // # cells in y direction
        this.matrixSensorLocations = new LinkedList<>();
        this.matrixObjects = new MatrixObject[this.x][this.y];

        // Copy user specified array of sensor locations into linked list
        for (Point p : sensorLocations) {
            matrixSensorLocations.add(new Point(p.x, p.y));
        }
        
        this.LED = new LEDdriver();
        
        // Initilise matrix with default values of 0
        this.refreshMatrix();
    }

    /**
     * Sets sensor values to values specified within list provided by the user.
     * @param values - Array of values to be assigned to the sensors.
     */
    public void setSensorValues(double[] values) {
        int i = 0;
        for (Point P : matrixSensorLocations) {
            matrixObjects[P.x][P.y].value = values[i++];
        }
    }

    /**
     * Sets cell size within matrix to user specified dimensions.
     * @param cellSize
     */
    public void setCellSize(Dimension cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Calculates size of the image to be used based on matrix and cell dimensions then
     * generates a chart and attaches the HeatMap image.
     * 
     * LED matrix is a 16 x 9 matrix with the following configuration
     *           ______________________________________________________________________             
     *  Din --->| 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 |---           
     *          |______________________________________________________________________|   |          
     *      ----|                                                                      |<---          
     *     |    |______________________________________________________________________|              
     *      --->|                                                                      |----> Dout    
     *          |______________________________________________________________________|             

     * As data is fed back in from the same side as it exits, the data for every odd row needs
     * to be flipped to be displayed correctly on the LED matrix.
     * 
     * @return chartImage - Image of HeatMap 
     */
    public Image getChartImage() {
        ArrayList<Color> LEDmatrixList = new ArrayList<>();
        ArrayList<Color> tempMatrix = new ArrayList<>();
        Color tempColor;
        
        // Calculate size of chart.
        chartSize = new Dimension(matrixObjects.length * cellSize.width, matrixObjects[0].length * cellSize.height);
        // Create image which we will eventually draw everything on.
        BufferedImage chartImage = new BufferedImage(chartSize.width, chartSize.height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D chartGraphics = chartImage.createGraphics();      
        // Create the heatmap image.
        BufferedImage heatMapImage = new BufferedImage(chartSize.width, chartSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D heatMapGraphics = heatMapImage.createGraphics();
        // Use anti-aliasing where ever possible.
        chartGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw each cell of the matrix onto the heat map image (row by row).
        for (int y = 0; y < this.y; y++) {
            
            // Refresh list as we only want each row by itself for later modification.
            tempMatrix.clear();
            for (int x = 0; x < this.x; x++) {       
                
                // Store each color from this particular row.
                tempColor = getHeatMapColor((float) matrixObjects[x][y].value);          
                tempMatrix.add(tempColor);
                // Set colour depending on normalised MatrixObject values.
                heatMapGraphics.setColor(tempColor);
                heatMapGraphics.fillRect(x * cellSize.width, y * cellSize.height, cellSize.width, cellSize.height);
            }
            
            // Flip every odd row.
            if(y%2 != 0) {
                Collections.reverse(tempMatrix);
            }
            // Append each row of LED's (odd rows flipped) to the final list.
            LEDmatrixList.addAll(tempMatrix);       
        }
        
        // Drive the LED's
        this.LED.write(LEDmatrixList, this.x * this.y);      
        // Draw the heat map onto the chart.
        chartGraphics.drawImage(heatMapImage, 0, 0, chartSize.width, chartSize.height, null);
        
        return chartImage;
    }

    /**
     * Returns dimension of chart containing the HeatMap.
     * @return chartSize  
     */
    public Dimension getChartSize() {
        return this.chartSize;
    }

    /**
     * Based on code from: http://www.andrewnoske.com/wiki/Code_-_heatmaps_and_color_gradients
     * @param value - Value to be mapped onto 7-color gradient
     * @return Color depending on where the supplied value lies in the color gradient
     */
    private Color getHeatMapColor(float value) {

         // <editor-fold defaultstate="collapsed" desc="Color LUT">
        float color[][] = {
            {0, 0, 0}, // black
            {0.05f, 0, 0.05f},
            {0.1f, 0, 0.1f},
            {0.15f, 0, 0.15f},
            {0.2f, 0, 0.2f},
            {0.25f, 0, 0.25f},
            {0.3f, 0, 0.3f},
            {0.35f, 0, 0.35f},
            {0.4f, 0, 0.4f},
            {0.45f, 0, 0.45f},
            {0.5f, 0, 0.5f},
            {0.55f, 0, 0.55f},
            {0.6f, 0, 0.6f},
            {0.65f, 0, 0.65f},
            {0.7f, 0, 0.7f},
            {0.75f, 0, 0.75f},
            {0.8f, 0, 0.8f},
            {0.85f, 0, 0.85f},
            {0.9f, 0, 0.9f},
            {0.95f, 0, 0.95f},
            {1, 0, 1}, // purple
            {0.95f, 0, 1},
            {0.9f, 0, 1},
            {0.85f, 0, 1},
            {0.8f, 0, 1},
            {0.75f, 0, 1},
            {0.7f, 0, 1},
            {0.65f, 0, 1},
            {0.6f, 0, 1},
            {0.55f, 0, 1},
            {0.5f, 0, 1},
            {0.45f, 0, 1},
            {0.4f, 0, 1},
            {0.35f, 0, 1},
            {0.3f, 0, 1},
            {0.25f, 0, 1},
            {0.2f, 0, 1},
            {0.15f, 0, 1},
            {0.1f, 0, 1},
            {0.05f, 0, 1},
            {0, 0, 1}, // blue
            {0, 0.05f, 1},
            {0, 0.1f, 1},
            {0, 0.15f, 1},
            {0, 0.2f, 1},
            {0, 0.25f, 1},
            {0, 0.3f, 1},
            {0, 0.35f, 1},
            {0, 0.4f, 1},
            {0, 0.45f, 1},
            {0, 0.5f, 1},
            {0, 0.55f, 1},
            {0, 0.6f, 1},
            {0, 0.65f, 1},
            {0, 0.7f, 1},
            {0, 0.75f, 1},
            {0, 0.8f, 1},
            {0, 0.85f, 1},
            {0, 0.9f, 1},
            {0, 0.95f, 1},
            {0, 1, 1}, // cyan
            {0, 1, 0.95f},
            {0, 1, 0.9f},
            {0, 1, 0.85f},
            {0, 1, 0.8f},
            {0, 1, 0.75f},
            {0, 1, 0.7f},
            {0, 1, 0.65f},
            {0, 1, 0.6f},
            {0, 1, 0.55f},
            {0, 1, 0.5f},
            {0, 1, 0.45f},
            {0, 1, 0.4f},
            {0, 1, 0.35f},
            {0, 1, 0.3f},
            {0, 1, 0.25f},
            {0, 1, 0.2f},
            {0, 1, 0.15f},
            {0, 1, 0.1f},
            {0, 1, 0.05f},
            {0, 1, 0}, // green
            {0.05f, 1, 0},
            {0.1f, 1, 0},
            {0.15f, 1, 0},
            {0.2f, 1, 0},
            {0.25f, 1, 0},
            {0.3f, 1, 0},
            {0.35f, 1, 0},
            {0.4f, 1, 0},
            {0.45f, 1, 0},
            {0.5f, 1, 0},
            {0.55f, 1, 0},
            {0.6f, 1, 0},
            {0.75f, 1, 0},
            {0.7f, 1, 0},
            {0.75f, 1, 0},
            {0.8f, 1, 0},
            {0.85f, 1, 0},
            {0.9f, 1, 0},
            {0.95f, 1, 0},
            {1, 1, 0}, // yellow
            {1, 0.98f, 0},
            {1, 0.96f, 0},
            {1, 0.94f, 0},
            {1, 0.92f, 0},
            {1, 0.9f, 0},
            {1, 0.88f, 0},
            {1, 0.86f, 0},
            {1, 0.84f, 0},
            {1, 0.82f, 0},
            {1, 0.8f, 0},
            {1, 0.78f, 0},
            {1, 0.76f, 0},
            {1, 0.74f, 0},
            {1, 0.72f, 0},
            {1, 0.7f, 0},
            {1, 0.68f, 0},
            {1, 0.66f, 0},
            {1, 0.64f, 0},
            {1, 0.62f, 0},
            {1, 0.6f, 0},
            {1, 0.58f, 0},
            {1, 0.56f, 0},
            {1, 0.54f, 0},
            {1, 0.52f, 0},
            {1, 0.5f, 0},
            {1, 0.48f, 0},
            {1, 0.46f, 0},
            {1, 0.44f, 0},
            {1, 0.42f, 0},
            {1, 0.4f, 0},
            {1, 0.38f, 0},
            {1, 0.36f, 0},
            {1, 0.34f, 0},
            {1, 0.32f, 0},
            {1, 0.3f, 0},
            {1, 0.28f, 0},
            {1, 0.26f, 0},
            {1, 0.24f, 0},
            {1, 0.22f, 0},
            {1, 0.2f, 0},
            {1, 0.18f, 0},
            {1, 0.16f, 0},
            {1, 0.14f, 0},
            {1, 0.12f, 0},
            {1, 0.1f, 0},
            {1, 0.08f, 0},
            {1, 0.06f, 0},
            {1, 0.04f, 0},
            {1, 0.02f, 0},
            {1, 0, 0}}; //red
        // </editor-fold>

        float fractBetween = 0;
        int NUM_COLORS = color.length, idx1, idx2;

        if (value <= 0) {
            idx1 = idx2 = 0;
        } else if (value >= 1) {
            idx1 = idx2 = NUM_COLORS - 1;
        } else {
            value *= (NUM_COLORS - 1);
            idx1 = (int) Math.floor(value);          // The desired color will be after this index,
            idx2 = idx1 + 1;                         // and before this index (inclusive).
            fractBetween = value - (float) idx1;     // Distance between the two indexes (0-1).
        }

        float red = (color[idx2][0] - color[idx1][0]) * fractBetween + color[idx1][0];
        float green = (color[idx2][1] - color[idx1][1]) * fractBetween + color[idx1][1];
        float blue = (color[idx2][2] - color[idx1][2]) * fractBetween + color[idx1][2];

        return new Color(red, green, blue);
    }

    /**
     * Refreshes the matrix with new MatrixObjects with no data.
     * Also declares whether the objects are a sensor or not.
     */
    public void refreshMatrix() {
        for (int y = 0; y < this.y; y++) {
            for (int x = 0; x < this.x; x++) {
                this.matrixObjects[x][y] = new MatrixObject(new Point(x, y), 0.0);
                matrixObjects[x][y].isSensor = matrixSensorLocations.contains(new Point(x, y));
            }
        }
    }

    /**
     * Returns the values of the MatrixObjects as a 2D array.
     * @return matrixData 
     */
    public double[][] returnData() {
        double[][] matrixData = new double[x][y];
        for (int y = 0; y < this.y; y++) {
            for (int x = 0; x < this.x; x++) {
                matrixData[x][y] = matrixObjects[x][y].value;
            }
        }
        return matrixData;
    }

    /**
     * Accepts a point p and generates a list of cells that are sensors.
     * @param p - matrix cell in question.
     * @return LinkedList of neighbour sensor cells.
     */
    public LinkedList<MatrixObject> findNeighbourSensors(Point p) {

        LinkedList<MatrixObject> neighbours = findAllNeighbours(p);

        // Remove neighbours from list that are not sensors.
        Iterator<MatrixObject> iterator = neighbours.iterator();
        while (iterator.hasNext()) {
            MatrixObject obj = iterator.next();
            if (!obj.isSensor) {
                iterator.remove();
            }
        }
        return neighbours;
    }

    /**
     * Generates a list of neighbour cells for a provided point p.
     * Checks if current cell is on the edge of the matrix and only adds neighbour. 
     * cells within bounds of the matrix to avoid out of bounds exceptions.
     * @param p - matrix cell in question.
     * @return LinkedList of neighbour cells within bounds of the matrix.
     */
    public LinkedList<MatrixObject> findAllNeighbours(Point p) {
        LinkedList<MatrixObject> neighbours = new LinkedList();

        if (isTopBounded(p) && isLeftBounded(p)) {
            neighbours.add(this.matrixObjects[p.x + 1][p.y]); // East
            neighbours.add(this.matrixObjects[p.x + 1][p.y + 1]); // South-East
            neighbours.add(this.matrixObjects[p.x][p.y + 1]); // South
        } else if (isBottomBounded(p) && isLeftBounded(p)) {
            neighbours.add(this.matrixObjects[p.x][p.y - 1]); // North
            neighbours.add(this.matrixObjects[p.x + 1][p.y - 1]); // North-East
            neighbours.add(this.matrixObjects[p.x + 1][p.y]); // East
        } else if (isBottomBounded(p) && isRightBounded(p)) {
            neighbours.add(this.matrixObjects[p.x][p.y - 1]); // North
            neighbours.add(this.matrixObjects[p.x - 1][p.y - 1]); // North-West
            neighbours.add(this.matrixObjects[p.x - 1][p.y]); // West
        } else if (isTopBounded(p) && isRightBounded(p)) {
            neighbours.add(this.matrixObjects[p.x][p.y + 1]); // South
            neighbours.add(this.matrixObjects[p.x - 1][p.y + 1]); // South-West
            neighbours.add(this.matrixObjects[p.x - 1][p.y]); // West
        } else if (isTopBounded(p)) {
            neighbours.add(this.matrixObjects[p.x + 1][p.y]); // East
            neighbours.add(this.matrixObjects[p.x + 1][p.y + 1]); // South-East
            neighbours.add(this.matrixObjects[p.x][p.y + 1]); // South
            neighbours.add(this.matrixObjects[p.x - 1][p.y + 1]); // South-West
            neighbours.add(this.matrixObjects[p.x - 1][p.y]); // West
        } else if (isLeftBounded(p)) {
            neighbours.add(this.matrixObjects[p.x][p.y - 1]); // North
            neighbours.add(this.matrixObjects[p.x + 1][p.y - 1]); // North-East
            neighbours.add(this.matrixObjects[p.x + 1][p.y]); // East
            neighbours.add(this.matrixObjects[p.x + 1][p.y + 1]); // South-East
            neighbours.add(this.matrixObjects[p.x][p.y + 1]); // South
        } else if (isBottomBounded(p)) {
            neighbours.add(this.matrixObjects[p.x - 1][p.y]); // West
            neighbours.add(this.matrixObjects[p.x - 1][p.y - 1]); // North-West
            neighbours.add(this.matrixObjects[p.x][p.y - 1]); // North
            neighbours.add(this.matrixObjects[p.x + 1][p.y - 1]); // North-East
            neighbours.add(this.matrixObjects[p.x + 1][p.y]); // East
        } else if (isRightBounded(p)) {
            neighbours.add(this.matrixObjects[p.x][p.y - 1]); // North
            neighbours.add(this.matrixObjects[p.x - 1][p.y - 1]); // North-West
            neighbours.add(this.matrixObjects[p.x - 1][p.y]); // West
            neighbours.add(this.matrixObjects[p.x - 1][p.y + 1]); // South-West
            neighbours.add(this.matrixObjects[p.x][p.y + 1]); // South
        } else {
            neighbours.add(this.matrixObjects[p.x + 1][p.y]); // East
            neighbours.add(this.matrixObjects[p.x + 1][p.y + 1]); // South-East
            neighbours.add(this.matrixObjects[p.x][p.y + 1]); // South
            neighbours.add(this.matrixObjects[p.x - 1][p.y + 1]); // South-West
            neighbours.add(this.matrixObjects[p.x - 1][p.y]); // West
            neighbours.add(this.matrixObjects[p.x - 1][p.y - 1]); // North-West
            neighbours.add(this.matrixObjects[p.x][p.y - 1]); // North
            neighbours.add(this.matrixObjects[p.x + 1][p.y - 1]); // North-East  
        }

        return neighbours;
    }

    /**
     * Returns true if the given point p coincides next to a sensor.
     * @param p - matrix cell in question.
     * @return boolean value.
     */
    public boolean hasNeighbourSensor(Point p) {
        LinkedList<MatrixObject> list = this.findNeighbourSensors(p);
        return !list.isEmpty();
    }

    private boolean isTopBounded(Point p) {
        return p.y == 0;
    }

    private boolean isBottomBounded(Point p) {
        return p.y == this.y - 1;
    }

    private boolean isLeftBounded(Point p) {
        return p.x == 0;
    }

    private boolean isRightBounded(Point p) {
        return p.x == this.x - 1;
    }

    /**
     * Iterates over the matrix and updates matrix cell values based on neighbour cell values.
     * Executes multiple iterations updating a temporary matrix before actually applying changes to
     * the actual matrix in order to allow changes to propogate to the edges of the matrix.
     * # iterations == largest matrix dimension to ensure gradient propogates across entire matrix in extreme cases.
     */
    public void updateMatrix() {
        LinkedList<MatrixObject> allNeighbours, sensorNeighbours;
        MatrixObject[][] temp = this.matrixObjects;
        int maxDimension = this.x > this.y ? this.x : this.y;

        for (int iterations = 0; iterations < maxDimension; iterations++) {
            boolean lastIteration = (iterations == maxDimension - 1 ? true : false);

            for (int y = 0; y < this.y; y++) {
                for (int x = 0; x < this.x; x++) {
                    if (!this.matrixObjects[x][y].isSensor) {

                        allNeighbours = this.findAllNeighbours(new Point(x, y));
                        sensorNeighbours = this.findNeighbourSensors(new Point(x, y));

                        switch (sensorNeighbours.size()) {
                            case 0: // No neighbour sensors
                                if (!lastIteration) {
                                    temp[x][y].value = this.avgValue(allNeighbours);
                                }
                                break;
                            case 1:
                                if (!lastIteration) {
                                    temp[x][y].value = (this.avgValue(sensorNeighbours) * 0.97) + (this.avgValue(allNeighbours) * 0.02);
                                } else {
                                    this.avgValue(allNeighbours);
                                }
                                break;
                            case 2:
                                if (!lastIteration) {
                                    temp[x][y].value = this.avgValue(sensorNeighbours) * 0.99;
                                } else {
                                    this.avgValue(allNeighbours);
                                }
                                break;
                            default: // More than 2 neighbour sensors
                                temp[x][y].value = this.avgValue(sensorNeighbours);
                                break;
                        }
                    }
                }
            }
        }
        matrixObjects = temp;
    }
    
    /**
     * Calculate color of a specific cell based on neighbour values.
     * @param neighbours - list of neighbour cells.
     * @return Average value of neighbour cells.
     */
    public double avgValue(LinkedList<MatrixObject> neighbours) {
        double val = 0;
        for (MatrixObject obj : neighbours) {
            val += obj.value;
        }
        return val / (double) neighbours.size();
    }

}
