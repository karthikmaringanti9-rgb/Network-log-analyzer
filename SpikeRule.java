package rules;

import model.LogEntry;
import util.Config;
import util.SlidingWindow;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpikeRule implements AnomalyRule {
    @Override
    public List<String> apply(List<LogEntry> entries, Config config) {
        List<String> result = new ArrayList<>();
        SlidingWindow window = new SlidingWindow(Duration.ofMinutes(config.windowMinutes()));
        entries.stream().sorted(Comparator.comparing(LogEntry::getTimestamp)).forEach(e -> {
            window.add(e);
            int count = window.count();
            if (count > config.threshold()) {
                Instant bucket = e.getTimestamp().truncatedTo(ChronoUnit.MINUTES);
                result.add("Spike " + bucket + " count " + count);
            }
        });
        return result;
    }
}

