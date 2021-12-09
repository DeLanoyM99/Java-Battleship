package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "game")
public class UpdateBoardResult {
    @Attribute
    private String status;

    @Attribute(name = "msg", required = false)
    private String msg;

    public String getStatus() { return status; }

    public String getMsg() { return msg; }

    public UpdateBoardResult() {}

}
