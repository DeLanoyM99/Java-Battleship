package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "battleship_user") // root xml node
public class LoginResult {

    @Attribute
    private String status; // will be a 'yes' or a 'no'

    @Attribute(name = "msg", required = false)
    private String msg; // will be "user" or "password" IF status is no

    @Attribute(name = "user_id", required = false)
    private String user_id; // will be "user" or "password" IF status is no

    public String getUserId() {return user_id;}

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public LoginResult() {} // required by retrofit

    public LoginResult(String status, String msg, String userid) {
        this.status = status;
        this.msg = msg;
        this.user_id = userid;
    }
}
