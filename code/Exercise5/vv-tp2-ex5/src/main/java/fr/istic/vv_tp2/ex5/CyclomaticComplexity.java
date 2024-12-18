package fr.istic.vv_tp2.ex5;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import fr.istic.vv_tp2.ex5.export.BarplotCycloExporter;
import fr.istic.vv_tp2.ex5.export.CSVCycloExporter;
import fr.istic.vv_tp2.ex5.export.CycloExporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class of the Cyclomatic Complexity calculator.
 */
public class CyclomaticComplexity {

    // Exporters used by the program.
    private static final CycloExporter[] EXPORTERS = new CycloExporter[] {
            new CSVCycloExporter(),
            new BarplotCycloExporter(),
    };

    /**
     * Cyclomatic Complexity calculator entry point.
     *
     * @param args Program's arguments : should contain the folder of a project.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Explore recursively a directory in order to find <code>.java</code> files
     * in it, and calculate cyclomatic complexity of their methods.
     *
     * @param path         Directory's path.
     * @param cycloEntries List of cyclomatic complexity entries, to be expanded
     *                     by complexity measurements of methods found in Java files inside the
     *                     directory.
     * @throws IOException If any I/O error occur.
     */
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
                .equals(".java")) {
            CompilationUnit unit = StaticJavaParser.parse(file);

            // Create the compilation unit visitor.
            CyclomaticUnitVisitor visitor = new CyclomaticUnitVisitor();
            CyclomaticUnitVisitor.CycloUnitParam param =
                    new CyclomaticUnitVisitor.CycloUnitParam(cycloEntries);

            unit.accept(visitor, param);
        }
    }

    /**
     * Represent a cyclomatic complexity measure entry.
     */
    public static class CycloEntry {
        /**
         * Package of the class of the evaluated method.
         */
        public final String packageName;

        /**
         * Name of the class of the evaluated method.
         */
        public final String className;

        /**
         * Name of the evaluated method.
         */
        public final String methodName;

        /**
         * Parameters list of the evaluated method.
         */
        public final String paramList;

        /**
         * Cyclomatic number found for the evaluated method.
         */
        public final int cyclomaticNumber;

        /**
         * Constructor of the class.
         *
         * @param packageName      Package of the class of the evaluated method.
         * @param className        Name of the class of the evaluated method.
         * @param methodName       Name of the class of the evaluated method.
         * @param paramList        Parameters list of the evaluated method.
         * @param cyclomaticNumber Cyclomatic number found for the evaluated method.
         */
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
