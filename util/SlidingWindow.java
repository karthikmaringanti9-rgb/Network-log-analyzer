package util;

import model.LogEntry;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindow {
    private final Duration window;
    private final Deque<LogEntry> deque = new ArrayDeque<>();

    public SlidingWindow(Duration window) {
        this.window = window;
    }

    public void add(LogEntry e) {
        deque.addLast(e);
        prune(e);
    }

    public int count() {
        return deque.size();
    }

    private void prune(LogEntry latest) {
        while (!deque.isEmpty()) {
            LogEntry first = deque.getFirst();
            if (latest.getTimestamp().minus(window).isAfter(first.getTimestamp())) deque.removeFirst();
            else break;
        }
    }
}

