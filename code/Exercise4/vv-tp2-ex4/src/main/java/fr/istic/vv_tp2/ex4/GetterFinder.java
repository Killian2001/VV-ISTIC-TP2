package fr.istic.vv_tp2.ex4;

import com.github.javaparser.StaticJavaParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class GetterFinder {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println(
                    "error: no inputFile provided: please provide at least one inputFile path.");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(inputFile.getName() + "_report_no_getter.csv");
        try (PrintWriter output = new PrintWriter(new FileOutputStream(outputFile))) {
            output.println("No getter field name,Declaring class,Package");
            exploreDirectory(inputFile, output);
        }
    }

    private static void exploreDirectory(File file, PrintWriter output) throws IOException {
        // If directory, explore its sub directories.
        if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
            for (File child : file.listFiles())
                exploreDirectory(child, output);
            return;
        }

        // Otherwise parse file.
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (fileName.substring(dotIndex == -1 ? 0 : dotIndex)
                .equals(".java"))
            findGetters(file, output);
    }

    private static void findGetters(File inputFile, PrintWriter output) throws IOException {
        GetterFinderVisitor visitor = new GetterFinderVisitor();
        GetterFinderVisitor.VisitorParam param = new GetterFinderVisitor.VisitorParam(output);

        StaticJavaParser.parse(inputFile)
                .accept(visitor, param);
    }
}
