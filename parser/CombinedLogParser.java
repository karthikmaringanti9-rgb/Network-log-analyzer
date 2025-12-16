package parser;

import exceptions.FileEmptyException;
import exceptions.MalformedLogException;
import model.LogEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CombinedLogParser extends LogParser {
    private static final Pattern PATTERN = Pattern.compile("^(\\S+) \\S+ \\S+ \\[(.*?)\\] \"(.*?)\" (\\d{3}) (\\S+) \"(.*?)\" \"(.*?)\"$");
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    @Override
    public List<LogEntry> parse(Path path) throws MalformedLogException, FileEmptyException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new MalformedLogException("Cannot read file");
        }
        if (lines.isEmpty()) throw new FileEmptyException("Empty file");
        List<LogEntry> entries = new ArrayList<>();
        int idx = 0;
        for (String line : lines) {
            idx++;
            Matcher m = PATTERN.matcher(line);
            if (!m.matches()) throw new MalformedLogException("Malformed line " + idx);
            String ip = m.group(1);
            String ts = m.group(2);
            String req = m.group(3);
            String[] parts = req.split(" ");
            String method = parts.length > 0 ? parts[0] : "";
            String endpoint = parts.length > 1 ? parts[1] : "";
            int status = Integer.parseInt(m.group(4));
            String sizeStr = m.group(5);
            long size = sizeStr.equals("-") ? 0 : Long.parseLong(sizeStr);
            ZonedDateTime zdt = ZonedDateTime.parse(ts, DTF);
            Instant instant = zdt.withZoneSameInstant(ZoneOffset.UTC).toInstant();
            entries.add(new LogEntry(ip, instant, method, endpoint, status, size));
        }
        return entries;
    }
}

