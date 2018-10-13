package BCILED;

import java.awt.Point;

/**
 * Object to hold the matrix cell data including it's position within the matrix, 
 * whether it's a sensor or not and it's value.
 * @author SATraceur
 */
public class MatrixObject {

    public Point point;
    public boolean isSensor;
    public double value;

    /**
     * MatrixObject constructor
     * @param p - position of cell within the matrix.
     * @param val - value of the cell.
     */
    public MatrixObject(Point p, double val) {
        this.point = p;
        this.value = val;
    }
}
