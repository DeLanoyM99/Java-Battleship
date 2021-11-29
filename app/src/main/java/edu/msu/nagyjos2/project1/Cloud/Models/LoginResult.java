package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "battleship_user") // root xml node
public class LoginResult {

    @Attribute
    private String status; // will be a 'yes' or a 'no'

    @Attribute
    private String msg; // will be "user" or "password" IF status is no

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    /*
    public void setStatus(String status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    */

    public LoginResult() {} // required by retrofit

    public LoginResult(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
