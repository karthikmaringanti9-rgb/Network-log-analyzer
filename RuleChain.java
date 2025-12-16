package rules;

import model.LogEntry;
import util.Config;

import java.util.ArrayList;
import java.util.List;

public class RuleChain {
    private final List<AnomalyRule> rules = new ArrayList<>();

    public RuleChain add(AnomalyRule rule) {
        rules.add(rule);
        return this;
    }

    public List<String> apply(List<LogEntry> entries, Config config) {
        List<String> out = new ArrayList<>();
        for (AnomalyRule r : rules) out.addAll(r.apply(entries, config));
        return out;
    }
}

