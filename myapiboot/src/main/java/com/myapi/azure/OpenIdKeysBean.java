package com.myapi.azure;

import java.io.Serializable;

public class OpenIdKeysBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2846155967301623882L;
	private java.util.List<KeyBean> keys;
	public java.util.List<KeyBean> getKeys() {
		return keys;
	}

	public void setKeys(java.util.List<KeyBean> keys) {
		this.keys = keys;
	}
}
