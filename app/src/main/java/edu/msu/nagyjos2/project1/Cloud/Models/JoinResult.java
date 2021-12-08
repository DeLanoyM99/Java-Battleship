package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "lobby") // root xml node
public class JoinResult {
    @Attribute
    private String status;

    @Attribute(name = "msg", required = false)
    private String message;

    @Attribute(name = "hostname", required = false)
    private String hostname;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getHostname() { return hostname; }

    public JoinResult() {}

    public JoinResult(String status, String hostname, String msg) {
        this.status = status;
        this.hostname = hostname;
        this.message = msg;
    }
}

