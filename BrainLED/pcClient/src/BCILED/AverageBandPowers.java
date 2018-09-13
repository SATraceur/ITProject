/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BCILED;

/**
 * **************************************************************************
 **
 ** Copyright 2015 by Emotiv. All rights reserved * Example - AverageBandPowers
 * * The average band power for a specific channel from the latest epoch with *
 * 0.5 seconds step size and 2 seconds window size * This example is used for
 * single connection. *
 * **************************************************************************
 */
//import com.emotiv.Iedk.*;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author yeqin
 */
public class AverageBandPowers {

    /**
     *
     * @param args
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        Pointer eEvent = Edk.INSTANCE.IEE_EmoEngineEventCreate();
        Pointer eState = Edk.INSTANCE.IEE_EmoStateCreate();

        IntByReference userID = null;
        boolean ready = false;
        int state = 0;
        PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
        Edk.IEE_DataChannels_t dataChannel;

        userID = new IntByReference(0);

        if (Edk.INSTANCE.IEE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK
                .ToInt()) {
            writer.println("Emotiv Engine start up failed.");
            return;
        }

        writer.println("Start receiving Data!");
        writer.println("Theta, Alpha, Low_beta, High_beta, Gamma");

        while (true) {
            state = Edk.INSTANCE.IEE_EngineGetNextEvent(eEvent);

            // New event needs to be handled
            if (state == EdkErrorCode.EDK_OK.ToInt()) {
                int eventType = Edk.INSTANCE.IEE_EmoEngineEventGetType(eEvent);
                Edk.INSTANCE.IEE_EmoEngineEventGetUserId(eEvent, userID);

                // Log the EmoState if it has been updated
                if (eventType == Edk.IEE_Event_t.IEE_UserAdded.ToInt()) {
                    if (userID != null) {
                        writer.println("User added");
                        ready = true;
                    }
                }
            } else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
                writer.println("Internal error in Emotiv Engine!");
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
                        writer.print(theta.getValue());
                        writer.print(", ");
                        writer.print(alpha.getValue());
                        writer.print(", ");
                        writer.print(low_beta.getValue());
                        writer.print(", ");
                        writer.print(high_beta.getValue());
                        writer.print(", ");
                        writer.print(gamma.getValue());
                        writer.print(", ");
                        writer.print("id: ");
                        writer.print(i);
                        writer.print(", ");
                    }

                    writer.println();
                    writer.flush();
                }
            }
        }

        Edk.INSTANCE.IEE_EngineDisconnect();
        Edk.INSTANCE.IEE_EmoStateFree(eState);
        Edk.INSTANCE.IEE_EmoEngineEventFree(eEvent);
        System.out.println("Disconnected!");
    }
}
