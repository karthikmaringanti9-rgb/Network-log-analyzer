package app;

import analyzer.NetworkLogAnalyzer;
import parser.CombinedLogParser;
import ui.AnalyzerFrame;
import util.Config;

import javax.swing.*;
import java.nio.file.Paths;

public class GuiMain {
    public static void main(String[] args) {
        Config config = Config.load(Paths.get("config.cfg"));
        NetworkLogAnalyzer analyzer = new NetworkLogAnalyzer(new CombinedLogParser(), config);
        SwingUtilities.invokeLater(() -> new AnalyzerFrame(analyzer).setVisible(true));
    }
}

