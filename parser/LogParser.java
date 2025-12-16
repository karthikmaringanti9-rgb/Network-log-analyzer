package parser;

import exceptions.FileEmptyException;
import exceptions.MalformedLogException;
import model.LogEntry;

import java.nio.file.Path;
import java.util.List;

public abstract class LogParser {
    public abstract List<LogEntry> parse(Path path) throws MalformedLogException, FileEmptyException;
}

