package fr.istic.vv_tp2.ex5.export;

import fr.istic.vv_tp2.ex5.CyclomaticComplexity;

import java.io.IOException;
import java.util.List;

/**
 * Defines operation over exporter objects, which export computed cyclomatic complexity data into a
 * file of a given format.
 */
public interface CycloExporter {
    /**
     * Exports data to a file.
     *
     * @param projectName  Name of the evaluated project.
     * @param cycloEntries Dataset of cyclomatic complexity entries computed.
     * @throws IOException If any I/O error occurs.
     */
    void export(String projectName, List<CyclomaticComplexity.CycloEntry> cycloEntries)
            throws IOException;
}
