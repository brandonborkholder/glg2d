package net.opengrabeso.glg2d.examples;

public class JoglExample {
    public static void main(String[] args) {
        G2DExample main = new G2DExample(new JoglExampleFactory());
        main.display();
    }
}
