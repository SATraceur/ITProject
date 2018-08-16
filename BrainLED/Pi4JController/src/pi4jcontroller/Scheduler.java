/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pi4jcontroller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.TreeMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Louis
 */
public class Scheduler {

    /**
     *
     * attributes of customer
     */
    private static final int NUM_THREADS = 1;
//    private static final boolean DONT_INTERRUPT_IF_RUNNING = false;
//    private final ScheduledExecutorService fScheduler;
//    private final long fInitialDelay;
//    private final long fDelayBetweenRuns;
//    private final long fShutdownAfter;
    public static String fileName;

    public static DataFormat getDataSet(String line) {
        DataFormat dataFormat = null;

        Gson gson = new Gson();

        dataFormat = gson.fromJson(line, DataFormat.class);

        return dataFormat;
    }

}
