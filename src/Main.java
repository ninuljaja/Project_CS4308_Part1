/**
 * Main Class
 */

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LexicalAnalyzer la = new LexicalAnalyzer();
        // run lexical analyzer of java example
        System.out.println("Java code:");
        la.readFile("JavaExample.java");
        // run lexical analyzer of python example
        System.out.println("\nPython code:");
        la.readFile("PythonExample.py");
    }
}