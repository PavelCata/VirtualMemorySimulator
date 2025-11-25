package MainInterface;

import Algorithms.FIFO;
import Algorithms.LRU;
import Algorithms.Optimal;
import Helpers.ChartManager;
import Helpers.PageGenerator;
import Helpers.StepResult;
import Helpers.SimulationHistory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AppGUI extends JFrame {

    private int[] pages = PageGenerator.generatePages(10, 13);
    private int frames = PageGenerator.generateFrameCount(2, 4);

    private JTextArea output = new JTextArea(15, 40);

    public AppGUI() {
        setTitle("Page Replacement Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(4, 2, 10, 10));

        JButton btnOptimal = new JButton("Run Optimal");
        JButton btnFIFO = new JButton("Run FIFO");
        JButton btnLRU = new JButton("Run LRU");
        JButton btnNew = new JButton("New Random Pages");
        JButton btnCustom = new JButton("Custom Sequence");
        JButton btnCharts = new JButton("Show Charts");
        JButton btnExit = new JButton("Exit");

        buttons.add(btnOptimal);
        buttons.add(btnFIFO);
        buttons.add(btnLRU);
        buttons.add(btnNew);
        buttons.add(btnCustom);
        buttons.add(btnCharts);
        buttons.add(btnExit);

        add(buttons, BorderLayout.SOUTH);

        btnOptimal.addActionListener(e -> runAlgorithm("Optimal"));
        btnFIFO.addActionListener(e -> runAlgorithm("FIFO"));
        btnLRU.addActionListener(e -> runAlgorithm("LRU"));

        btnNew.addActionListener(e -> {
            pages = PageGenerator.generatePages(10, 13);
            frames = PageGenerator.generateFrameCount(2, 4);
            output.setText("Generated NEW random pages:\n" +
                    Arrays.toString(pages) +
                    "\nFrames: " + frames);
        });

        btnCustom.addActionListener(e -> {
            String text = JOptionPane.showInputDialog(
                    this,
                    "Enter custom sequence (ex: 1 2 3 4 1 2 5 1 2 3):",
                    "Custom Sequence",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (text == null) return; // Cancel

            text = text.trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Input empty!");
                return;
            }

            String[] parts = text.split("\\s+");
            int[] newPages = new int[parts.length];

            try {
                for (int i = 0; i < parts.length; i++) {
                    newPages[i] = Integer.parseInt(parts[i]);
                }
                pages = newPages;
                output.setText("Custom sequence set:\n" + Arrays.toString(pages));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid format! Use NUMBERS only.");
            }
        });

        btnCharts.addActionListener(e -> ChartManager.showCharts());

        btnExit.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void runAlgorithm(String type) {
        SimulationHistory sh = new SimulationHistory(type);
        StepResult result;
        int stepIndex = 0;

        output.setText(type + " algorithm:\n");

        switch (type) {
            case "Optimal" -> {
                Optimal opt = new Optimal(pages, frames);
                while ((result = opt.step()) != null) {
                    sh.logStep(pages[stepIndex], result);
                    output.append(printStep(result) + "\n");
                    stepIndex++;
                }
            }
            case "FIFO" -> {
                FIFO fifo = new FIFO(pages, frames);
                while ((result = fifo.step()) != null) {
                    sh.logStep(pages[stepIndex], result);
                    output.append(printStep(result) + "\n");
                    stepIndex++;
                }
            }
            case "LRU" -> {
                LRU lru = new LRU(pages, frames);
                while ((result = lru.step()) != null) {
                    sh.logStep(pages[stepIndex], result);
                    output.append(printStep(result) + "\n");
                    stepIndex++;
                }
            }
        }

        sh.close();
    }

    private String printStep(StepResult step) {
        return "Frames: " + step.frames +
                " | Hit: " + step.hit +
                " | Hits: " + step.hits +
                " | Misses: " + step.misses;
    }
}
