package com.pavelalon.contactsviewer.sms;

import java.sql.Date;

public class SMS{
	public static int INBOX = 1;
	public static int OUTBOX = 2;
	
	private String address;
	private int messageType;
	private String body;
	private Date messageDate;

	public SMS() {
		// TODO Auto-generated constructor stub
	}

	public SMS(String address, int messageType, String body, long messageDate) {
		super();
		this.address = address;
		this.messageType = messageType;
		this.body = body;
		this.messageDate = new Date(messageDate);
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		if (messageType != INBOX || messageType != OUTBOX) {
			throw new RuntimeException("Incorrect message type: " + messageType);
		} else {

			this.messageType = messageType;
		}
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(long messageDate) {
		if (messageDate < 0) {
			throw new RuntimeException("Incorrect date: " + messageDate);
		} else {
			this.messageDate = new Date(messageDate);
		}
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "SMS [address=" + address + ", messageType=" + messageType
				+ ", body=" + body + ", messageDate=" + messageDate + "]";
	}

}
