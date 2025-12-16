package analyzer;

import exceptions.FileEmptyException;
import exceptions.MalformedLogException;
import model.LogEntry;
import parser.LogParser;
import rules.RuleChain;
import rules.SpikeRule;
import util.Config;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetworkLogAnalyzer {
    private final LogParser parser;
    private final Config config;
    private List<LogEntry> entries = new ArrayList<>();

    public NetworkLogAnalyzer(LogParser parser, Config config) {
        this.parser = parser;
        this.config = config;
    }

    public void parse(Path path) {
        try {
            entries = parser.parse(path);
        } catch (MalformedLogException | FileEmptyException e) {
            System.out.println(e.getMessage());
            entries = new ArrayList<>();
        }
    }

    public void printIpReport() {
        Map<String, Long> counts = entries.stream().collect(Collectors.groupingBy(LogEntry::getIp, Collectors.counting()));
        List<Map.Entry<String, Long>> sorted = counts.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList());
        for (Map.Entry<String, Long> e : sorted) System.out.println(e.getKey() + "," + e.getValue());
    }

    public void printErrorTrends() {
        Map<Instant, Long> perMinute = errorCountsPerMinute();
        double rate = errorRate();
        List<String> anomalies = new RuleChain().add(new SpikeRule()).apply(entries, config);
        System.out.println("error_rate=" + String.format("%.4f", rate));
        List<Map.Entry<Instant, Long>> sorted = perMinute.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
        for (Map.Entry<Instant, Long> e : sorted) System.out.println(e.getKey() + "," + e.getValue());
        for (String a : anomalies) System.out.println(a);
    }

    public void export(Path ipCsv, Path errorsTxt) {
        Map<String, Long> counts = entries.stream().collect(Collectors.groupingBy(LogEntry::getIp, Collectors.counting()));
        List<Map.Entry<String, Long>> sorted = counts.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList());
        try (BufferedWriter w = Files.newBufferedWriter(ipCsv)) {
            w.write("ip,count\n");
            for (Map.Entry<String, Long> e : sorted) {
                w.write(e.getKey() + "," + e.getValue());
                w.newLine();
            }
        } catch (IOException ignored) {}
        Map<Instant, Long> perMinute = errorCountsPerMinute();
        double rate = errorRate();
        List<String> anomalies = new RuleChain().add(new SpikeRule()).apply(entries, config);
        try (BufferedWriter w = Files.newBufferedWriter(errorsTxt)) {
            w.write("error_rate=" + String.format("%.4f", rate));
            w.newLine();
            List<Map.Entry<Instant, Long>> sortedErr = perMinute.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
            for (Map.Entry<Instant, Long> e : sortedErr) {
                w.write(e.getKey() + "," + e.getValue());
                w.newLine();
            }
            for (String a : anomalies) {
                w.write(a);
                w.newLine();
            }
        } catch (IOException ignored) {}
    }

    private Map<Instant, Long> errorCountsPerMinute() {
        Map<Instant, Long> map = new HashMap<>();
        entries.stream().filter(e -> e.getStatus() >= 400).forEach(e -> {
            Instant bucket = e.getTimestamp().truncatedTo(ChronoUnit.MINUTES);
            map.put(bucket, map.getOrDefault(bucket, 0L) + 1);
        });
        return map;
    }

    private double errorRate() {
        long total = entries.size();
        long errors = entries.stream().filter(e -> e.getStatus() >= 400).count();
        if (total == 0) return 0.0;
        return (double) errors / (double) total;
    }

    public List<Map.Entry<String, Long>> ipCountsSorted() {
        Map<String, Long> counts = entries.stream().collect(Collectors.groupingBy(LogEntry::getIp, Collectors.counting()));
        return counts.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList());
    }

    public Map<Instant, Long> errorCountsPerMinutePublic() {
        return errorCountsPerMinute();
    }

    public double errorRatePublic() {
        return errorRate();
    }

    public List<String> anomalyLines() {
        return new RuleChain().add(new SpikeRule()).apply(entries, config);
    }
}
