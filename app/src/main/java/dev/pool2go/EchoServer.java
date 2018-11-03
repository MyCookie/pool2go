package dev.pool2go;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

/**
 * An echo server which echos it's input to Logcat
 */

public class EchoServer implements Runnable {

    private ServerSocket listener;
    private CallingActivity caller;

    public EchoServer(CallingActivity appendLogCallback, int port) throws IOException {
        listener = new ServerSocket(port);
        caller = appendLogCallback;

        if (caller.getClass().isAnonymousClass())
            caller.appendLog(Level.CONFIG,
                "Test Server created for anonymous class " + caller.toString() + " on port " + Integer.toString(port) + ".",
                false);
        else
            caller.appendLog(Level.CONFIG,
                "Test Server created for " + caller.getClass().getSimpleName() + " on port " + Integer.toString(port) + ".",
                false);
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
                    // if anonymous use the memory address as an identifier instead of a simple name
                    if (caller.getClass().isAnonymousClass())
                        caller.appendLog(Level.INFO, "EchoServer, " + caller.toString() + ": " + s, false);
                    else
                        caller.appendLog(Level.INFO, "EchoServer, " + caller.getClass().getSimpleName() + ": " + s, false);
                }

                // Thread.stop() has been deprecated, use Thread.interrupt() instead
                if (Thread.interrupted())
                    return;
            }
        } catch (IOException e) {
            caller.appendLog(Level.WARNING, e.getMessage(), false);
        }
    }
}
