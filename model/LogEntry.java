package model;

import java.time.Instant;

public class LogEntry {
    private final String ip;
    private final Instant timestamp;
    private final String method;
    private final String endpoint;
    private final int status;
    private final long size;

    public LogEntry(String ip, Instant timestamp, String method, String endpoint, int status, long size) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.method = method;
        this.endpoint = endpoint;
        this.status = status;
        this.size = size;
    }

    public String getIp() { return ip; }
    public Instant getTimestamp() { return timestamp; }
    public String getMethod() { return method; }
    public String getEndpoint() { return endpoint; }
    public int getStatus() { return status; }
    public long getSize() { return size; }
}

