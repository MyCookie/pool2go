package dev.pool2go;

/**
 * The test server will want a way to write to a log file, however permissions will be an issue since
 * it is not the main/home activity. This provides a callback method for the Activity that spins up
 * the server.
 */
public interface CallingActivity {

    /**
     * Given a string s, write to a file defined by filename. If notify is flagged as true, create
     * a simple, un-obtrusive notification for the user.
     *
     * @param filename
     * @param s
     * @param notify
     */
    void appendLog(String filename, String s, boolean notify);

    /**
     * Pass a file name, if a file with the given name exists, do nothing. If not, create the file.
     *
     * @param filename
     */
    void fileFactory(String filename);
}
