package net.opengrabeso.opengl;

import com.github.opengrabeso.jaagl.GL2GL3;
import com.github.opengrabeso.jaagl.GL3;
import com.jogamp.common.util.InterruptSource;
import com.jogamp.opengl.awt.GLCanvas;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SelectJaaglEventListener {

    private final Jaagl2EventListener jaaglListener;

    public SelectJaaglEventListener(Jaagl2EventListener jaaglListener) {

        this.jaaglListener = jaaglListener;
    }

    public void run(String[] args) {
        boolean jogl = Arrays.asList(args).contains("-jogl");
        boolean lwjgl = Arrays.asList(args).contains("-lwjgl");
        if (!jogl && !lwjgl) {
            lwjgl = true; // default with no option
        }

        if (lwjgl) {

            // will print the error message in System.err.
            GLFWErrorCallback.createThrow().set();
            // Initialize GLFW. Most GLFW functions will not work before doing this.
            if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
            glfwDefaultWindowHints(); // optional, the current window hints are already the default

            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation

            //glfwWindowHint(GLFW_SAMPLES, pars.antialiasingSamples);

            glfwWindowHint(GLFW_STENCIL_BITS, 8);
            glfwWindowHint(GLFW_DEPTH_BITS, 24);
            // TODO: try other profiles as well
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

            long window = glfwCreateWindow(512, 512, getClass().getName(), NULL, NULL);

            glfwMakeContextCurrent(window);

            glfwSwapInterval(1);
            glfwShowWindow(window);

            int[] width = new int[1];
            int[] height = new int[1];
            glfwGetWindowSize(window, width, height);

            org.lwjgl.opengl.GL.createCapabilities();

            GL2GL3 gl = com.github.opengrabeso.jaagl.lwjgl.LWGL2.createGL3();

            jaaglListener.init(gl);

            jaaglListener.reshape(gl, 0, 0, width[0], height[0]);

            glfwSetWindowSizeCallback(window, (windowHandle, w, h) ->
                        jaaglListener.reshape(gl, 0, 0, w, h)
                    );

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while ( !glfwWindowShouldClose(window) ) {

                jaaglListener.display(gl);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                glfwSwapBuffers(window); // swap the color buffers

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
            }



        } else {
            final Frame frame = new Frame(getClass().getName());
            frame.setLayout(new BorderLayout());

            final GLCanvas canvas = new GLCanvas();

            Jaagl2EventListenerJogl listenerJogl = new Jaagl2EventListenerJogl(jaaglListener);

            canvas.addGLEventListener(listenerJogl);
            frame.add(canvas, BorderLayout.CENTER);

            frame.setSize(512, 512);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent e) {
                    new InterruptSource.Thread(null, new Runnable() {
                        public void run() {
                            System.exit(0);
                        }
                    }).start();
                }
            });
            try {
                javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        frame.setVisible(true);
                    }
                });
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }


}
