package com.pavelalon.contactsviewer.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.pavelalon.contactsviewer.model.TestContactsAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.content.ContentResolver;
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
	private EditText etFindView;
	private ListView lvContacts;
	private List<AccountEntity> accounts;
	private List<ContactEntity> testContacts;
	private TestContactsAdapter testContactsAdapter;
	private CheckBox chk_haveNUmber;

	// private final String SELECTION = "UPPER(" +
	// ContactsContract.Contacts.DISPLAY_NAME + ")"
	// + " LIKE UPPER('%" + name+ "%') " + "and " +
	// ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL";

	public static final String LOG_TAG = "CONTACT_VIEWER";
	private Uri contactsUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_viewer);

		Log.i(LOG_TAG, "ContactViewerActivity.onCreate");

		testContacts = new ArrayList<ContactEntity>();
		accounts = new ArrayList<AccountEntity>();
		btnFind = (Button) findViewById(R.id.btn_find);
		etFindView = (EditText) findViewById(R.id.et_find_contact);
		lvContacts = (ListView) findViewById(R.id.lv_contacts);
		chk_haveNUmber = (CheckBox) findViewById(R.id.chk_contacts_with_number);

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
				Log.d(LOG_TAG, "Clicked item in position: " + position
						+ "\nContact name: " + testEntity.getContactName()
						+ "\nContact phone: " + testEntity.getContactPrimaryPhone());
				
				SMSNumber(testEntity.getContactPrimaryPhone());
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

		return tempAccs;
	}

	/**
	 * Get SMS history for provided number
	 * @param phoneNumber value to get history for
	 */
	private void SMSNumber(String phoneNumber) {
		Log.d(LOG_TAG, "IN SMSNumber");
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
		Cursor cursor1 = getContentResolver().query(
				mSmsinboxQueryUri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body", "type" }, null, null, null);
//		 startManagingCursor(cursor1);
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
					
					Log.d(LOG_TAG, "date= " + date);
					Log.d(LOG_TAG, "name= " + name);
					Log.d(LOG_TAG, "body= " + body);
					Log.d(LOG_TAG, "type= " + type);

				}

			}
		}
		cursor1.close();
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
