/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * hahaha i did too
 i changed something
 */
package pcclient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author yeqin
 */
public class PcClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        String address = "localhost";

        while (true) {
            Socket s = new Socket(address, 5000);

            String k;
            Scanner scanner = new Scanner(new File("d:\\test.txt"));
            k = scanner.next();
            System.out.print(k);
            scanner.close();
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println(k);
            out.close();
            s.close();

            Thread.sleep(10000);
        }

    }
}
