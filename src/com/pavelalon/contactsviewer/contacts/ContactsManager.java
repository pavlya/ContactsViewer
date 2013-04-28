//package com.pavelalon.contactsviewer.contacts;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.ContactsContract.CommonDataKinds;
//import android.provider.ContactsContract.PhoneLookup;
//import android.util.Log;
//
//import com.pavelalon.contactsviewer.sms.Phone;
//import com.pavelalon.contactsviewer.tools.Tools;
//import com.pavelalon.contactsviewer.view.R;
//
//
//
//public class ContactsManager {
//	
//	Context ctx;
//
//	public static String getContactName (Context ctx, String phoneNumber) {
//    	String res;
//        if (phoneNumber != null) {
//            try {
//                res = phoneNumber;
//                ContentResolver resolver = ctx.getContentResolver();
//                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
//                Cursor c = resolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
//    
//                if (c != null && c.moveToFirst()) {
//                    res = Tools.getString(c, CommonDataKinds.Phone.DISPLAY_NAME);      
//                    if (SettingsManager.getSettingsManager(ctx).displayContactNumber) {
//                    	res += " " + Phone.cleanPhoneNumber(phoneNumber);
//                    }
//                    c.close();
//                }
//            } catch (Exception ex) {
//              Log.e(Tools.LOG_TAG, "getContactName error: Phone number = " + phoneNumber, ex);  
////              res = ctx.getString(R.string.chat_call_hidden);
//            }
//        } else {
////            res = ctx.getString(R.string.chat_call_hidden);
//        }
//        return res;
//    }
//}
