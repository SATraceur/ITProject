/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcclient;



import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Anonymous
 */
public class Matrix {

    private LinkedList<Point> matrixSensorLocations;
    private MatrixObject[][] matrixObjects;
    private int x, y;
    private final double MAX_INTENSITY = 200;

    public Matrix(int xDimension, int yDimension) {
        this.x = xDimension;
        this.y = yDimension;
        this.matrixSensorLocations = new LinkedList<Point>();
        this.matrixObjects = new MatrixObject[this.x][this.y];
        this.refreshMatrix();
    }

    public void refreshMatrix() {
        for (int y = 0; y < this.y; y++) {
            for (int x = 0; x < this.x; x++) {
                this.matrixObjects[x][y] = new MatrixObject(new Point(x, y), 0.0);
                // Declare whether the object is a sensor or not
                matrixObjects[x][y].isSensor = matrixSensorLocations.contains(new Point(x, y));
            }
        }
    }

    public void initSensors(Point[] sensorLocations) {

        // Copy user specified array of sensor locations into linked list
        for (Point p : sensorLocations) {
            matrixSensorLocations.add(new Point(p.x + 7, p.y + 7)); // REMEMBER TO DELETE THIS (+7) DODGY SHIT
        }
        // Declare whether each matrix object is a sensor or not
        for (int x = 0; x < this.x; x++) {
            for (int y = 0; y < this.y; y++) {
                matrixObjects[x][y].isSensor = matrixSensorLocations.contains(new Point(x, y));
            }
        }
    }

    public double[][] returnData() {
        double[][] matrixData = new double[x][y];
        for (int y = 0; y < this.y; y++) {
            for (int x = 0; x < this.x; x++) {
                matrixData[x][y] = matrixObjects[x][y].value;
            }
        }
        return matrixData;
    }

    public int getXDimension() {
        return this.x;
    }

    public int getYDimension() {
        return this.y;
    }

    public void printSensorLocations() {
        for (int y = 0; y < this.y; y++) {
            for (int x = 0; x < this.x; x++) {
                if (matrixObjects[x][y].isSensor) {
                    System.out.print("X ");
                } else {
                    System.out.print("O ");
                }

            }
            System.out.println();
        }
    }

    public LinkedList<MatrixObject> findNeighbourSensors(Point p, MatrixObject[][] matrix) {

        LinkedList<MatrixObject> neighbours = findAllNeighbours(p, matrix);

        // Remove neignbours from list that are not sensors
        Iterator<MatrixObject> iterator = neighbours.iterator();
        while (iterator.hasNext()) {
            MatrixObject obj = iterator.next();
            if (!obj.isSensor) {
                iterator.remove();
            }
        }
        return neighbours;
    }

