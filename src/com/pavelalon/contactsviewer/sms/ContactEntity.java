package com.pavelalon.contactsviewer.sms;

public class ContactEntity {
	
	private int contactId;
	private int phoneLookupID;	// Used to get available phone numbers
	private String contactName;
	private String contactPrimaryPhone;
	
	
	
	public ContactEntity(int contactId, int phoneLookupID, String contactName,
			String contactPrimaryPhone) {
		super();
		this.contactId = contactId;
		this.phoneLookupID = phoneLookupID;
		this.contactName = contactName;
		this.contactPrimaryPhone = contactPrimaryPhone;
	}

	public ContactEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public ContactEntity(int contactId, String contactName) {
		super();
		this.contactId = contactId;
		this.contactName = contactName;
	}

	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPrimaryPhone() {
		return contactPrimaryPhone;
	}

	public void setContactPrimaryPhone(String contactPrimaryPhone) {
		this.contactPrimaryPhone = contactPrimaryPhone;
	}
	
	public int getPhoneLookupID() {
		return phoneLookupID;
	}

	public void setPhoneLookupID(int phoneLookupID) {
		this.phoneLookupID = phoneLookupID;
	}
}
