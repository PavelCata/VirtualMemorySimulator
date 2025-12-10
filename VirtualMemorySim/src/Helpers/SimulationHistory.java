package Helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimulationHistory {

    private FileWriter writerSingleRun;
    private int step = 0;
    private String algorithm;

    private static int runCounterOptimal = 1;
    private static int runCounterFIFO = 1;
    private static int runCounterLRU = 1;

    public SimulationHistory(String algorithm) {
        this.algorithm = algorithm;

        File dir = new File("runs");
        if (!dir.exists()) dir.mkdir();

        String algoLower = algorithm.toLowerCase();
        int runIndex = getRunIndex(algorithm);

        String fileName = "runs/" + algoLower + "_run_" + runIndex + ".csv";

        try {
            writerSingleRun = new FileWriter(fileName, false);
            writerSingleRun.write("Step,Page,Hit,Miss,Hits,Misses\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getRunIndex(String algo) {
        return switch (algo) {
            case "Optimal" -> runCounterOptimal++;
            case "FIFO" -> runCounterFIFO++;
            case "LRU" -> runCounterLRU++;
            default -> 1;
        };
    }

    public void logStep(int page, StepResult stepResult) {
        try {
            step++;

            String line =
                    step + "," +
                            page + "," +
                            stepResult.hit + "," +
                            (!stepResult.hit) + "," +
                            stepResult.hits + "," +
                            stepResult.misses + "\n";

            writerSingleRun.write(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (writerSingleRun != null) writerSingleRun.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
