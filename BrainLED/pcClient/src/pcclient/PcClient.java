package pcclient;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
//import com.emotiv.Iedk.*;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import java.awt.Dimension;
import java.util.Random;
import javax.swing.JFrame;

/**
 * TODO: - Design kernel to be used for gaussian smoothing
 *       - Implement a simple gaussian smoothing/blur algorithm
 * 
 * NOTES:
 *       - https://homepages.inf.ed.ac.uk/rbf/HIPR2/gsmooth.htm
 *       - https://en.wikipedia.org/wiki/Gaussian_blur
 * 
 * REFERENCES:
 *       - http://www.andrewnoske.com/wiki/Code_-_heatmaps_and_color_gradients
 *       - http://www.tc33.org/projects/jheatchart/
 */
public class PcClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {

        // Heatmap GUI related stuff
        JFrame window = new JFrame("HeatMap GUI");
        Panel panel = new Panel(100, 100); // heatmap dimensions
        panel.setCellSize(new Dimension(5,5)); // heatmap cell size
        window.setResizable(true);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(panel.getMapSize().width, panel.getMapSize().height);
        window.add(panel);
        window.setVisible(true);
        
        // EEG headset related stuff
        Pointer eEvent = Edk.INSTANCE.IEE_EmoEngineEventCreate();
        Pointer eState = Edk.INSTANCE.IEE_EmoStateCreate();
        IntByReference userID = null;
        boolean ready = false;
        int state = 0;
        Edk.IEE_DataChannels_t dataChannel;
        userID = new IntByReference(0);
        
        // Stuff for storing data
        Random rand = new Random();
        double[] alphaValues = new double[14];
        double[] lowBetaValues = new double[14];
        double[] highBetaValues = new double[14];
        double[] gammaValues = new double[14];
        double[] thetaValues = new double[14];
        double[] normalizedAlphaValues = new double[14];
        double min, max;      
        
        // Change this to true to print data from EEG headset
        boolean debug = false;

        if (Edk.INSTANCE.IEE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK
                .ToInt()) {
            System.out.println("Emotiv Engine start up failed.");
            return;
        }

        System.out.println("Start receiving Data!");
        System.out.println("Theta, Alpha, Low_beta, High_beta, Gamma");

        while (true) {

            state = Edk.INSTANCE.IEE_EngineGetNextEvent(eEvent);

            // New event needs to be handled
            if (state == EdkErrorCode.EDK_OK.ToInt()) {

                int eventType = Edk.INSTANCE.IEE_EmoEngineEventGetType(eEvent);
                Edk.INSTANCE.IEE_EmoEngineEventGetUserId(eEvent, userID);

                // Log the EmoState if it has been updated
                if (eventType == Edk.IEE_Event_t.IEE_UserAdded.ToInt()) {
                    if (userID != null) {
                        System.out.println("User added");
                        ready = true;
                    }
                }
            } else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
                System.out.println("Internal error in Emotiv Engine!");
                break;
            }

            if (ready) {

                DoubleByReference alpha = new DoubleByReference(0);
                DoubleByReference low_beta = new DoubleByReference(0);
                DoubleByReference high_beta = new DoubleByReference(0);
                DoubleByReference gamma = new DoubleByReference(0);
                DoubleByReference theta = new DoubleByReference(0);              

                for (int i = 3; i < 17; i++) {
                    int result = Edk.INSTANCE.IEE_GetAverageBandPowers(userID.getValue(), i, theta, alpha, low_beta, high_beta, gamma);
                    if (result == EdkErrorCode.EDK_OK.ToInt()) {

                        thetaValues[i - 3] = theta.getValue();
                        lowBetaValues[i - 3] = low_beta.getValue();
                        highBetaValues[i - 3] = high_beta.getValue();
                        gammaValues[i - 3] = gamma.getValue();
                        alphaValues[i - 3] = alpha.getValue();

                        if (debug) {
                            System.out.print(theta.getValue());
                            System.out.print(", ");
                            System.out.print(alpha.getValue());
                            System.out.print(", ");
                            System.out.print(low_beta.getValue());
                            System.out.print(", ");
                            System.out.print(high_beta.getValue());
                            System.out.print(", ");
                            System.out.print(gamma.getValue());
                            System.out.print(", ");
                        }
                    }
                    if (debug) {
                        System.out.println();
                    }
                }
            }

            // Reset min/max variables from previous iteration
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
            
            // Generate random alphaValues for testing and store min/max for normalisation
            for (int i = 0; i < 14; i++) {
                alphaValues[i] = rand.nextDouble() * 20;
                if (alphaValues[i] > max) {
                    max = alphaValues[i];
                } else if (alphaValues[i] < min) {
                    min = alphaValues[i];
                }
            }

            // Normalise data such that it ranges from 0 - 1
            for (int i = 0; i < 14; i++) {
                normalizedAlphaValues[i] = (alphaValues[i] - min) / (max - min);
            }

            // Update heatmap with normalised random data
            panel.setHeatMapSensorValues(normalizedAlphaValues);
            panel.update();

        }

        Edk.INSTANCE.IEE_EngineDisconnect();
        Edk.INSTANCE.IEE_EmoStateFree(eState);
        Edk.INSTANCE.IEE_EmoEngineEventFree(eEvent);
        System.out.println("Disconnected!");
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
