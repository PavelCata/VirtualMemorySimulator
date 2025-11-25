package Helpers;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ChartManager {

    public static void showCharts() {
        File dir = new File("runs");
        if (!dir.exists()) {
            JOptionPane.showMessageDialog(null, "Folderul 'runs' nu exista!");
            return;
        }

        Map<String, List<File>> groups = new HashMap<>();
        groups.put("Optimal", new ArrayList<>());
        groups.put("FIFO", new ArrayList<>());
        groups.put("LRU", new ArrayList<>());

        for (File f : Objects.requireNonNull(dir.listFiles())) {
            String name = f.getName().toLowerCase();
            if (name.contains("optimal")) groups.get("Optimal").add(f);
            else if (name.contains("fifo")) groups.get("FIFO").add(f);
            else if (name.contains("lru")) groups.get("LRU").add(f);
        }

        XYSeriesCollection missesDataset = new XYSeriesCollection();
        missesDataset.addSeries(buildAverageSeries("Optimal", groups.get("Optimal"), 3));
        missesDataset.addSeries(buildAverageSeries("FIFO", groups.get("FIFO"), 3));
        missesDataset.addSeries(buildAverageSeries("LRU", groups.get("LRU"), 3));

        JFreeChart missesChart = ChartFactory.createXYLineChart(
                "Average Misses per Step",
                "Step",
                "Misses",
                missesDataset
        );

        styleChart(missesChart);

        JFrame f1 = new JFrame("Average Misses");
        f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f1.add(new ChartPanel(missesChart));
        f1.pack();
        f1.setLocationRelativeTo(null);
        f1.setVisible(true);

        XYSeriesCollection hitsDataset = new XYSeriesCollection();
        hitsDataset.addSeries(buildAverageSeries("Optimal", groups.get("Optimal"), 2));
        hitsDataset.addSeries(buildAverageSeries("FIFO", groups.get("FIFO"), 2));
        hitsDataset.addSeries(buildAverageSeries("LRU", groups.get("LRU"), 2));

        JFreeChart hitsChart = ChartFactory.createXYLineChart(
                "Average Hits per Step",
                "Step",
                "Hits",
                hitsDataset
        );

        styleChart(hitsChart);

        JFrame f2 = new JFrame("Average Hits");
        f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f2.add(new ChartPanel(hitsChart));
        f2.pack();
        f2.setLocationRelativeTo(null);
        f2.setVisible(true);
    }

    private static XYSeries buildAverageSeries(String title, List<File> files, int columnIndex) {
        XYSeries series = new XYSeries(title);

        if (files.isEmpty()) {
            return series;
        }

        int steps = 10;
        double[] sum = new double[steps];
        int fileCount = files.size();

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine(); // header

                for (int i = 0; i < steps; i++) {
                    String line = br.readLine();
                    if (line == null) break;

                    String[] p = line.split(",");
                    double value = Double.parseDouble(p[columnIndex].trim());
                    sum[i] += value;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < steps; i++) {
            double avg = sum[i] / fileCount;
            series.add(i + 1, avg);
        }

        return series;
    }

    private static void styleChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(3f));

        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(1, new BasicStroke(3f));

        renderer.setSeriesPaint(2, Color.GREEN);
        renderer.setSeriesStroke(2, new BasicStroke(3f));

        renderer.setDefaultShapesVisible(false);
        plot.setRenderer(renderer);
    }
}
