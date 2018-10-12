package dev.pool2go;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A version-one simple server that reads input from any client on port 8080 and writes to a log.
 */

public class TestServer implements Runnable {

    ServerSocket listener;
    CallingActivity caller;
    String filename;

    public TestServer (CallingActivity appendLogCallback) throws IOException {
        listener = new ServerSocket(8080);
        caller = appendLogCallback;
        // associate a new logfile for the server and it's caller/creator class
        if (caller.getClass().isAnonymousClass()) {
            // however if it is an anonymous class, make a generic log file
            // when writing to the file, use the class's memory address to identify the log
            filename = "test_server_log.txt";
            caller.fileFactory(filename, false);
            caller.appendLog(filename, "Added new anonymous class located at " + caller.toString(), false);
        } else {
            filename = "test_server_" + caller.getClass().getSimpleName() + "_.txt";
            caller.fileFactory(filename, false);
        }
    }

    /**
     * Use Thread.start() to call this method.
     */
    public void run() {
        try {
            while(true) {
                Socket socket = listener.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String s = in.readLine();
                if (s != null) {
                    if (caller.getClass().isAnonymousClass()) {
                        // if anonymous use the memory address as an identifier instead of a simple name
                        caller.appendLog(filename, "TestServer, " + caller.toString() + ": " + s, false);
                    } else {
                        caller.appendLog(filename, "TestServer, " + caller.getClass().getSimpleName() + ": " + s, false);
                    }
                }

                // Thread.stop() has been deprecated, use Thread.interrupt() instead
                if (Thread.interrupted())
                    return;
            }
        } catch (IOException e) {
            e.getMessage();
            // write message to file
        }
    }
}
