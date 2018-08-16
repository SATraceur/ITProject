/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi4jcontroller;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author yeqin
 */
public class DisplayReader {

    private static DataFormat getDataFormat(String fileName) {
        DataFormat dataFormat = null;
        try {
            Gson gson = new Gson();
            String line = "";
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            fileReader.close();

            dataFormat = gson.fromJson(line, DataFormat.class);

        } catch (IOException e) {
        }

        return dataFormat;
    }

}
