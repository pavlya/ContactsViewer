package com.pavelalon.contactsviewer.sms;

public class SmsMessageEntity {

	private String address;
	private String body;
	private long date;
	
	public SmsMessageEntity() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public SmsMessageEntity(String address, String body, long date) {
		super();
		this.address = address;
		this.body = body;
		this.date = date;
	}
	
	
}
