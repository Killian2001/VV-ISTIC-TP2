package fr.istic.vv_tp2.ex5.export;

import fr.istic.vv_tp2.ex5.CyclomaticComplexity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVCycloExporter implements CycloExporter {
    @Override
    public void export(String path, List<CyclomaticComplexity.CycloEntry> cycloEntries) throws
            IOException {
        File inputFile = new File(path);
        File outputFile = new File(inputFile.getName() + "_report_cc.csv");

        try (PrintWriter output = new PrintWriter(new FileOutputStream(outputFile))) {
            // Print file's header.
            output.println("Package,Declaring class,Method,Params,CC");

            for (CyclomaticComplexity.CycloEntry entry : cycloEntries) {
                String csvLine =
                        String.format("%s,%s,%s,%s,%s", entry.packageName, entry.className,
                                entry.methodName, entry.paramList,
                                entry.cyclomaticNumber);
                output.println(csvLine);
            }
        }
    }
}
