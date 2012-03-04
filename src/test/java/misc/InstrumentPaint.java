package misc;

import glg2d.VertexBuffer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.swing.JComponent;

import com.sun.tools.attach.VirtualMachine;

public class InstrumentPaint {
  public static void instrument() {
    try {
      instrument0();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * I hacked this together with duct tape and wire hangers, along with help
   * from these two sites: <a href=
   * "http://sleeplessinslc.blogspot.com/2008/09/java-instrumentation-with-jdk-16x-class.html"
   * >http://sleeplessinslc.blogspot.com/2008/09/java-instrumentation-with-jdk-
   * 16x-class.html</a> and <a href=
   * "http://sleeplessinslc.blogspot.com/2008/07/java-instrumentation.html"
   * >http://sleeplessinslc.blogspot.com/2008/07/java-instrumentation.html</a>.
   */
  private static void instrument0() throws Exception {
    // create a temporary jar for the agent
    File jarFile = File.createTempFile("agent", ".jar");
    jarFile.deleteOnExit();

    // create the manifest, we need especially these attributes
    Manifest manifest = new Manifest();
    Attributes mainAttributes = manifest.getMainAttributes();
    mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    mainAttributes.put(new Attributes.Name("Agent-Class"), InstrumentPaint.class.getName());
    mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
    mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");

    // write this class as the agent
    JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);
    JarEntry agent = new JarEntry(InstrumentPaint.class.getName().replace('.', '/') + ".class");
    jos.putNextEntry(agent);
    CtClass ctClass = ClassPool.getDefault().get(InstrumentPaint.class.getName());
    jos.write(ctClass.toBytecode());
    jos.closeEntry();
    jos.close();

    // use an Oracle-specific method to get pid of the current JVM
    String name = ManagementFactory.getRuntimeMXBean().getName();
    VirtualMachine vm = VirtualMachine.attach(name.substring(0, name.indexOf('@')));

    // load the agent and redefine the classes
    vm.loadAgent(jarFile.getAbsolutePath());

    // detaches, we already loaded the agent
    vm.detach();
  }
  
  public static void agentmain(String agentargs, Instrumentation instrumentation) throws UnmodifiableClassException, NotFoundException {
    final ClassPool pool = ClassPool.getDefault();

    instrumentation.addTransformer(new ClassFileTransformer() {
      @Override
      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
          byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!className.equals("javax/swing/JComponent")) {
          return classfileBuffer;
        }
        
        System.err.println("Redefining: " + className);

        try {
          CtClass c = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
          CtMethod method = c.getMethod("paint", "(Ljava/awt/Graphics;)V");
          method.insertBefore(" if (getClass().getName().startsWith(\"javax.swing\") && !$1.getClass().getName().equals(\"glg2d.GLGraphics2D\")) System.err.println(getClass().getName() + \".paint(\" + $1.getClass().getName() + \")\");");
          return c.toBytecode();
        } catch (Exception e) {
          e.printStackTrace();
          return classfileBuffer;
        }
      }
    }, true);

    instrumentation.retransformClasses(JComponent.class);
  }
}
