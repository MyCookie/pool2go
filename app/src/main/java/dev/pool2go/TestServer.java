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

    public TestServer (CallingActivity appendLogCallback) throws IOException {
        listener = new ServerSocket(8080);
        caller = appendLogCallback;
    }

    /**
     * Thread.start will call this method.
     */
    public void run() {
        try {
            while(true) {
                Socket socket = listener.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String s = in.readLine();
                if (s != null) {
                    caller.appendLog("TestServer: " + s, false);
                }
            }
        } catch (IOException e) {
            e.getMessage();
            // write message to file
        }
    }
}
