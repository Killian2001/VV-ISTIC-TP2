package fr.istic.vv_tp2.ex5.export;

import fr.istic.vv_tp2.ex5.CyclomaticComplexity;

import java.io.IOException;
import java.util.List;

public interface CycloExporter {
    void export(String path, List<CyclomaticComplexity.CycloEntry> cycloEntries) throws IOException;
}
