package com.myapi.azure;

import java.io.Serializable;

public class OpenIdConfigurationBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4887630329284474407L;
	private String authorization_endpoint;
	private String token_endpoint;
	private java.util.List<String> token_endpoint_auth_methods_supported;
	private String jwks_uri;
	public String getAuthorization_endpoint() {
		return authorization_endpoint;
	}
	public void setAuthorization_endpoint(String authorization_endpoint) {
		this.authorization_endpoint = authorization_endpoint;
	}
	public String getToken_endpoint() {
		return token_endpoint;
	}
	public void setToken_endpoint(String token_endpoint) {
		this.token_endpoint = token_endpoint;
	}
	public java.util.List<String> getToken_endpoint_auth_methods_supported() {
		return token_endpoint_auth_methods_supported;
	}
	public void setToken_endpoint_auth_methods_supported(
			java.util.ArrayList<String> token_endpoint_auth_methods_supported) {
		this.token_endpoint_auth_methods_supported = token_endpoint_auth_methods_supported;
	}
	public String getJwks_uri() {
		return jwks_uri;
	}
	public void setJwks_uri(String jwks_uri) {
		this.jwks_uri = jwks_uri;
	}
}
