/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi4jcontroller;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author yeqin
 */
public class ActServer {

    public static void main(String args[]) throws Exception {

        while (true) {
            //create server socket on port 5000
            ServerSocket ss = new ServerSocket(5000);

            System.out.println("Waiting for request");
            while (true) {
                Socket s = ss.accept();
                System.out.println("Connected With " + s.getInetAddress().toString());
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                String result = br.readLine();
                System.out.println(result);

                System.out.println("SendGet....Ok");

                br.close();
                s.close();
                DataFormat d = Scheduler.getDataSet(result);
                System.out.println(d);
                Pi4JController.controlPins(d);

            }

        }

    }
}
