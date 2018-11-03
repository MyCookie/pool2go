package dev.pool2go;

import java.util.logging.Level;

/**
 * The test server will want a way to write to a log file, however permissions will be an issue since
 * it is not the main/home activity. This provides a callback method for the Activity that spins up
 * the server.
 */
public interface CallingActivity {

    /**
     * <p>Send a message to Logcat. Use the appropriate Level:</p>
     * <p>
     *     <ul>
     *         <li>Level.SEVERE: Log an error using Log.e</li>
     *         <li>Level.WARNING: Log a warning using Log.w</li>
     *         <li>Level.CONFIG: Log a useful event using Log.i</li>
     *         <li>Level.INFO: Log a debug message uing Log.d</li>
     *     </ul>
     * </p>
     *
     * @see android.util.Log
     * @see java.util.logging.Level
     *
     * @param level the appropriate level of the event
     * @param s the message to send
     * @param notify
     */
    void appendLog(Level level, String s, boolean notify);

    /**
     * Pass a file name, if a file with the given name exists, do nothing. If not, create the file.
     * If notify is flagged as true, create a simple, un-obtrusive notification for the user.
     *
     * @param filename
     * @param notify
     */
    void fileFactory(String filename, boolean notify);
}
