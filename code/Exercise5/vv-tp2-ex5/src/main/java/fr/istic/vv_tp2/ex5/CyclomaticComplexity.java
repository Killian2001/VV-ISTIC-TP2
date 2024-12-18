package fr.istic.vv_tp2.ex5;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import fr.istic.vv_tp2.ex5.export.BarplotCycloExporter;
import fr.istic.vv_tp2.ex5.export.CSVCycloExporter;
import fr.istic.vv_tp2.ex5.export.CycloExporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CyclomaticComplexity {

    // Exporters used by the program.
    private static final CycloExporter[] EXPORTERS = new CycloExporter[] {
            new CSVCycloExporter(),
            new BarplotCycloExporter(),
    };

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println(
                    "error: no inputFile provided: please provide at least one inputFile path.");
            System.exit(1);
        }

        String path = args[0];
        String projectName = new File(path).getName();

        System.out.println("Analyzing the project...");

        // Run the analysis.
        List<CycloEntry> cycloEntries = new ArrayList<>();
        exploreDirectory(args[0], cycloEntries);

        // Export results.
        int exportersCount = EXPORTERS.length;
        for (int i = 0; i < exportersCount; i++) {
            System.out.printf("Exporting... [%d/%d]%n", i + 1, exportersCount);
            EXPORTERS[i].export(projectName, cycloEntries);
        }
    }

    private static void exploreDirectory(String path, List<CycloEntry> cycloEntries)
            throws IOException {
        File file = new File(path);

        // If directory, explore its sub directories.
        if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
            for (File child : file.listFiles())
                exploreDirectory(child.getPath(), cycloEntries);
            return;
        }

        // Otherwise parse file.
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (fileName.substring(dotIndex == -1 ? 0 : dotIndex)
                .equals(".java"))
            exploreCompilationUnit(file, cycloEntries);
    }

    private static void exploreCompilationUnit(File inputFile, List<CycloEntry> cycloEntries)
            throws FileNotFoundException {
        CompilationUnit unit = StaticJavaParser.parse(inputFile);

        // Get the package name.
        String packageName = unit.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElse("(default)");

        for (ClassOrInterfaceDeclaration intClass : unit.findAll(
                ClassOrInterfaceDeclaration.class))
            exploreClass(intClass, cycloEntries, packageName);
    }

    private static void exploreClass(ClassOrInterfaceDeclaration intClass,
            List<CycloEntry> cycloEntries,
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

            // Add the entry.
            cycloEntries.add(new CycloEntry(packageName, className, methodName, paramList,
                    cyclomaticComplexity));

            // Explore subclasses.
            for (FieldDeclaration field : intClass.getFields())
                field.ifClassOrInterfaceDeclaration(
                        cls -> exploreClass(cls, cycloEntries, packageName));
        }
    }

    private static int calculateCyclomaticComplexity(MethodDeclaration declaration) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        CyclomaticComplexityVisitor.CycloCounter counter =
                new CyclomaticComplexityVisitor.CycloCounter();

        declaration.accept(visitor, counter);

        return counter.getCyclomaticNumber();
    }

    public static class CycloEntry {
        public final String packageName;
        public final String className;
        public final String methodName;
        public final String paramList;
        public final int cyclomaticNumber;

        public CycloEntry(String packageName, String className, String methodName, String paramList,
                int cyclomaticNumber) {
            this.packageName = packageName;
            this.className = className;
            this.methodName = methodName;
            this.paramList = paramList;
            this.cyclomaticNumber = cyclomaticNumber;
        }
    }
}
