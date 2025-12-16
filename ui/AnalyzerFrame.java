package ui;

import analyzer.NetworkLogAnalyzer;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AnalyzerFrame extends JFrame {
    private final NetworkLogAnalyzer analyzer;
    private final JTextArea output = new JTextArea();
    private final JTextField logPath = new JTextField("access.log");

    public AnalyzerFrame(NetworkLogAnalyzer analyzer) {
        this.analyzer = analyzer;
        setTitle("Network Log Analyzer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        output.setEditable(false);
        JPanel top = new JPanel(new BorderLayout(8, 8));
        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> chooseFile());
        top.add(new JLabel("Log Path:"), BorderLayout.WEST);
        top.add(logPath, BorderLayout.CENTER);
        top.add(browse, BorderLayout.EAST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton parse = new JButton("Parse");
        JButton ipReport = new JButton("Report IPs");
        JButton errorTrends = new JButton("Error Trends");
        JButton export = new JButton("Export");
        parse.addActionListener(e -> onParse());
        ipReport.addActionListener(e -> onIpReport());
        errorTrends.addActionListener(e -> onErrorTrends());
        export.addActionListener(e -> onExport());
        actions.add(parse);
        actions.add(ipReport);
        actions.add(errorTrends);
        actions.add(export);
        setLayout(new BorderLayout(8, 8));
        add(top, BorderLayout.NORTH);
        add(actions, BorderLayout.SOUTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            logPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onParse() {
        Path p = logPath.getText().trim().isEmpty() ? Paths.get("access.log") : Paths.get(logPath.getText().trim());
        analyzer.parse(p);
        output.setText("Parsed\n");
    }

    private void onIpReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("ip,count\n");
        List<Map.Entry<String, Long>> sorted = analyzer.ipCountsSorted();
        for (Map.Entry<String, Long> e : sorted) sb.append(e.getKey()).append(",").append(e.getValue()).append("\n");
        output.setText(sb.toString());
    }

    private void onErrorTrends() {
        StringBuilder sb = new StringBuilder();
        double rate = analyzer.errorRatePublic();
        sb.append("error_rate=").append(String.format("%.4f", rate)).append("\n");
        Map<Instant, Long> perMinute = analyzer.errorCountsPerMinutePublic();
        perMinute.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
            sb.append(e.getKey()).append(",").append(e.getValue()).append("\n");
        });
        for (String a : analyzer.anomalyLines()) sb.append(a).append("\n");
        output.setText(sb.toString());
    }

    private void onExport() {
        analyzer.export(Paths.get("ip_stats.csv"), Paths.get("errors.txt"));
        output.setText("Exported\n");
    }
}

