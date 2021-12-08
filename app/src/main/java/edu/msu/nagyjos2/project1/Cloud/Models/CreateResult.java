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

    @Attribute(name = "guestname", required = false)
    private String guestname;

    public String getMessage() { return message; }

    public String getStatus() { return status; }

    public String getGuestid() { return guestid; }

    public String getGuestname() { return guestname; }

    public CreateResult() {}

    public CreateResult(String status, String guestid, String guestname, String msg) {
        this.status = status;
        this.guestid = guestid;
        this.guestname = guestname;
        this.message = msg;
    }
}
