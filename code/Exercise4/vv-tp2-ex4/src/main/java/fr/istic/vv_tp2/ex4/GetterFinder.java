package fr.istic.vv_tp2.ex4;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
        CompilationUnit unit = StaticJavaParser.parse(inputFile);
        String packageName = unit.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElse("(default)");

        for (ClassOrInterfaceDeclaration intClass : unit.findAll(
                ClassOrInterfaceDeclaration.class)) {
            // Filtering interfaces and non-public classes.
            if (intClass.isInterface() || !intClass.isPublic())
                continue;

            List<String> fieldNames = new ArrayList<>();

            // Gathering fields identifiers
            for (FieldDeclaration fieldDecl : intClass.getFields())
                for (VariableDeclarator varDecl : fieldDecl.getVariables())
                    fieldNames.add(varDecl.getNameAsString());

            // For each field, find if they have a getter.
            for (String fieldName : fieldNames) {
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) +
                        (fieldName.length() > 1 ? fieldName.substring(1) : "");

                // List of getters.
                List<MethodDeclaration> getters = intClass.getMethodsByName(getterName)
                        .stream()
                        .filter(MethodDeclaration::isPublic)
                        .toList();

                if (getters.isEmpty()) {
                    String csvLine =
                            String.format("%s,%s,%s", fieldName, intClass.getNameAsString(),
                                    packageName);
                    output.println(csvLine);
                }
            }
        }
    }
}
