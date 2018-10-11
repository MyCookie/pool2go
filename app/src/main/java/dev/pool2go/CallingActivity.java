package dev.pool2go;

/**
 * The test server will want a way to write to a log file, however permissions will be an issue since
 * it is not the main/home activity. This provides a callback method for the Activity that spins up
 * the server.
 */
public interface CallingActivity {
    void appendLog(String s);
}
