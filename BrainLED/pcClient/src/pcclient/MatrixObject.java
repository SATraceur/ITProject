/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcclient;

import java.awt.Point;

/**
 *
 * @author Anonymous
 */
public class MatrixObject {
    public Point point;
    public boolean isSensor;
    public double value;
    
    public MatrixObject(Point p, double val){
        this.point = p;
        this.value = val;
    }
    
    @Override
    public String toString() {
        return "X: " + this.point.x + " Y: " + this.point.y + " VALUE: " + this.value;
    }
}
