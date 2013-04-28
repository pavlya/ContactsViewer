package com.pavelalon.contactsviewer.sms;

import java.util.List;

import com.pavelalon.contactsviewer.view.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SMSAdapter extends BaseAdapter {

	private List<SMS> smsList;
	private Context ctx;
	private LayoutInflater inflater;
	private TextView tvName;
	private TextView tvDate;
	private TextView tvBody;
	
	public SMSAdapter(Context context, List<SMS> sms) {
		super();
		this.ctx = context;
		this.smsList = sms;
		inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return smsList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vHolder = convertView;
        //If its a new view, if its not inflate it.
        //If it's not, recycle it
        if(convertView == null){
            vHolder = inflater.inflate(R.layout.sms_item, parent,false);

        }
        
        tvName = (TextView)vHolder.findViewById(R.id.tv_name);
        tvDate = (TextView)vHolder.findViewById(R.id.tv_date);
        tvBody = (TextView)vHolder.findViewById(R.id.tv_body);
        
        SMS currentSMS = smsList.get(position);
        if(SMS.INBOX == currentSMS.getMessageType()){
        	tvName.setText("Other: ");
        } else {
        	tvName.setText("ME: ");
        }
        tvDate.setText(String.valueOf(currentSMS.getMessageDate()));
        tvBody.setText(currentSMS.getBody());
        
		return vHolder;
	}

}
