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

    private JPanel ramPanel = new JPanel();
    private JPanel secondaryPanel = new JPanel();

    private Optimal optimal;
    private FIFO fifo;
    private LRU lru;
    private SimulationHistory history;
    private String currentAlgorithm = "";
    private int stepIndex = 0;

    public AppGUI() {
        setTitle("Page Replacement Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel memContainer = new JPanel(new GridLayout(2, 1, 10, 10));

        ramPanel.setBorder(BorderFactory.createTitledBorder("Memorie fizica"));
        secondaryPanel.setBorder(BorderFactory.createTitledBorder("Memorie secundara"));

        ramPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        secondaryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        memContainer.add(ramPanel);
        memContainer.add(secondaryPanel);

        add(memContainer, BorderLayout.NORTH);

        refreshSecondaryPanel();
        refreshRamPanelEmpty();


        JPanel buttons = new JPanel(new GridLayout(4, 2, 10, 10));
        JButton btnOptimal = new JButton("Run Optimal");
        JButton btnCharts = new JButton("Show Charts");
        JButton btnFIFO = new JButton("Run FIFO");
        JButton btnCustom = new JButton("Custom Sequence");
        JButton btnLRU = new JButton("Run LRU");
        JButton btnNew = new JButton("New Random Pages");
        JButton btnStep = new JButton("Step");
        JButton btnExit = new JButton("Exit");

        buttons.add(btnOptimal);
        buttons.add(btnCharts);
        buttons.add(btnFIFO);
        buttons.add(btnCustom);
        buttons.add(btnLRU);
        buttons.add(btnNew);
        buttons.add(btnStep);
        buttons.add(btnExit);

        add(buttons, BorderLayout.SOUTH);

        btnOptimal.addActionListener(e -> startAlgorithm("Optimal"));
        btnFIFO.addActionListener(e -> startAlgorithm("FIFO"));
        btnLRU.addActionListener(e -> startAlgorithm("LRU"));

        btnStep.addActionListener(e -> doStep());

        btnNew.addActionListener(e -> {
            pages = PageGenerator.generatePages(10, 13);
            frames = PageGenerator.generateFrameCount(2, 4);
            output.setText("Generated NEW random pages:\n" +
                    Arrays.toString(pages) +
                    "\nFrames: " + frames);

            refreshSecondaryPanel();
            refreshRamPanelEmpty();
        });

        btnCustom.addActionListener(e -> {
            String text = JOptionPane.showInputDialog(
                    this,
                    "Enter custom sequence (ex: 1 2 3 4 1 2 5 1 2 3):",
                    "Custom Sequence",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (text == null) return;
            text = text.trim();

            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Input empty!");
                return;
            }

            try {
                String[] parts = text.split("\\s+");
                int[] newPages = new int[parts.length];

                for (int i = 0; i < parts.length; i++)
                    newPages[i] = Integer.parseInt(parts[i]);

                pages = newPages;
                output.setText("Custom sequence set:\n" + Arrays.toString(pages));

                refreshSecondaryPanel();
                refreshRamPanelEmpty();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid format! Use NUMBERS only.");
            }
        });

        btnCharts.addActionListener(e -> ChartManager.showCharts());
        btnExit.addActionListener(e -> System.exit(0));

        setSize(1920, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startAlgorithm(String type) {
        currentAlgorithm = type;
        stepIndex = 0;

        optimal = null;
        fifo = null;
        lru = null;

        switch (type) {
            case "Optimal" -> optimal = new Optimal(pages, frames);
            case "FIFO" -> fifo = new FIFO(pages, frames);
            case "LRU" -> lru = new LRU(pages, frames);
        }

        history = new SimulationHistory(type);

        output.setText(type + " started.\n");
        refreshSecondaryPanel();
        refreshRamPanelEmpty();
    }

    private void doStep() {
        StepResult result = null;

        if (currentAlgorithm.equals("Optimal") && optimal != null)
            result = optimal.step();
        else if (currentAlgorithm.equals("FIFO") && fifo != null)
            result = fifo.step();
        else if (currentAlgorithm.equals("LRU") && lru != null)
            result = lru.step();
        else {
            output.append("Select an algorithm first.\n");
            return;
        }

        if (result == null) {
            output.append("Simulation finished.\n");
            history.close();
            return;
        }

        history.logStep(pages[stepIndex], result);

        output.append(printStep(result) + "\n");

        refreshRamPanelWithResult(result);
        highlightSecondary(stepIndex);

        stepIndex++;
    }

    private void highlightSecondary(int index) {
        secondaryPanel.removeAll();

        for (int i = 0; i < pages.length; i++) {
            JLabel lbl = new JLabel(String.valueOf(pages[i]));
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(35, 35));
            lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            if (i == index) lbl.setBackground(Color.YELLOW);
            else lbl.setBackground(Color.LIGHT_GRAY);

            secondaryPanel.add(lbl);
        }

        secondaryPanel.revalidate();
        secondaryPanel.repaint();
    }

    private void refreshSecondaryPanel() {
        secondaryPanel.removeAll();

        for (int p : pages) {
            JLabel lbl = new JLabel(String.valueOf(p));
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(35, 35));
            lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            lbl.setBackground(Color.LIGHT_GRAY);

            secondaryPanel.add(lbl);
        }

        secondaryPanel.revalidate();
        secondaryPanel.repaint();
    }

    private void refreshRamPanelEmpty() {
        ramPanel.removeAll();

        for (int i = 0; i < frames; i++) {
            JLabel lbl = new JLabel("-");
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(50, 50));
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            lbl.setBackground(Color.WHITE);

            ramPanel.add(lbl);
        }

        ramPanel.revalidate();
        ramPanel.repaint();
    }

    private void refreshRamPanelWithResult(StepResult step) {
        ramPanel.removeAll();
        int total = frames;
        int used = step.frames.size();

        for (int i = 0; i < used; i++) {
            Object f = step.frames.get(i);

            JLabel lbl = new JLabel(String.valueOf(f));
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(50, 50));
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            if (step.hit)
                lbl.setBackground(new Color(52, 189, 35));
            else
                lbl.setBackground(new Color(255, 143, 94));

            ramPanel.add(lbl);
        }

        for (int i = used; i < total; i++) {
            JLabel lbl = new JLabel("-");
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(50, 50));
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            lbl.setBackground(Color.WHITE);

            ramPanel.add(lbl);
        }

        ramPanel.revalidate();
        ramPanel.repaint();
    }


    private String printStep(StepResult step) {
        return "Frames: " + step.frames +
                " | Hit: " + step.hit +
                " | Hits: " + step.hits +
                " | Misses: " + step.misses;
    }
}
