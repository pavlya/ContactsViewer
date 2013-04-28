package com.pavelalon.contactsviewer.view;

import java.util.List;

import com.pavelalon.contactsviewer.sms.SMS;
import com.pavelalon.contactsviewer.sms.SMSAdapter;
import com.pavelalon.contactsviewer.sms.SMSManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;

public class SMSActivity extends Activity {

	private SMSAdapter adapter;
	private ListView smsListView;
	private SMSManager manager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		
		Intent intent = getIntent();
		String phoneNumber = intent.getStringExtra("phoneNumber");
		manager = new SMSManager(this);
		List<SMS> smsList = manager.getSmsForContact(phoneNumber);
		adapter = new SMSAdapter(this, smsList);
		
		smsListView = (ListView)findViewById(R.id.lv_sms);
		smsListView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sm, menu);
		return true;
	}

}
