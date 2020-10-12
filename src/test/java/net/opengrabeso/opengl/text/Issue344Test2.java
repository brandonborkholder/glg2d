package net.opengrabeso.opengl.text;

public class Issue344Test2 extends Issue344Base {
    protected String getText() {
        // test 2 - unicode hangs program with a large font & long string
        return "\u201Cabcdef\u201D \u201Cghijkl\u201D Up: \u2191 \u201C\u201D  \u201Cmnopqrstuvwxyz\u201D";
    }

    public static void main(final String[] args) {
        new Issue344Test2().run(args);
    }
}
