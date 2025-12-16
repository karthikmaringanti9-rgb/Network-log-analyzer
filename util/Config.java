package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private final Map<String, String> map;

    private Config(Map<String, String> map) { this.map = map; }

    public static Config load(Path path) {
        Map<String, String> m = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for (String l : lines) {
                String s = l.trim();
                if (s.isEmpty() || s.startsWith("#")) continue;
                int i = s.indexOf('=');
                if (i > 0) {
                    String k = s.substring(0, i).trim();
                    String v = s.substring(i + 1).trim();
                    m.put(k, v);
                }
            }
        } catch (IOException ignored) {}
        return new Config(m);
    }

    public int threshold() {
        String v = map.getOrDefault("threshold", "100");
        try { return Integer.parseInt(v); } catch (Exception e) { return 100; }
    }

    public int windowMinutes() {
        String v = map.getOrDefault("windowMinutes", "1");
        try { return Integer.parseInt(v); } catch (Exception e) { return 1; }
    }
}

