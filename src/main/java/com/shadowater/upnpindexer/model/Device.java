package com.shadowater.upnpindexer.model;

import java.net.URL;

import org.jupnp.model.types.DeviceType;
import org.jupnp.model.types.UDN;

public class Device {
	private UDN udn;
	private String friendlyName;
	private URL baseURL;
	private DeviceType deviceType;
	
	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public UDN getUdn() {
		return udn;
	}

	public void setUdn(UDN udn) {
		this.udn = udn;
	}

	public URL getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
}
