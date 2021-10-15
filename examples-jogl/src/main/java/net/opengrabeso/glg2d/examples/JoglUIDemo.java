package net.opengrabeso.glg2d.examples;

public class JoglUIDemo {
    public static void main(String[] args) {
        UIDemoFrame main = new UIDemoFrame(new JoglExampleFactory());
        try {
            main.display();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
