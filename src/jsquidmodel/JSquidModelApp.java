/*
 * JSquidModelApp.java
 */

package jsquidmodel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class JSquidModelApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        JSquidModelView Form1 = new JSquidModelView(this);
        try {
            Form1.onStartup();
        } catch (IOException ex) {
            Logger.getLogger(JSquidModelApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        show(Form1);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JSquidModelApp
     */
    public static JSquidModelApp getApplication() {
        return Application.getInstance(JSquidModelApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(JSquidModelApp.class, args);
    }
}
