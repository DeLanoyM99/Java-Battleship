package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "lobby") // root xml node
public class CreateResult {
    @Attribute
    private String status;

    @Attribute(name = "msg", required = false)
    private String message;

    @Attribute(name = "guestid", required = false)
    private String guestid;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGuestid() { return guestid; }

    public CreateResult() {}

    public CreateResult(String status, String msg) {
        this.status = status;
        this.message = msg;
    }
}
