package net.opengrabeso.opengl;

import com.github.opengrabeso.jaagl.GL2;
import com.jogamp.common.util.InterruptSource;
import com.jogamp.opengl.awt.GLCanvas;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
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
            lwjgl = false; // default with no option
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

            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

            long window = glfwCreateWindow(512, 512, getClass().getName(), NULL, NULL);

            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            glfwShowWindow(window);

            org.lwjgl.opengl.GL.createCapabilities();

            GL2 gl = null;

            jaaglListener.init(gl);

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while ( !glfwWindowShouldClose(window) ) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

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
