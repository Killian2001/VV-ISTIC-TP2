package fr.istic.vv_tp2.ex4;

import com.github.javaparser.StaticJavaParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * Main class of the GetterFinder program, which finds every private attributes with
 * no getter in a given Java project.
 */
public class GetterFinder {

    /**
     * Entry point of the program.
     *
     * @param args Program's arguments : should contain the folder of a project.
     * @throws IOException If any I/O error occurs.
     */
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

    /**
     * Explore recursively a directory in order to find <code>.java</code> files
     * in it, and find attributes with no getters in these files.
     *
     * @param file   Directory's file.
     * @param output Output file's print writer.
     * @throws IOException If any I/O error occur.
     */
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

    /**
     * Finds private attributes with no getter in a given Java file.
     *
     * @param inputFile Java file.
     * @param output    Output file's print writer.
     * @throws IOException If any I/O error occur.
     */
    private static void findGetters(File inputFile, PrintWriter output) throws IOException {
        GetterFinderVisitor visitor = new GetterFinderVisitor();
        GetterFinderVisitor.VisitorParam param = new GetterFinderVisitor.VisitorParam(output);

        StaticJavaParser.parse(inputFile)
                .accept(visitor, param);
    }
}
