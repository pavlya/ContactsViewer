package com.pavelalon.contactsviewer.sms;

import java.util.Comparator;

public class SmsDateComparator implements Comparator<SMS> {

	@Override
	public int compare(SMS sms1, SMS sms2) {
		// ascending
		return sms1.getMessageDate().compareTo(sms2.getMessageDate());
		// descending
//		return sms2.getMessageDate().compareTo(sms1.getMessageDate());
	}
}
