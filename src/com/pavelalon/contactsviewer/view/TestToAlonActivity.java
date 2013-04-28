package com.pavelalon.contactsviewer.view;

import java.util.ArrayList;
import java.util.List;

import com.pavelalon.contactsviewer.sms.ContactEntity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;

public class TestToAlonActivity extends Activity {

	public static final String LOG_TAG = "CONTACT_VIEWER";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_to_alon);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_to_alon, menu);
		return true;
	}
	
	/**
	 * Final version
	 * 
	 * @param selectionValue
	 *            selection String for Cursor (Can be null if you don't want to
	 *            search for specified phone number)
	 * @return
	 */
	public List<ContactEntity> getAccountsWithTelephoneNumberByName(
			String selectionValue) {
		List<ContactEntity> tempAccs = new ArrayList<ContactEntity>();
		ContentResolver contentResolver = getContentResolver();

		int contactID = 0;
		String contactName = null;
		int hasPhoneNumber = 0;
		int phoneLookupID = 0;
		String phoneNumber = null;

		// TODO need to add case insensitive workaround for this search
		String selection = selectionValue;

		String[] selectionArgs = null; // no arguments provided

		String sortOrder = null; // No sort

		// get Contacts with specified name
		Cursor contactsCursor = contentResolver.query(
				ContactsContract.Contacts.CONTENT_URI, null, selection,
				selectionArgs, sortOrder);
		if (contactsCursor.moveToFirst()) {
			while (!contactsCursor.isAfterLast()) {
				hasPhoneNumber = contactsCursor.getInt(contactsCursor
						.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
				// Checks if contact have Phone Number
				if (hasPhoneNumber == 1) {
					// Get the ContactID
					String phoneContactId = contactsCursor
							.getString(contactsCursor
									.getColumnIndex(PhoneLookup._ID));

					// assign phoneID to variable
					phoneLookupID = Integer.parseInt(phoneContactId);
					contactName = contactsCursor
							.getString(contactsCursor
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					contactID = contactsCursor.getInt(contactsCursor
							.getColumnIndex(ContactsContract.Contacts._ID));

					// Projection for phone cursor
					String[] PHONES_PROJECTION = new String[] { "_id",
							"display_name", "data1", "data3" };
					// Cursor that used to get phone numbers
					Cursor phone = contentResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							PHONES_PROJECTION,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + phoneContactId, null, null);
					while (phone.moveToNext()) {
						// phoneNumber =
						// phone.getString(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN);
						Log.d(LOG_TAG, "Phone number: " + phone.getString(2));
						phoneNumber = phone.getString(2); // The ID of Main
															// phone number
					}
					phone.close();
					tempAccs.add(new ContactEntity(contactID, phoneLookupID,
							contactName, phoneNumber));
				}
				contactsCursor.moveToNext();
			}
		}

		return tempAccs;
	}

}
