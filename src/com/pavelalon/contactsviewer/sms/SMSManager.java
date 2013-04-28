package com.pavelalon.contactsviewer.sms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.pavelalon.contactsviewer.view.ContactViewerActivity;

public class SMSManager {

	private Context ctx;
	private List<SMS> smsArray;
	
	private static final int FIRST_TELEPHONE_MINUS = 7;
	private static final int LAST_TELEPHONE_MINUS = 4;
	private static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
	public static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(
			SMS_CONTENT_URI, "inbox");
	public static final Uri SMS_SENTBOX_CONTENT_URI = Uri.withAppendedPath(
			SMS_CONTENT_URI, "sent");

	public SMSManager(Context context) {
		this.ctx = context;
		smsArray = new ArrayList<SMS>();
	}
	
	

	/**
	 * 
	 * @param phoneNumber of contact
	 * Cleans up the existing numbers and look in database for all possible variations
	 * @return List<SMS> contains all messages
	 */
	public List<SMS> getSmsForContact(String phoneNumber) {
		String inboxNumber = toRawFormat(phoneNumber); // clean up phone number
		getInbox(inboxNumber);
		String[] sentBoxNumbers = toSentboxFormat(inboxNumber); // create array of possible number formats
		for (String stringNums : sentBoxNumbers) {
			getSentbox(stringNums);
		}
		Collections.sort(smsArray, new SmsDateComparator()); // sort the list before returning it
		return smsArray;
	}

	private void getInbox(String phoneNumber) {
		getSms(SMS_INBOX_CONTENT_URI, phoneNumber, SMS.INBOX);
	}

	private void getSentbox(String phoneNumber) {
		getSms(SMS_SENTBOX_CONTENT_URI, phoneNumber, SMS.OUTBOX);
	}
	
	/**
	 * 
	 * @param testingUri
	 * @param phone
	 */
	private void getSms(Uri testingUri,
			String phone, int smsType) {
		Log.d(ContactViewerActivity.LOG_TAG,
				"getSms: using phone number: "
						+ phone);
		ContentResolver contentResolver = ctx.getContentResolver();
		final String projection[] = new String[] { "*" };
		String selection = "address like '%" + phone + "%'";

		Cursor cursor = contentResolver.query(testingUri, projection, selection,
				null, null);
		String address = null;
		String body = null;
		long messageDate = 0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				address = cursor.getString(cursor.getColumnIndex("address"));
				body = cursor.getString(cursor.getColumnIndex("body"));
				messageDate = cursor.getLong(cursor.getColumnIndex("date"));
				smsArray.add(new SMS(address, smsType, body, messageDate));
				cursor.moveToNext();
			}

		}
	}

	private void tablesSMSTester(Uri testingUri,
			String phone) {
		Log.d(ContactViewerActivity.LOG_TAG,
				"tablesContentsTesterPhoneNumOnlyContents: using phone number: "
						+ phone);
		ContentResolver contentResolver = ctx.getContentResolver();
		final String projection[] = new String[] { "*" };
		String selectionString = "address like '%" + phone + "%'";

		Cursor cursor = contentResolver.query(testingUri, projection, null,
				null, null);
		int columnsLength = cursor.getColumnCount();
		Log.d(ContactViewerActivity.LOG_TAG, "Columns length: " + columnsLength);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				for (int i = 0; i < columnsLength; i++) {
					Log.d(ContactViewerActivity.LOG_TAG,
							"Value of column "
									+ cursor.getColumnName(i)
									+ " is: "
									+ cursor.getString(cursor
											.getColumnIndex(cursor
													.getColumnName(i))));

				}
				cursor.moveToNext();
			}

		}
	}
	
	// clean up the phone string
		public static String toRawFormat(String phoneNumber){
			String phoneString = phoneNumber;
			// replace all +972 occurences of the String
			if(phoneString.charAt(0) == '+'){
				phoneString = phoneString.substring(4);
			}
			// removes "0" from the beggining of the string
			if(phoneString.charAt(0) == '0'){
				phoneString = phoneString.substring(1);
			}
			return phoneString.replace("(", "")
	                .replace(")", "")
	                .replace("-", "")
	                .replace(".", "")
	                .replace("+", "")
	                .replace(" ", "");
		}
		
		/**
		 * 
		 * @param phoneNumber formatted phone number that we will receive 
		 * @return the array of numbers that you should use for lookup in phone sentbox table 
		 */
		public static String[] toSentboxFormat(String phoneNumber){
			
			int phoneLength = phoneNumber.length();
			StringBuilder phoneString = new StringBuilder(phoneNumber);
			// convert 050******* to 050***-**** number format
			phoneString.insert(phoneLength - LAST_TELEPHONE_MINUS, "-");
			// convert 050***-**** to 050-***-**** format
			phoneString.insert(phoneLength - FIRST_TELEPHONE_MINUS, "-");
			
			String[] phones = new String[2];
			phones[0] = phoneNumber;
			phones[1] = phoneString.toString();
			return phones;
		}

}
