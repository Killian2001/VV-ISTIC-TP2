package fr.istic.vv_tp2.ex5.export;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import fr.istic.vv_tp2.ex5.CyclomaticComplexity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Exports data into a bar plot, which is sorted by descending cyclomatic complexities.
 */
public class BarplotCycloExporter implements CycloExporter {

    private static final int EXPORT_WIDTH = 800;
    private static final int EXPORT_HEIGHT = 600;

    @Override
    public void export(String file, List<CyclomaticComplexity.CycloEntry> cycloEntries) throws
            IOException {
        // Sort data by descending order.
        cycloEntries.sort((entry1, entry2) -> Integer.compare(entry2.cyclomaticNumber,
                entry1.cyclomaticNumber));

        // Produce the dataset.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (CyclomaticComplexity.CycloEntry entry : cycloEntries)
            dataset.addValue(entry.cyclomaticNumber, "Cyclomatic Complexity", entry.methodName);

        // Produce the chart.
        JFreeChart chart =
                ChartFactory.createBarChart(file, "Method", "Cyclomatic Complexity", dataset,
                        PlotOrientation.VERTICAL, false, false, false);

        // Export the chart.
        File outputFile = new File(String.format("%s_chart.png", file));
        ChartUtils.saveChartAsPNG(outputFile, chart, EXPORT_WIDTH, EXPORT_HEIGHT);
    }
}
