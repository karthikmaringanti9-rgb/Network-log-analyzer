package app;

import analyzer.NetworkLogAnalyzer;
import parser.CombinedLogParser;
import util.Config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Config config = Config.load(Paths.get("config.cfg"));
            NetworkLogAnalyzer analyzer = new NetworkLogAnalyzer(new CombinedLogParser(), config);
            while (true) {
                System.out.println("1) Parse");
                System.out.println("2) Report IPs");
                System.out.println("3) Error Trends");
                System.out.println("4) Export");
                System.out.println("5) Exit");
                String choice = scanner.nextLine().trim();
                if ("1".equals(choice)) {
                    System.out.print("access.log path [access.log]: ");
                    String p = scanner.nextLine().trim();
                    Path logPath = p.isEmpty() ? Paths.get("access.log") : Paths.get(p);
                    analyzer.parse(logPath);
                    System.out.println("Parsed");
                } else if ("2".equals(choice)) {
                    analyzer.printIpReport();
                } else if ("3".equals(choice)) {
                    analyzer.printErrorTrends();
                } else if ("4".equals(choice)) {
                    analyzer.export(Paths.get("ip_stats.csv"), Paths.get("errors.txt"));
                    System.out.println("Exported");
                } else if ("5".equals(choice)) {
                    break;
                }
            }
        }
    }
}