    public LinkedList<MatrixObject> findAllNeighbours(Point p, MatrixObject[][] matrix) {
        LinkedList<MatrixObject> neighbours = new LinkedList();

        if (isTopBounded(p) && isLeftBounded(p)) {
            neighbours.add(matrix[p.x + 1][p.y]); // East
            neighbours.add(matrix[p.x + 1][p.y + 1]); // South-East
            neighbours.add(matrix[p.x][p.y + 1]); // South
        } else if (isBottomBounded(p) && isLeftBounded(p)) {
            neighbours.add(matrix[p.x][p.y - 1]); // North
            neighbours.add(matrix[p.x + 1][p.y - 1]); // North-East
            neighbours.add(matrix[p.x + 1][p.y]); // East
        } else if (isBottomBounded(p) && isRightBounded(p)) {
            neighbours.add(matrix[p.x][p.y - 1]); // North
            neighbours.add(matrix[p.x - 1][p.y - 1]); // North-West
            neighbours.add(matrix[p.x - 1][p.y]); // West
        } else if (isTopBounded(p) && isRightBounded(p)) {
            neighbours.add(matrix[p.x][p.y + 1]); // South
            neighbours.add(matrix[p.x - 1][p.y + 1]); // South-West
            neighbours.add(matrix[p.x - 1][p.y]); // West
        } else if (isTopBounded(p)) {
            neighbours.add(matrix[p.x + 1][p.y]); // East
            neighbours.add(matrix[p.x + 1][p.y + 1]); // South-East
            neighbours.add(matrix[p.x][p.y + 1]); // South
            neighbours.add(matrix[p.x - 1][p.y + 1]); // South-West
            neighbours.add(matrix[p.x - 1][p.y]); // West
        } else if (isLeftBounded(p)) {
            neighbours.add(matrix[p.x][p.y - 1]); // North
            neighbours.add(matrix[p.x + 1][p.y - 1]); // North-East
            neighbours.add(matrix[p.x + 1][p.y]); // East
            neighbours.add(matrix[p.x + 1][p.y + 1]); // South-East
            neighbours.add(matrix[p.x][p.y + 1]); // South
        } else if (isBottomBounded(p)) {
            neighbours.add(matrix[p.x - 1][p.y]); // West
            neighbours.add(matrix[p.x - 1][p.y - 1]); // North-West
            neighbours.add(matrix[p.x][p.y - 1]); // North
            neighbours.add(matrix[p.x + 1][p.y - 1]); // North-East
            neighbours.add(matrix[p.x + 1][p.y]); // East
        } else if (isRightBounded(p)) {
            neighbours.add(matrix[p.x][p.y - 1]); // North
            neighbours.add(matrix[p.x - 1][p.y - 1]); // North-West
            neighbours.add(matrix[p.x - 1][p.y]); // West
            neighbours.add(matrix[p.x - 1][p.y + 1]); // South-West
            neighbours.add(matrix[p.x][p.y + 1]); // South
        } else {
            neighbours.add(matrix[p.x + 1][p.y]); // East
            neighbours.add(matrix[p.x + 1][p.y + 1]); // South-East
            neighbours.add(matrix[p.x][p.y + 1]); // South
            neighbours.add(matrix[p.x - 1][p.y + 1]); // South-West
            neighbours.add(matrix[p.x - 1][p.y]); // West
            neighbours.add(matrix[p.x - 1][p.y - 1]); // North-West
            neighbours.add(matrix[p.x][p.y - 1]); // North
            neighbours.add(matrix[p.x + 1][p.y - 1]); // North-East  
        }

        return neighbours;
    }

    public boolean hasNeighbourSensor(Point p) {
        LinkedList<MatrixObject> list = this.findNeighbourSensors(p, this.matrixObjects);
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

    public void updateMatrix() {
        Random rand = new Random();
        LinkedList<MatrixObject> allNeighbours, sensorNeighbours;
        MatrixObject[][] temp = this.matrixObjects;

        // Generate random value for sensors first....
        for (Point p : this.matrixSensorLocations) {           
            temp[p.x][p.y].value = MAX_INTENSITY * rand.nextDouble();
       //     System.out.println("Value: " + temp[p.x][p.y].value);
        }

        // Then iterate over matrix and update adjacent cells.
        // Execute multiple iterations before actually updating matrix for additional smoothing.
        for (int iterations = 0; iterations < 10; iterations++) { 
            for (int y = 0; y < this.y; y++) {
                for (int x = 0; x < this.x; x++) {
                    if (!this.matrixObjects[x][y].isSensor) {

                        allNeighbours = this.findAllNeighbours(new Point(x, y), matrixObjects);
                        sensorNeighbours = this.findNeighbourSensors(new Point(x, y), matrixObjects);

                        switch (sensorNeighbours.size()) {
                            case 0: // No neighbour sensors
                                temp[x][y].value = this.avgColor(allNeighbours);
                                break;
                            case 1:
                                temp[x][y].value = this.avgColor(sensorNeighbours)/1.3;
                                break;
                            case 2:
                                temp[x][y].value = this.avgColor(sensorNeighbours)/1.4;
                                break;
                            case 3:
                                temp[x][y].value = this.avgColor(sensorNeighbours)/1.5;
                                break;
                            default: // More than 3 neighbour sensors
                                temp[x][y].value = this.avgColor(sensorNeighbours)/1.6;
                                break;
                        }
                    }
                }
            }
        }
        matrixObjects = temp;
    }

    public double avgColor(LinkedList<MatrixObject> neighbours) {
        double val = 0;
        for (MatrixObject obj : neighbours) {
            val += obj.value;
        }
        return val / neighbours.size();
    }

}

