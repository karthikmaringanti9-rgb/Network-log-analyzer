package rules;

import model.LogEntry;
import util.Config;

import java.util.List;

public interface AnomalyRule {
    List<String> apply(List<LogEntry> entries, Config config);
}

