package Helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimulationHistory {
    private static final String LOG_FOLDER = "runs";
    private FileWriter writer;
    private int step = 0;

    public SimulationHistory(String algorithmName) {
        try {
            File dir = new File(LOG_FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            File file = new File(dir, algorithmName + "_" + timestamp + ".csv");

            writer = new FileWriter(file);
            writer.write("Step,Page,Hits,Misses\n");
        } catch (IOException e) {
            e.printStackTrace();
            writer = null;
        }
    }

    public void logStep(int page, StepResult result) {
        if (writer == null || result == null) return;
        step++;
        try {
            writer.write(step + "," + page + "," + result.hits + "," + result.misses + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
