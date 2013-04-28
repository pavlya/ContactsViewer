package com.pavelalon.contactsviewer.model;

import java.util.List;
import java.util.zip.Inflater;

import com.pavelalon.contactsviewer.sms.ContactEntity;
import com.pavelalon.contactsviewer.view.R;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class TestContactsAdapter extends BaseAdapter{

	private Context ctx;
	private List<ContactEntity> contactsList;
	private LayoutInflater inflater;
	private TextView tvID;
	private TextView tvName;
	private TextView tvPhoneNumber;
	
	public TestContactsAdapter(Context context, List<ContactEntity> contacts) {
		this.ctx = context;
		this.contactsList = contacts;
		inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contactsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contactsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null){
			view = inflater.inflate(R.layout.test_contact_layout, parent, false);
		}
		
		tvID = (TextView)view.findViewById(R.id.tv_id);
		tvName = (TextView)view.findViewById(R.id.tv_name);
		tvPhoneNumber = (TextView)view.findViewById(R.id.tv_phone);
		
		tvID.setText(""+ contactsList.get(position).getContactId());
		tvName.setText(contactsList.get(position).getContactName());
		tvPhoneNumber.setText(contactsList.get(position).getContactPrimaryPhone());
		
		return view;
	}

}
