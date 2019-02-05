package com.myapi.exception;

public class TokenException extends Exception{
	private static final long serialVersionUID = 3404434161799702443L;
	private int code;
	public TokenException(int code, String msg) {
        super(msg);
        this.code = code;
    }
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
