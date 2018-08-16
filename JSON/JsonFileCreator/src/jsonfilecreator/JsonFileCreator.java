/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonfilecreator;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yeqin
 */

public class JsonFileCreator {

    public static DateSet dataSetBuilderRandom() {
        DateSet d = new DateSet();
        int totalNumber = 100;
        for (int i = 0; i < totalNumber; i++) {
            Random r = new Random();
            int Low = 1;
            int High = 14;
            int selection = r.nextInt(High - Low) + Low;
            switch (selection) {
                case 1:
                    d.dataSet.add(new DataFormat(i, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 2:
                    d.dataSet.add(new DataFormat(i, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 3:
                    d.dataSet.add(new DataFormat(i, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 4:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 5:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 6:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 7:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 8:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0));
                    break;
                case 9:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
                    break;
                case 10:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
                    break;
                case 11:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0));
                    break;
                case 12:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0));
                    break;
                case 13:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0));
                    break;
                case 14:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
                    break;
            }

        }
        return d;
    }

    public static DateSet dataSetBuilderOneByOne() {
        DateSet d = new DateSet();
        int totalNumber = 140;
        for (int i = 0; i < totalNumber; i++) {

            int selection = i / 10 + 1;
            switch (selection) {
                case 1:
                    d.dataSet.add(new DataFormat(i, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 2:
                    d.dataSet.add(new DataFormat(i, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 3:
                    d.dataSet.add(new DataFormat(i, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 4:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 5:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 6:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 7:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
                    break;
                case 8:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0));
                    break;
                case 9:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
                    break;
                case 10:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
                    break;
                case 11:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0));
                    break;
                case 12:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0));
                    break;
                case 13:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0));
                    break;
                case 14:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
                    break;
                default:
                    d.dataSet.add(new DataFormat(i, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
                    break;
            }

        }

        return d;
    }
   
   public static String customersFileBuilder(int s) {
        Gson gson = new Gson();
        DateSet d;
        if(s==1){
        d = dataSetBuilderRandom();
        }else {
        d = dataSetBuilderOneByOne();
        }
        
        String customersFile = "dataSet.txt";
        try {
            
            PrintWriter out = new PrintWriter(customersFile);
            out.println(gson.toJson(d));
            out.close();
             
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DateSet.class.getName()).log(Level.SEVERE, null, ex);
        }

       return customersFile;
    }
/**
 * @param args the command line arguments
 */
public static void main(String[] args) {
        // TODO code application logic here
        customersFileBuilder(0);
    }
    
}
