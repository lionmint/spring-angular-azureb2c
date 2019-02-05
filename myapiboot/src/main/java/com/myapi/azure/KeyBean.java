package com.myapi.azure;

import java.io.Serializable;

public class KeyBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4551262373054581742L;
	private String kty;
	private String use;
	private String kid;
	private String x5t;
	private String n;
	private String e;
	private java.util.List<String> x5c;
	private String issuer;
	public String getKty() {
		return kty;
	}
	public void setKty(String kty) {
		this.kty = kty;
	}
	public String getUse() {
		return use;
	}
	public void setUse(String use) {
		this.use = use;
	}
	public String getKid() {
		return kid;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}
	public String getX5t() {
		return x5t;
	}
	public void setX5t(String x5t) {
		this.x5t = x5t;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getE() {
		return e;
	}
	public void setE(String e) {
		this.e = e;
	}
	public java.util.List<String> getX5c() {
		return x5c;
	}
	public void setX5c(java.util.List<String> x5c) {
		this.x5c = x5c;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
}
