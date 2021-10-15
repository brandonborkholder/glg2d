package net.opengrabeso.glg2d;

import com.github.opengrabeso.jaagl.GL2GL3;
import net.opengrabeso.glg2d.impl.shader.GLShaderGraphics2D;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import javax.swing.*;
import java.awt.*;
import java.awt.peer.ComponentPeer;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GLG2DPanelLWJGL {

    private final JComponent component;
    private final String title;
    // The window handle
    private long window;

    public GLG2DPanelLWJGL(JComponent component, String title) {
        this.component = component;
        this.title = title;
    }

    public GLG2DPanelLWJGL() {
        this(null, "LWJGL Example");
    }
    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private long setup(int major, int minor, boolean core, boolean forward) {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, major);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, minor);
        glfwWindowHint(GLFW_OPENGL_PROFILE, core ? GLFW_OPENGL_CORE_PROFILE : GLFW_OPENGL_ANY_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, forward ? GLFW_TRUE : GLFW_FALSE);
        long ret = glfwCreateWindow(300, 300, title, NULL, NULL);
        if (ret != NULL) {
            System.out.printf("Created OpenGL %d.%d", major, minor);
        }
        return ret;
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable


        window = setup(4, 0, true, false); // try core 4 first
        if (window == NULL) window = setup(3, 3, true, false); // then core 3.3
        if (window == NULL) window = setup(3, 2, true, false); // then core 3.2
        if (window == NULL) window = setup(3, 2, true, true); // then core 3.2 forward compatible
        if (window == NULL) window = setup(1, 0, false, true); // as a last resort, try anything you have

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLCapabilities caps = GL.createCapabilities();

        GL2GL3 gl;

        if (caps.OpenGL30) {
            gl = com.github.opengrabeso.jaagl.lwjgl.LWGL.createGL3();
        } else {
            gl = com.github.opengrabeso.jaagl.lwjgl.LWGL.createGL2();
        }
        GLShaderGraphics2D graphics2D = new GLShaderGraphics2D(gl);


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            if (component == null) {
                // Set the clear color
                glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            } else {
                Color color = component.getBackground();

                if (color == null) color = Color.lightGray;

                gl.glClearColor(color.getRed()  / 255.0f, color.getGreen()  / 255.0f, color.getBlue()  / 255.0f, 1);
                gl.glClear(gl.GL_COLOR_BUFFER_BIT() | gl.GL_DEPTH_BUFFER_BIT()); // clear the framebuffer


                graphics2D.prePaint(gl);

                try ( MemoryStack stack = stackPush() ) {
                    IntBuffer pWidth = stack.mallocInt(1); // int*
                    IntBuffer pHeight = stack.mallocInt(1); // int*
                    glfwGetWindowSize(window, pWidth, pHeight);
                    Dimension dim = new Dimension(pWidth.get(), pHeight.get());
                    if (dim != component.getSize()) {
                        //println(s"component resize ${dim.width}")
                        component.setSize(dim.width, dim.height);
                        //component.onSizeChanged();
                        gl.glViewport(0, 0, dim.width, dim.height);
                        //graphics2D.setClip(0, 0, dim.width, dim.height);

                        ComponentPeer peer = new LWJGLComponentPeer();
                        // peer is necessary for validate - otherwise validateTree is not called
                        sun.awt.AWTAccessor.getComponentAccessor().setPeer(component, peer);
                        component.invalidate();
                        component.validate();
                    }
                }

                component.paint(graphics2D);

                graphics2D.postPaint();
            }

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

}