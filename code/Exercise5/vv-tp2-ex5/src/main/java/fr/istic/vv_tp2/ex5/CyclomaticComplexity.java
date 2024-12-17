package fr.istic.vv_tp2.ex5;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

public class CyclomaticComplexity {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println(
                    "error: no inputFile provided: please provide at least one inputFile path.");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(inputFile.getName() + "_report_cc.csv");
        try (PrintWriter output = new PrintWriter(new FileOutputStream(outputFile))) {
            output.println("Package,Declaring class,Method,Params,CC");
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
            exploreCompilationUnit(file, output);
    }

    private static void exploreCompilationUnit(File inputFile, PrintWriter output)
            throws FileNotFoundException {
        CompilationUnit unit = StaticJavaParser.parse(inputFile);

        // Get the package name.
        String packageName = unit.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElse("(default)");

        for (ClassOrInterfaceDeclaration intClass : unit.findAll(
                ClassOrInterfaceDeclaration.class))
            exploreClass(intClass, output, packageName);
    }

    private static void exploreClass(ClassOrInterfaceDeclaration intClass, PrintWriter output,
            String packageName) {
        // Filtering interfaces.
        if (intClass.isInterface())
            return;

        String className = intClass.getNameAsString();

        // Iterate over all methods of the class.
        for (MethodDeclaration method : intClass.getMethods()) {
            if (method.getBody()
                    .isEmpty())
                continue;

            String methodName = method.getNameAsString();

            // Build the list of parameters.
            List<Parameter> params = method.getParameters();
            List<String> paramStrings =
                    params.stream()
                            .map(p -> p.getTypeAsString() + " " + p.getNameAsString())
                            .toList();
            String paramList = "\"(" + String.join(", ", paramStrings) + ")\"";

            // Compute the cyclomatic complexity.
            int cyclomaticComplexity = calculateCyclomaticComplexity(method);

            // Print the result in the file.
            String csvLine =
                    String.format("%s,%s,%s,%s,%s", packageName, className, methodName, paramList,
                            cyclomaticComplexity);
            output.println(csvLine);

            // Explore subclasses.
            for (FieldDeclaration field : intClass.getFields())
                field.ifClassOrInterfaceDeclaration(
                        cls -> exploreClass(cls, output, packageName));
        }
    }

    private static int calculateCyclomaticComplexity(MethodDeclaration declaration) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        CyclomaticComplexityVisitor.CycloCounter counter =
                new CyclomaticComplexityVisitor.CycloCounter();

        declaration.accept(visitor, counter);

        return counter.getCyclomaticNumber();
    }
}
