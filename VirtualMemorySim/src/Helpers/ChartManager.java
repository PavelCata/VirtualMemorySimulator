package Helpers;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ChartManager {


    public static void showLastRunChart(String algo) {
        File dir = new File("runs");
        if (!dir.exists()) {
            JOptionPane.showMessageDialog(null, "Folderul 'runs' nu exista!");
            return;
        }

        File[] files = dir.listFiles((d, name) ->
                name.toLowerCase().startsWith(algo.toLowerCase() + "_run_")
        );

        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(null,
                    "Nu exista rulari salvate pentru " + algo);
            return;
        }

        Arrays.sort(files);
        File last = files[files.length - 1];

        showChartsFor(last.getAbsolutePath(), algo + " (Last Run)");
    }



    public static void showChartsFor(String csvFile, String algoName) {

        XYSeries hits = new XYSeries("Hits");
        XYSeries misses = new XYSeries("Misses");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine();
            String line;
            int step = 1;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                hits.add(step, Integer.parseInt(p[4].trim()));
                misses.add(step, Integer.parseInt(p[5].trim()));
                step++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        plotSingleChart(algoName + " - Hits", "Hits", hits);
        plotSingleChart(algoName + " - Misses", "Misses", misses);
    }

    private static void plotSingleChart(String title, String yLabel, XYSeries series) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Step",
                yLabel,
                dataset
        );
        styleChart(chart);

        JFrame f = new JFrame(title);
        f.add(new ChartPanel(chart));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static void styleChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();

        r.setSeriesPaint(0, Color.BLUE);
        r.setSeriesStroke(0, new BasicStroke(3f));
        r.setDefaultShapesVisible(false);
        plot.setRenderer(r);
    }


    public static void showCombinedAverageCharts() {

        XYSeries avgFIFO_H = computeAverageForAlgo("FIFO", 4, "FIFO Avg Hits");
        XYSeries avgLRU_H  = computeAverageForAlgo("LRU", 4, "LRU Avg Hits");
        XYSeries avgOPT_H  = computeAverageForAlgo("Optimal", 4, "Optimal Avg Hits");

        XYSeriesCollection hitsDS = new XYSeriesCollection();
        hitsDS.addSeries(avgFIFO_H);
        hitsDS.addSeries(avgLRU_H);
        hitsDS.addSeries(avgOPT_H);

        JFreeChart hitsChart = ChartFactory.createXYLineChart(
                "Average Hits per Step (All Algorithms)",
                "Step",
                "Hits",
                hitsDS
        );
        styleMultiChart(hitsChart);

        JFrame fh = new JFrame("Comparative Average Hits");
        fh.add(new ChartPanel(hitsChart));
        fh.pack();
        fh.setLocationRelativeTo(null);
        fh.setVisible(true);

        XYSeries avgFIFO_M = computeAverageForAlgo("FIFO", 5, "FIFO Avg Miss");
        XYSeries avgLRU_M  = computeAverageForAlgo("LRU", 5, "LRU Avg Miss");
        XYSeries avgOPT_M  = computeAverageForAlgo("Optimal", 5, "Optimal Avg Miss");

        XYSeriesCollection missDS = new XYSeriesCollection();
        missDS.addSeries(avgFIFO_M);
        missDS.addSeries(avgLRU_M);
        missDS.addSeries(avgOPT_M);

        JFreeChart missChart = ChartFactory.createXYLineChart(
                "Average Misses per Step (All Algorithms)",
                "Step",
                "Misses",
                missDS
        );
        styleMultiChart(missChart);

        JFrame fm = new JFrame("Comparative Average Misses");
        fm.add(new ChartPanel(missChart));
        fm.pack();
        fm.setLocationRelativeTo(null);
        fm.setVisible(true);
    }


    private static XYSeries computeAverageForAlgo(String algo, int col, String title) {

        File dir = new File("runs");
        String prefix = algo.toLowerCase() + "_run_";
        File[] files = dir.listFiles((d, name) ->
                name.toLowerCase().startsWith(prefix)
        );

        return computeAverage(files, title, col);
    }


    private static XYSeries computeAverage(File[] files, String title, int colIndex) {

        XYSeries result = new XYSeries(title);
        if (files == null || files.length == 0) return result;

        ArrayList<ArrayList<Integer>> allRuns = new ArrayList<>();

        for (File f : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {

                br.readLine();
                String line;
                ArrayList<Integer> vals = new ArrayList<>();

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    vals.add(Integer.parseInt(parts[colIndex].trim()));
                }

                allRuns.add(vals);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int maxSteps = allRuns.stream().mapToInt(ArrayList::size).max().orElse(0);

        for (int i = 0; i < maxSteps; i++) {
            double sum = 0;
            int count = 0;

            for (var run : allRuns) {
                if (i < run.size()) {
                    sum += run.get(i);
                    count++;
                }
            }

            if (count > 0)
                result.add(i + 1, sum / count);
        }

        return result;
    }

    private static void styleMultiChart(JFreeChart chart) {
        XYPlot p = chart.getXYPlot();
        XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();

        r.setSeriesPaint(0, Color.RED);
        r.setSeriesPaint(1, Color.BLUE);
        r.setSeriesPaint(2, Color.GREEN);

        r.setSeriesStroke(0, new BasicStroke(3f));
        r.setSeriesStroke(1, new BasicStroke(3f));
        r.setSeriesStroke(2, new BasicStroke(3f));

        r.setDefaultShapesVisible(false);
        p.setRenderer(r);
    }
}
