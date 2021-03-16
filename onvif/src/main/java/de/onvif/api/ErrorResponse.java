package de.onvif.api;


public class ErrorResponse {
    private String result="1";
    private String errormsg;

    public ErrorResponse(String errormsg) {
        this.errormsg = errormsg;
    }
}