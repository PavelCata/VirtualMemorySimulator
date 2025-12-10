package MainInterface;

import Algorithms.FIFO;
import Algorithms.LRU;
import Algorithms.Optimal;
import Helpers.ChartManager;
import Helpers.PageGenerator;
import Helpers.StepResult;
import Helpers.SimulationHistory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class AppGUI extends JFrame {
    private Timer autoTimer;
    private static final Color BG_MAIN = new Color(0xF4F4F7);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xD0D0D5);
    private static final Color TEXT_COLOR = new Color(0x222222);

    private static final Color SECONDARY_DEFAULT_BG = new Color(0xE4E4EA);
    private static final Color SECONDARY_CURRENT_BG = new Color(0xFFE7A0);

    private static final Color FRAME_EMPTY_BG = Color.WHITE;
    private static final Color FRAME_HIT_BG = new Color(0xCDECCB);
    private static final Color FRAME_MISS_BG = new Color(0xFAD0C3);

    private static final Color BUTTON_BG = new Color(0xE0E2F3);
    private static final Color BUTTON_BG_HOVER = new Color(0xD1D4F0);

    private final Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font monoFont = new Font("Consolas", Font.PLAIN, 13);

    private int[] pages = PageGenerator.generatePages(12, 15);
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
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BG_MAIN);

        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel memContainer = new JPanel(new GridLayout(2, 1, 10, 10));
        memContainer.setOpaque(false);

        styleSectionPanel(ramPanel, "Memorie fizica");
        styleSectionPanel(secondaryPanel, "Memorie secundara");

        ramPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        secondaryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        memContainer.add(ramPanel);
        memContainer.add(secondaryPanel);

        add(memContainer, BorderLayout.NORTH);

        refreshSecondaryPanel();
        refreshRamPanelEmpty();

        JPanel outputCard = new JPanel(new BorderLayout());
        outputCard.setBackground(CARD_BG);
        outputCard.setBorder(createTitledCardBorder("Detalii simulare"));

        output.setEditable(false);
        output.setFont(monoFont);
        output.setForeground(TEXT_COLOR);
        output.setBackground(Color.WHITE);
        output.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        outputCard.add(scroll, BorderLayout.CENTER);

        add(outputCard, BorderLayout.CENTER);

        JPanel controlsContainer = new JPanel(new BorderLayout(10, 10));
        controlsContainer.setOpaque(false);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonsPanel.setOpaque(false);

        JPanel algoPanel = createButtonGroupPanel("Algoritmi");
        JButton btnOptimal = createButton("Run Optimal");
        JButton btnFIFO = createButton("Run FIFO");
        JButton btnLRU = createButton("Run LRU");
        algoPanel.add(btnOptimal);
        algoPanel.add(btnFIFO);
        algoPanel.add(btnLRU);

        JPanel simPanel = createButtonGroupPanel("Simulare");
        JButton btnExit = createButton("EXIT");
        JButton btnStart = createButton("Start Simulation");
        simPanel.add(btnStart);
        simPanel.add(btnExit);

        JPanel seqPanel = createButtonGroupPanel("Secvente");
        JButton btnCustom = createButton("Custom Sequence");
        JButton btnNew = createButton("New Random Pages");
        seqPanel.add(btnCustom);
        seqPanel.add(btnNew);

        JPanel analysisPanel = createButtonGroupPanel("Analiza");
        JButton btnCharts = createButton("Show Charts");
        analysisPanel.add(btnCharts);

        buttonsPanel.add(algoPanel);
        buttonsPanel.add(simPanel);
        buttonsPanel.add(seqPanel);
        buttonsPanel.add(analysisPanel);

        controlsContainer.add(buttonsPanel, BorderLayout.CENTER);
        add(controlsContainer, BorderLayout.SOUTH);

        btnOptimal.addActionListener(e -> startAlgorithm("Optimal"));
        btnFIFO.addActionListener(e -> startAlgorithm("FIFO"));
        btnLRU.addActionListener(e -> startAlgorithm("LRU"));


        btnStart.addActionListener(e -> {
            if (currentAlgorithm.isEmpty()) {
                output.append("Select an algorithm first.\n");
                return;
            }

            if (autoTimer != null && autoTimer.isRunning())
                return;

            autoTimer = new Timer(1200, ev -> doStep());
            autoTimer.start();

            output.append("Simulation running...\n");
        });


        btnNew.addActionListener(e -> {
            pages = PageGenerator.generatePages(12, 15);
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
                    "Enter custom sequence:",
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

                String fText = JOptionPane.showInputDialog(
                        this,
                        "Enter number of frames:",
                        "Frame Count",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (fText == null) return;
                fText = fText.trim();

                int newFrames = Integer.parseInt(fText);

                if (newFrames <= 0) {
                    JOptionPane.showMessageDialog(this, "Frame count must be > 0");
                    return;
                }

                frames = newFrames;

                output.setText(
                        "Custom sequence set:\n" + Arrays.toString(pages) +
                                "\nFrames: " + frames
                );

                refreshSecondaryPanel();
                refreshRamPanelEmpty();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid format! Use NUMBERS only.");
            }
        });


        btnCharts.addActionListener(e -> {
            String[] opts = {
                    "Charts for One Algorithm",
                    "Combined Average (All Algorithms)"
            };

            String choice = (String) JOptionPane.showInputDialog(
                    this,
                    "Select chart type:",
                    "Charts",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opts,
                    opts[0]
            );

            if (choice == null) return;

            switch (choice) {
                case "Charts for One Algorithm" -> {
                    String[] algos = {"FIFO", "LRU", "Optimal"};
                    String selectedAlgo = (String) JOptionPane.showInputDialog(
                            this,
                            "Choose algorithm:",
                            "Algorithm",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            algos,
                            algos[0]
                    );
                    if (selectedAlgo != null)
                        ChartManager.showLastRunChart(selectedAlgo);
                }

                case "Combined Average (All Algorithms)" ->
                        ChartManager.showCombinedAverageCharts();
            }
        });



        btnExit.addActionListener(e -> System.exit(0));

        setSize(1280, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));
        setVisible(true);
    }


    private void styleSectionPanel(JPanel panel, String title) {
        panel.setBackground(CARD_BG);
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP
        );
        tb.setTitleFont(titleFont);
        tb.setTitleColor(TEXT_COLOR);
        panel.setBorder(tb);
    }

    private TitledBorder createTitledCardBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP
        );
        tb.setTitleFont(titleFont);
        tb.setTitleColor(TEXT_COLOR);
        return tb;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(baseFont);
        btn.setBackground(BUTTON_BG);
        btn.setForeground(TEXT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BUTTON_BG_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BUTTON_BG);
            }
        });

        return btn;
    }

    private JPanel createButtonGroupPanel(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBackground(CARD_BG);
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP
        );
        tb.setTitleFont(titleFont);
        tb.setTitleColor(TEXT_COLOR);
        panel.setBorder(tb);
        return panel;
    }

    private void startAlgorithm(String type) {
        currentAlgorithm = type;
        stepIndex = 0;

        if (autoTimer != null && autoTimer.isRunning())
            autoTimer.stop();

        optimal = null;
        fifo = null;
        lru = null;

        switch (type) {
            case "Optimal" -> optimal = new Optimal(pages, frames);
            case "FIFO" -> fifo = new FIFO(pages, frames);
            case "LRU" -> lru = new LRU(pages, frames);
        }

        history = new SimulationHistory(type);

        output.setText(type + " loaded.\nPress START to begin simulation.\n");

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
            if (history != null) history.close();
            if (autoTimer != null) autoTimer.stop();
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
            lbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            lbl.setFont(baseFont);

            if (i == index) lbl.setBackground(SECONDARY_CURRENT_BG);
            else lbl.setBackground(SECONDARY_DEFAULT_BG);

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
            lbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            lbl.setBackground(SECONDARY_DEFAULT_BG);
            lbl.setFont(baseFont);

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
            lbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            lbl.setBackground(FRAME_EMPTY_BG);
            lbl.setFont(baseFont);

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
            lbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            lbl.setFont(baseFont);

            if (step.hit)
                lbl.setBackground(FRAME_HIT_BG);
            else
                lbl.setBackground(FRAME_MISS_BG);

            ramPanel.add(lbl);
        }

        for (int i = used; i < total; i++) {
            JLabel lbl = new JLabel("-");
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(50, 50));
            lbl.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            lbl.setBackground(FRAME_EMPTY_BG);
            lbl.setFont(baseFont);

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
