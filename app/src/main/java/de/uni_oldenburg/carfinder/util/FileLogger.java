package de.uni_oldenburg.carfinder.util;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.uni_oldenburg.carfinder.BuildConfig;

/**
 * Debugging file logger for testing geo-related stuff.
 */
public class FileLogger {

    private static String path = null;
    private static FileLogger instance = null;

    public static FileLogger getInstance() {
        if (instance == null)
            instance = new FileLogger();
        return instance;
    }

    public static void init(String logPath) {
        path = logPath + "/carfinder.log";
        Log.i(Constants.LOG_TAG, "FilePath for Logger is:" + path);

    }

    public void log(String text) {
        if (BuildConfig.DEBUG) {
            File logFile = new File(path);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
