package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "battleship_user") // root xml node
public class SignupResult {

    @Attribute
    private String status; // will be a 'yes' or a 'no'

    @Attribute(name = "msg", required = false)
    private String msg; // will be "user exists" or "insertfail" IF status is no

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public SignupResult() {} // required by retrofit

    public SignupResult(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
