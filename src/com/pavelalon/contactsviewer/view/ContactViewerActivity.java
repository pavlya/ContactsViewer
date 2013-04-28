package com.pavelalon.contactsviewer.view;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.pavelalon.contactsviewer.model.TestContactsAdapter;
import com.pavelalon.contactsviewer.sms.ContactEntity;
import com.pavelalon.contactsviewer.sms.SMS;
import com.pavelalon.contactsviewer.sms.SMSManager;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ContactViewerActivity extends Activity {

	private Button btnFind;
	private Button btnTestTables;
	private EditText etFindView;
	private ListView lvContacts;
	private List<AccountEntity> accounts;
	private List<ContactEntity> testContacts;
	private TestContactsAdapter testContactsAdapter;
	private CheckBox chk_haveNUmber;
	private String Kenan = "0544659996";

	// private final String SELECTION = "UPPER(" +
	// ContactsContract.Contacts.DISPLAY_NAME + ")"
	// + " LIKE UPPER('%" + name+ "%') " + "and " +
	// ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL";

	public static final String LOG_TAG = "CONTACT_VIEWER";
	public static final String LOG_TAG_PHONE = "CONTACT_VIEWER_PHONE";

	private static final Uri PHONES_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	private static final Uri CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
	private static final Uri THREADS_CONTENT_URI = Uri
			.parse("content://mms-sms/threadID");

	private static final Uri SMS_CONVERSATIONS_URI = Uri
			.parse("content://mms-sms/conversations/");
	private static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
	public static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(
			SMS_CONTENT_URI, "inbox");
	public static final Uri SMS_SENTBOX_CONTENT_URI = Uri.withAppendedPath(
			SMS_CONTENT_URI, "sent");
	private static final String COLUMNS[] = new String[] { "person", "address",
			"body", "date", "type" };

	private static final String SORT_ORDER = "date DESC";
	private static final String SORT_ORDER_LIMIT = "date DESC limit ";

	private Uri contactsUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_viewer);

		Log.i(LOG_TAG, "ContactViewerActivity.onCreate");

		testContacts = new ArrayList<ContactEntity>();
		accounts = new ArrayList<AccountEntity>();
		btnFind = (Button) findViewById(R.id.btn_find);
		btnTestTables = (Button) findViewById(R.id.btn_test_tables);
		etFindView = (EditText) findViewById(R.id.et_find_contact);
		lvContacts = (ListView) findViewById(R.id.lv_contacts);
		chk_haveNUmber = (CheckBox) findViewById(R.id.chk_contacts_with_number);

		btnTestTables.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// tablesTester(PHONES_CONTENT_URI);
				// tablesContentsTester(SMS_INBOX_CONTENT_URI);
				// tablesContentsTester(SMS_CONTENT_URI);
				// tablesContentsTester(SMS_CONVERSATIONS_URI);
				// tablesTester(SMS_CONTENT_URI);
				// String[] borra =
				// {"0547565889","+972547565889","054-756-5889"};
				// for (int i = 0; i < borra.length; i++) {
				// Log.i(LOG_TAG, "Now it's an inbox");
				// tablesContentsTesterPhoneNumOnlyContents(SMS_INBOX_CONTENT_URI,
				// borra[i]);
				// Log.i(LOG_TAG, "Now it's a sentbox");
				// tablesContentsTesterPhoneNumOnlyContents(SMS_SENTBOX_CONTENT_URI,
				// borra[i]);
				// }
				String[] nina = { "0524687899", "+972524687899",
						"052-468-7899", "+972 52-468-7899", };
				for (int i = 0; i < nina.length; i++) {
					Log.i(LOG_TAG, "Now it's an inbox");
					List<SMS> testInbox = getSms(SMS_INBOX_CONTENT_URI,
							nina[i], SMS.INBOX);
					for (SMS sms : testInbox) {
						Log.d(LOG_TAG, "SMS INBOX: " + sms.toString());
					}
					// Log.i(LOG_TAG, "Now it's a sentbox");
					List<SMS> testInboxgetSms = getSms(SMS_SENTBOX_CONTENT_URI,
							nina[i], SMS.OUTBOX);
					for (SMS sms : testInboxgetSms) {
						Log.d(LOG_TAG, "SMS SENTBOX: " + sms.toString());
					}
					// tablesSMSTester(SMS_SENTBOX_CONTENT_URI, nina[i]);
				}

				// String[] kenan =
				// {"0544659996","+972544659996","054-465-9996","+972 54-465-9996",
				// };
				// for (int i = 0; i < kenan.length; i++) {
				// Log.i(LOG_TAG, "Now it's an inbox");
				// tablesSMSTester(SMS_INBOX_CONTENT_URI, kenan[i]);
				// Log.i(LOG_TAG, "Now it's a sentbox");
				// tablesSMSTester(SMS_SENTBOX_CONTENT_URI, kenan[i]);
				// }

				// String[] papa = {"0526988793","+972526988793"};
				// for (int i = 0; i < papa.length; i++) {
				// tablesContentsTesterPhoneNumOnlyContents(SMS_INBOX_CONTENT_URI,
				// papa[i]);
				// Log.i(LOG_TAG, "Not it's a sentbox");
				// tablesContentsTesterPhoneNumOnlyContents(SMS_SENTBOX_CONTENT_URI,
				// papa[i]);
				// }
			}
		});

		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(LOG_TAG, "Button find is clicked ");
				String searchValue = etFindView.getText().toString();
				boolean withNumbers = chk_haveNUmber.isChecked();
				if (withNumbers) {

					String selectionString = ContactsContract.Contacts.DISPLAY_NAME
							+ " like '%" + searchValue + "%'"; // search for
																// similarities
																// to inputed
					// String
					testContacts = getAccountsWithTelephoneNumberByName(selectionString);

				} else {
					getAccountsWithTelephoneNumberByName(null);
				}

				if (testContacts != null) {
					testContactsAdapter = new TestContactsAdapter(
							ContactViewerActivity.this, testContacts);
					lvContacts.setAdapter(testContactsAdapter);
				} else {
					Toast.makeText(ContactViewerActivity.this,
							"No such contact", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// check mapping between array and listView
		lvContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactEntity testEntity = testContacts.get(position);
				Log.d(LOG_TAG,
						"Clicked item in position: " + position
								+ "\nContact name: "
								+ testEntity.getContactName()
								+ "\nContact phone: "
								+ testEntity.getContactPrimaryPhone());

				// SMSNumber(testEntity.getContactPrimaryPhone());
				// smsMmsConversatonTester();
				// getSMSBYPHone(testEntity.getContactPrimaryPhone());
				// tablesContentsTesterPhoneNum(SMS_INBOX_CONTENT_URI,
				// testEntity.getContactPrimaryPhone());
				// tablesContentsTesterPhoneNum(SMS_INBOX_CONTENT_URI, Kenan);
				// tablesContentsTesterPhoneNum(SMS_INBOX_CONTENT_URI,
				// "+972 54-465-9996");
				// tablesContentsTesterPhoneNum(SMS_INBOX_CONTENT_URI,
				// "+972544659996");
				// String[] alon = {"0523266182","+972523266182"};
				// for (int i = 0; i < alon.length; i++) {
				// tablesContentsTesterPhoneNumOnlyContents(SMS_INBOX_CONTENT_URI,
				// alon[i]);
				// }

				// String[] borra =
				// {"0547565889","+972547565889","054-756-5889"};
				// for (int i = 0; i < borra.length; i++) {
				// //
				// tablesContentsTesterPhoneNumOnlyContents(SMS_SENTBOX_CONTENT_URI,
				// borra[i]);
				// tablesContentsTesterPhoneNum(SMS_SENTBOX_CONTENT_URI,
				// borra[i]);
				// }

				// String[] papa = {"0526988793","+972526988793"};
				// for (int i = 0; i < papa.length; i++) {
				// tablesContentsTesterPhoneNumOnlyContents(SMS_INBOX_CONTENT_URI,
				// papa[i]);
				// }
				// String[] papa = {"0526988793","+972526988793"};
				// for (int i = 0; i < papa.length; i++) {
				// tablesContentsTesterPhoneNumOnlyContents(SMS_SENTBOX_CONTENT_URI,
				// papa[i]);
				// }

				// contactTablesContentsTester(PHONES_CONTENT_URI,
				// testEntity.getContactName());
				// SMSManager smsManager= new
				// SMSManager(ContactViewerActivity.this);
				// List<SMS> smsList =
				// smsManager.getSmsForContact(testEntity.getContactPrimaryPhone());
				// for (SMS sms : smsList) {
				// Log.d(LOG_TAG, sms.toString());
				// }
				Intent intent = new Intent(ContactViewerActivity.this,
						SMSActivity.class);
				intent.putExtra("phoneNumber",
						testEntity.getContactPrimaryPhone());
				startActivity(intent);
			}
		});

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
		if (contactsCursor != null)
			contactsCursor.close();

		return tempAccs;
	}

	/**
	 * 
	 * @param testingUri
	 * @param phone
	 */
	private void tablesSMSTester(Uri testingUri, String phone) {
		Log.d(ContactViewerActivity.LOG_TAG,
				"tablesContentsTesterPhoneNumOnlyContents: using phone number: "
						+ phone);
		ContentResolver contentResolver = this.getContentResolver();
		final String projection[] = new String[] { "*" };
		String selection = "address like '%" + phone + "%'";

		Cursor cursor = contentResolver.query(testingUri, projection,
				selection, null, null);
		int columnsLength = cursor.getColumnCount();
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

	/**
	 * 
	 * @param testingUri
	 * @param phone
	 */
	private List<SMS> getSms(Uri testingUri, String phone, int smsType) {
		List<SMS> smsPhones = new ArrayList<SMS>();
		Log.d(ContactViewerActivity.LOG_TAG, "getSms: using phone number: "
				+ phone);
		ContentResolver contentResolver = this.getContentResolver();
		final String projection[] = new String[] { "*" };
		String selection = "address like '%" + phone + "%'";

		Cursor cursor = contentResolver.query(testingUri, projection,
				selection, null, null);
		String address = null;
		String body = null;
		long messageDate = 0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				address = cursor.getString(cursor.getColumnIndex("address"));
				body = cursor.getString(cursor.getColumnIndex("body"));
				messageDate = cursor.getLong(cursor.getColumnIndex("date"));
				smsPhones.add(new SMS(address, smsType, body, messageDate));
				cursor.moveToNext();
			}

		}
		return smsPhones;
	}

	/**
	 * Get SMS history for provided number
	 * 
	 * @param phoneNumber
	 *            value to get history for
	 */
	private void SMSNumber(String phoneNumber) {
		Log.d(LOG_TAG, "IN SMSNumber");
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
		Cursor cursor1 = getContentResolver().query(
				mSmsinboxQueryUri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body", "type" }, null, null, null);
		// startManagingCursor(cursor1);
		String[] columns = new String[] { "address", "person", "date", "body",
				"type" };
		if (cursor1.getCount() > 0) {
			String count = Integer.toString(cursor1.getCount());
			while (cursor1.moveToNext()) {
				String address = cursor1.getString(cursor1
						.getColumnIndex(columns[0]));

				if (address.equalsIgnoreCase(phoneNumber)) { // put your number
																// here
					String name = cursor1.getString(cursor1
							.getColumnIndex(columns[1]));
					String date = cursor1.getString(cursor1
							.getColumnIndex(columns[2]));
					String body = cursor1.getString(cursor1
							.getColumnIndex(columns[3]));
					String type = cursor1.getString(cursor1
							.getColumnIndex(columns[4]));

					Log.d(LOG_TAG, "date= " + new Date(Long.parseLong(date)));
					Log.d(LOG_TAG, "name= " + name);
					Log.d(LOG_TAG, "body= " + body);
					Log.d(LOG_TAG, "type= " + type);

				}
				cursor1.moveToNext();
			}
		}
		cursor1.close();
	}

	private void getSMSBYPHone(String phone) {

		ContentResolver contentResolver = getContentResolver();

		// cannot pass null as usual selection
		final String[] projection = new String[] { "*" };
		Uri uri = Uri.parse("content://mms-sms/conversations/");
		// String selection = "address = ?";
		// String[] selectionArgs = {phone};
		String selection = null;
		String[] selectionArgs = null;
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				String address = cursor.getString(cursor
						.getColumnIndex("address"));
				Log.d(LOG_TAG_PHONE,
						"address in content://mms-sms/conversations/ : "
								+ address);
				Log.d(LOG_TAG_PHONE, "phone provided: " + phone);
				if (address.equalsIgnoreCase(phone)) {
					Log.d(LOG_TAG_PHONE, "EQUALS !!! phone: " + phone);
					Log.d(LOG_TAG_PHONE,
							"date: "
									+ new Date(Long.valueOf(cursor
											.getString(cursor
													.getColumnIndex("date")))));
					Log.d(LOG_TAG_PHONE,
							"Number: "
									+ cursor.getString(cursor
											.getColumnIndex("address")));
					Log.d(LOG_TAG_PHONE,
							"Body: "
									+ cursor.getString(cursor
											.getColumnIndex("body")));
					Log.d(LOG_TAG_PHONE,
							"type: "
									+ cursor.getString(cursor
											.getColumnIndex("type")));
					Log.d(LOG_TAG_PHONE,
							"person: "
									+ cursor.getString(cursor
											.getColumnIndex("person")));
					Log.d(LOG_TAG_PHONE,
							"msg_box: "
									+ cursor.getString(cursor
											.getColumnIndex("msg_box")));
				}
				cursor.moveToNext();
			}
			// if (address.equalsIgnoreCase(phone)) {
			// while (!cursor.isAfterLast()) {
			// Log.d(LOG_TAG_PHONE,
			// "date: "
			// + new Date(Long.valueOf(cursor
			// .getString(cursor
			// .getColumnIndex("date")))));
			// Log.d(LOG_TAG_PHONE,
			// "Number: "
			// + cursor.getString(cursor
			// .getColumnIndex("address")));
			// Log.d(LOG_TAG_PHONE,
			// "Body: "
			// + cursor.getString(cursor
			// .getColumnIndex("body")));
			// Log.d(LOG_TAG_PHONE,
			// "type: "
			// + cursor.getString(cursor
			// .getColumnIndex("type")));
			// Log.d(LOG_TAG_PHONE,
			// "person: "
			// + cursor.getString(cursor
			// .getColumnIndex("person")));
			// Log.d(LOG_TAG_PHONE,
			// "msg_box: "
			// + cursor.getString(cursor
			// .getColumnIndex("msg_box")));
			// cursor.moveToNext();
			// }
			// }

		}
	}

	/**
	 * 
	 * @param testingUri
	 *            Uri of the table you want to test Some tables require new
	 *            String[]{"*"} projection to use all fields and some of them
	 *            only null
	 */
	private void tablesTester(Uri testingUri) {
		ContentResolver contentResolver = getContentResolver();
		final String projection[] = null; // new String[] { "*" };

		Cursor cursor = contentResolver.query(testingUri, projection, null,
				null, null);
		int columnsLength = cursor.getColumnCount();
		Log.d(LOG_TAG, "Columns length: " + columnsLength);
		int counter = 0;
		if (cursor.moveToFirst()) {
			for (int i = 0; i < columnsLength; i++) {
				// String response = String.format("Name of column %n is %s ",
				// i, cursor.getColumnName(i));
				// Log.d(LOG_TAG, response);
				Log.d(LOG_TAG,
						"Name of column " + i + " is: "
								+ cursor.getColumnName(i));
			}

		}
	}

	private void contactTablesContentsTester(Uri testingUri, String name) {
		ContentResolver contentResolver = getContentResolver();
		final String projection[] = null;
		final String selection = ContactsContract.Contacts.DISPLAY_NAME
				+ " like '%" + name + "%'";
		Cursor cursor = contentResolver.query(testingUri, projection, null,
				null, null);
		int columnsLength = cursor.getColumnCount();
		Log.d(LOG_TAG, "Columns length: " + columnsLength);
		int counter = 0;
		if (cursor.moveToFirst()) {
			for (int i = 0; i < columnsLength; i++) {
				// String response = String.format("Name of column %n is %s ",
				// i, cursor.getColumnName(i));
				// Log.d(LOG_TAG, response);
				Log.d(LOG_TAG,
						"Name of column " + i + " is: "
								+ cursor.getColumnName(i));
			}
			while (!cursor.isAfterLast()) {
				for (int i = 0; i < columnsLength; i++) {
					Log.d(LOG_TAG,
							"Value of column "
									+ cursor.getColumnName(i)
									+ " is: "
									+ cursor.getString(cursor
											.getColumnIndex(cursor
													.getColumnName(i))));
				}
				Log.d(LOG_TAG,
						"-----------------------------------------------");
				counter++;
				cursor.moveToNext();
			}

		}
	}

	private void tablesContentsTester(Uri testingUri) {
		ContentResolver contentResolver = getContentResolver();
		final String projection[] = new String[] { "*" };

		Cursor cursor = contentResolver.query(testingUri, projection, null,
				null, null);
		int columnsLength = cursor.getColumnCount();
		Log.d(LOG_TAG, "Columns length: " + columnsLength);
		int counter = 0;
		if (cursor.moveToFirst()) {
			for (int i = 0; i < columnsLength; i++) {
				// String response = String.format("Name of column %n is %s ",
				// i, cursor.getColumnName(i));
				// Log.d(LOG_TAG, response);
				Log.d(LOG_TAG,
						"Name of column " + i + " is: "
								+ cursor.getColumnName(i));
			}
			while (!cursor.isAfterLast()) {
				for (int i = 0; i < columnsLength; i++) {
					Log.d(LOG_TAG,
							"Value of column "
									+ cursor.getColumnName(i)
									+ " is: "
									+ cursor.getString(cursor
											.getColumnIndex(cursor
													.getColumnName(i))));
				}
				Log.d(LOG_TAG,
						"-----------------------------------------------");
				counter++;
				cursor.moveToNext();
			}

		}
	}

	private void tablesContentsTesterPhoneNumOnlyContents(Uri testingUri,
			String phone) {
		Log.d(LOG_TAG,
				"tablesContentsTesterPhoneNumOnlyContents: using phone number: "
						+ phone);
		ContentResolver contentResolver = getContentResolver();
		final String projection[] = new String[] { "*" };

		Cursor cursor = contentResolver.query(testingUri, projection, null,
				null, null);
		int columnsLength = cursor.getColumnCount();
		Log.d(LOG_TAG, "Columns length: " + columnsLength);
		int counter = 0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				if (cursor.getString(cursor.getColumnIndex("address"))
						.equalsIgnoreCase(phone)) {
					for (int i = 0; i < columnsLength; i++) {
						Log.d(LOG_TAG,
								"Value of column "
										+ cursor.getColumnName(i)
										+ " is: "
										+ cursor.getString(cursor
												.getColumnIndex(cursor
														.getColumnName(i))));
					}
				}
				counter++;
				cursor.moveToNext();
			}

		}
	}

	private void tablesContentsTesterPhoneNum(Uri testingUri, String phone) {
		ContentResolver contentResolver = getContentResolver();
		final String projection[] = new String[] { "*" };

		Cursor cursor = contentResolver.query(testingUri, projection, null,
				null, null);
		int columnsLength = cursor.getColumnCount();
		Log.d(LOG_TAG, "Columns length: " + columnsLength);
		int counter = 0;
		if (cursor.moveToFirst()) {
			for (int i = 0; i < columnsLength; i++) {
				// String response = String.format("Name of column %n is %s ",
				// i, cursor.getColumnName(i));
				// Log.d(LOG_TAG, response);
				Log.d(LOG_TAG,
						"Name of column " + i + " is: "
								+ cursor.getColumnName(i));
			}
			while (!cursor.isAfterLast()) {
				if (cursor.getString(cursor.getColumnIndex("address"))
						.equalsIgnoreCase(phone)) {
					for (int i = 0; i < columnsLength; i++) {
						Log.d(LOG_TAG,
								"Value of column "
										+ cursor.getColumnName(i)
										+ " is: "
										+ cursor.getString(cursor
												.getColumnIndex(cursor
														.getColumnName(i))));
					}
				}
				counter++;
				cursor.moveToNext();
			}

		}
	}

	private void smsMmsConversatonTester() {
		ContentResolver contentResolver = getContentResolver();
		final String[] projection = new String[] { "*" }; // cannot pass null as
															// usual selection
		Uri uri = Uri.parse("content://mms-sms/conversations/");
		Cursor cursor = contentResolver
				.query(uri, projection, null, null, null);
		// String address = cursor.getString(cursor.getColumnIndex("address"));
		int columnsLength = cursor.getColumnCount();
		Log.d(LOG_TAG, "Columns length: " + columnsLength);
		int counter = 0;
		if (cursor.moveToFirst()) {
			for (int i = 0; i < columnsLength; i++) {
				// String response = String.format("Name of column %n is %s ",
				// i, cursor.getColumnName(i));
				// Log.d(LOG_TAG, response);
				Log.d(LOG_TAG,
						"Name of column " + i + " is: "
								+ cursor.getColumnName(i));
			}
			while (!cursor.isAfterLast()) {
				Log.d(LOG_TAG,
						"Name of "
								+ counter
								+ " Contact is: "
								+ cursor.getString(cursor
										.getColumnIndex("display_name")));
				Log.d(LOG_TAG,
						"Name of "
								+ counter
								+ " address is: "
								+ cursor.getString(cursor
										.getColumnIndex("address")));
				counter++;
				cursor.moveToNext();
			}
			// while (!cursor.isAfterLast()) {
			//
			// String name = cursor.getString(cursor.getColumnIndex("person"));
			// String date = cursor.getString(cursor.getColumnIndex("date"));
			// String body = cursor.getString(cursor.getColumnIndex("body"));
			// String type = cursor.getString(cursor.getColumnIndex("type"));
			//
			// Log.d(LOG_TAG, "date= " + date);
			// Log.d(LOG_TAG, "name= " + name);
			// Log.d(LOG_TAG, "body= " + body);
			// Log.d(LOG_TAG, "type= " + type);
			//
			// }

		}
	}

	private void smsMmsConversaton(String phoneNumber) {
		ContentResolver contentResolver = getContentResolver();
		final String[] projection = new String[] { "*" }; // cannot pass null as
															// usual selection
		Uri uri = Uri.parse("content://mms-sms/conversations/");
		Cursor cursor = contentResolver
				.query(uri, projection, null, null, null);
		String address = cursor.getString(cursor.getColumnIndex("address"));
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				if (address.equalsIgnoreCase(phoneNumber)) {
					String name = cursor.getString(cursor
							.getColumnIndex("person"));
					String date = cursor.getString(cursor
							.getColumnIndex("date"));
					String body = cursor.getString(cursor
							.getColumnIndex("body"));
					String type = cursor.getString(cursor
							.getColumnIndex("type"));

					Log.d(LOG_TAG, "date= " + date);
					Log.d(LOG_TAG, "name= " + name);
					Log.d(LOG_TAG, "body= " + body);
					Log.d(LOG_TAG, "type= " + type);
				}
			}
		}
	}

	/**********************************
	 * USELESSSS SHIT !!! ***************
	 * 
	 * public List<ContactEntity> getAccountsWithTelephoneNumber() {
	 * List<ContactEntity> tempAccs = new ArrayList<ContactEntity>();
	 * ContentResolver contentResolver = getContentResolver();
	 * 
	 * int contactID = 0; String contactName = null; int hasPhoneNumber = 0; int
	 * phoneLookupID = 0; String phoneNumber = null;
	 * 
	 * // TODO need to add case insensitive workaround for this search String
	 * selection = null;
	 * 
	 * String[] selectionArgs = null; // no arguments provided
	 * 
	 * String sortOrder = null; // No sort
	 * 
	 * // get Contacts with specified name Cursor contactsCursor =
	 * contentResolver.query( ContactsContract.Contacts.CONTENT_URI, null,
	 * selection, selectionArgs, sortOrder); if (contactsCursor.moveToFirst()) {
	 * while (!contactsCursor.isAfterLast()) { hasPhoneNumber =
	 * contactsCursor.getInt(contactsCursor
	 * .getColumnIndex(Contacts.HAS_PHONE_NUMBER)); // Checks if contact have
	 * Phone Number if (hasPhoneNumber == 1) { // Get the ContactID String
	 * phoneContactId = contactsCursor .getString(contactsCursor
	 * .getColumnIndex(PhoneLookup._ID));
	 * 
	 * // assign phoneID to variable phoneLookupID =
	 * Integer.parseInt(phoneContactId); contactName = contactsCursor
	 * .getString(contactsCursor
	 * .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); contactID =
	 * contactsCursor.getInt(contactsCursor
	 * .getColumnIndex(ContactsContract.Contacts._ID));
	 * 
	 * // Projection for phone cursor String[] PHONES_PROJECTION = new String[]
	 * { "_id", "display_name", "data1", "data3" }; // Cursor that used to get
	 * phone numbers Cursor phone = contentResolver.query(
	 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
	 * ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + phoneContactId,
	 * null, null); while (phone.moveToNext()) { phoneNumber = phone
	 * .getString(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN); }
	 * phone.close(); tempAccs.add(new ContactEntity(contactID, phoneLookupID,
	 * contactName, phoneNumber)); } contactsCursor.moveToNext(); } }
	 * 
	 * return tempAccs; }
	 * 
	 * 
	 * 
	 * public List<ContactEntity> getAccountsWithTelephoneNumberByNameOld(
	 * String accountName) { List<ContactEntity> tempAccs = new
	 * ArrayList<ContactEntity>(); ContentResolver contentResolver =
	 * getContentResolver();
	 * 
	 * int contactID = 0; String contactName = null; int hasPhoneNumber = 0; int
	 * phoneLookupID = 0; String phoneNumber = null;
	 * 
	 * // TODO need to add case insensitive workaround for this search String
	 * selection = ContactsContract.Contacts.DISPLAY_NAME + " like '%" +
	 * accountName + "%'"; // search for similarities to inputed // String
	 * 
	 * String[] selectionArgs = null; // no arguments provided
	 * 
	 * String sortOrder = null; // No sort
	 * 
	 * // get Contacts with specified name Cursor contactsCursor =
	 * contentResolver.query( ContactsContract.Contacts.CONTENT_URI, null,
	 * selection, selectionArgs, sortOrder); if (contactsCursor.moveToFirst()) {
	 * while (!contactsCursor.isAfterLast()) { hasPhoneNumber =
	 * contactsCursor.getInt(contactsCursor
	 * .getColumnIndex(Contacts.HAS_PHONE_NUMBER)); // Checks if contact have
	 * Phone Number if (hasPhoneNumber == 1) { // Get the ContactID String
	 * phoneContactId = contactsCursor .getString(contactsCursor
	 * .getColumnIndex(PhoneLookup._ID));
	 * 
	 * // assign phoneID to variable phoneLookupID =
	 * Integer.parseInt(phoneContactId); contactName = contactsCursor
	 * .getString(contactsCursor
	 * .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); contactID =
	 * contactsCursor.getInt(contactsCursor
	 * .getColumnIndex(ContactsContract.Contacts._ID));
	 * 
	 * // Projection for phone cursor String[] PHONES_PROJECTION = new String[]
	 * { "_id", "display_name", "data1", "data3" }; // Cursor that used to get
	 * phone numbers Cursor phone = contentResolver.query(
	 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
	 * ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + phoneContactId,
	 * null, null); while (phone.moveToNext()) { // phoneNumber = //
	 * phone.getString(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN);
	 * Log.d(LOG_TAG, "Phone number: " + phone.getString(2)); phoneNumber =
	 * phone.getString(2); // The ID of Main // phone number } phone.close();
	 * tempAccs.add(new ContactEntity(contactID, phoneLookupID, contactName,
	 * phoneNumber)); } contactsCursor.moveToNext(); } }
	 * 
	 * return tempAccs; }
	 * 
	 * @SuppressWarnings("deprecation") protected Cursor
	 *                                  getContactsByNameAndTelephone(String
	 *                                  searchValue) { contactsUri =
	 *                                  ContactsContract.Contacts.CONTENT_URI;
	 * 
	 *                                  String[] projection = new String[] {
	 *                                  ContactsContract.Contacts._ID,
	 *                                  ContactsContract.Contacts.DISPLAY_NAME,
	 *                                  // returns 1 if at least one number does
	 *                                  exist and 0 if doesn't
	 *                                  ContactsContract.Contacts
	 *                                  .HAS_PHONE_NUMBER };
	 * 
	 *                                  // TODO need to add case insensitive
	 *                                  workaround for this search String
	 *                                  selection =
	 *                                  ContactsContract.Contacts.DISPLAY_NAME +
	 *                                  " like '%" + searchValue + "%'"; //
	 *                                  search for similarities to inputed //
	 *                                  String
	 * 
	 *                                  String[] selectionArgs = null; // no
	 *                                  arguments provided
	 * 
	 *                                  String sortOrder =
	 *                                  ContactsContract.Contacts.DISPLAY_NAME +
	 *                                  " COLLATE LOCALIZED ASC"; // sort order
	 * 
	 *                                  return managedQuery(contactsUri,
	 *                                  projection, selection, // selection for
	 *                                  // contacts display name selectionArgs,
	 *                                  sortOrder); }
	 * 
	 *                                  private String getPhoneNumberByID(int
	 *                                  contactId) { Log.d(LOG_TAG,
	 *                                  "In ContactViewerActivity.getPhoneNumberByID"
	 *                                  ); String selection =
	 *                                  ContactsContract.CommonDataKinds
	 *                                  .Phone._ID + " = ?"; // search // by //
	 *                                  id; String[] selectionArgs = {
	 *                                  String.valueOf(contactId) }; Cursor
	 *                                  phones = getContentResolver().query(
	 *                                  ContactsContract
	 *                                  .CommonDataKinds.Phone.CONTENT_URI,
	 *                                  null, // projection selection,
	 *                                  selectionArgs, null); // sort order
	 * 
	 *                                  String phoneNumber = null; while
	 *                                  (phones.moveToNext()) { Log.d(LOG_TAG,
	 *                                  "In ContactViewerActivity.getPhoneNumberByID.while (phones.moveToNext())"
	 *                                  ); phoneNumber = phones
	 *                                  .getString(phones
	 *                                  .getColumnIndex(ContactsContract
	 *                                  .CommonDataKinds.Phone.NUMBER));
	 *                                  Log.d(LOG_TAG, "Contact phone: " +
	 *                                  phoneNumber);
	 * 
	 *                                  } // phones.close();
	 * 
	 *                                  return phoneNumber; }
	 * 
	 *                                  private void getPhones() { Cursor phones
	 *                                  = getContentResolver().query(
	 *                                  ContactsContract
	 *                                  .CommonDataKinds.Phone.CONTENT_URI,
	 *                                  null, null, null, null); while
	 *                                  (phones.moveToNext()) { String id =
	 *                                  phones .getString(phones
	 *                                  .getColumnIndex(
	 *                                  ContactsContract.CommonDataKinds
	 *                                  .Phone._ID)); String name = phones
	 *                                  .getString(phones
	 *                                  .getColumnIndex(ContactsContract
	 *                                  .CommonDataKinds.Phone.DISPLAY_NAME));
	 *                                  String phoneNumber = phones
	 *                                  .getString(phones
	 *                                  .getColumnIndex(ContactsContract
	 *                                  .CommonDataKinds.Phone.NUMBER));
	 *                                  Log.d(LOG_TAG, "Contact ID: " + id +
	 *                                  "\nContact name: " + name +
	 *                                  "\nContact phone: " + phoneNumber +
	 *                                  "\n");
	 * 
	 *                                  } phones.close(); }
	 * 
	 * 
	 *                                  private Cursor getContactsByName(String
	 *                                  searchValue) { ContentResolver
	 *                                  contentResolver = getContentResolver();
	 *                                  contactsUri =
	 *                                  ContactsContract.Contacts.CONTENT_URI;
	 *                                  String[] projection = new String[] {
	 *                                  ContactsContract.Contacts._ID, // gets
	 *                                  // ID
	 *                                  ContactsContract.Contacts.DISPLAY_NAME,
	 *                                  }; // and display name
	 * 
	 *                                  // TODO need to add case insensitive
	 *                                  workaround for this search String
	 *                                  selection =
	 *                                  ContactsContract.Contacts.DISPLAY_NAME +
	 *                                  " like '%" + searchValue + "%'"; //
	 *                                  search for similarities to inputed //
	 *                                  string
	 * 
	 *                                  String[] selectionArgs = null; // no
	 *                                  arguments provided
	 * 
	 *                                  String sortOrder =
	 *                                  ContactsContract.Contacts.DISPLAY_NAME +
	 *                                  " COLLATE LOCALIZED ASC"; // sort order
	 *                                  return
	 *                                  contentResolver.query(contactsUri,
	 *                                  projection, selection, selectionArgs,
	 *                                  sortOrder);
	 * 
	 *                                  // return managedQuery(contactsUri,
	 *                                  projection, selection, // selection //
	 *                                  for // // contacts display name //
	 *                                  selectionArgs, sortOrder); }
	 * 
	 * 
	 *                                  protected List<ContactEntity>
	 *                                  getContactsFromCursor(Cursor contacts) {
	 *                                  if (contacts.moveToFirst()) {
	 *                                  List<ContactEntity> tesContList = new
	 *                                  ArrayList<ContactEntity>(); while
	 *                                  (!contacts.isAfterLast()) { int
	 *                                  contactId = contacts.getInt(contacts
	 *                                  .getColumnIndex(Contacts._ID)); String
	 *                                  contactName =
	 *                                  contacts.getString(contacts
	 *                                  .getColumnIndex(Contacts.DISPLAY_NAME));
	 * 
	 *                                  int hasPhoneNumber =
	 *                                  contacts.getInt(contacts
	 *                                  .getColumnIndex(
	 *                                  Contacts.HAS_PHONE_NUMBER));
	 * 
	 *                                  // Checks if contact have Phone Number
	 *                                  and assign it to contact if
	 *                                  (hasPhoneNumber == 1) { ContactEntity
	 *                                  contact = new ContactEntity(contactId,
	 *                                  contactName); String phoneNumber =
	 *                                  getPhoneNumberByID(contactId);
	 *                                  contact.setContactPrimaryPhone
	 *                                  (phoneNumber); tesContList.add(contact);
	 *                                  } contacts.moveToNext(); } return
	 *                                  tesContList; } return null; }
	 * 
	 * 
	 *                                  protected List<ContactEntity>
	 *                                  getTestContactsFromCursor(Cursor
	 *                                  contacts) { if (contacts.moveToFirst())
	 *                                  { List<ContactEntity> tesContList = new
	 *                                  ArrayList<ContactEntity>(); while
	 *                                  (!contacts.isAfterLast()) { int
	 *                                  contactId = contacts.getInt(contacts
	 *                                  .getColumnIndex(Contacts._ID)); String
	 *                                  contactName =
	 *                                  contacts.getString(contacts
	 *                                  .getColumnIndex(Contacts.DISPLAY_NAME));
	 *                                  tesContList.add(new
	 *                                  ContactEntity(contactId, contactName));
	 *                                  contacts.moveToNext(); } return
	 *                                  tesContList; } return null; }
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 *           menu; this adds items to the action bar if it is present.
	 *           getMenuInflater().inflate(R.menu.contact_viewer, menu); return
	 *           true; }
	 */

}
