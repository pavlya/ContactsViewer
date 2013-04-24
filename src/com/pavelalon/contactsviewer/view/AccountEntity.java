package com.pavelalon.contactsviewer.view;

import android.graphics.drawable.Drawable;

public class AccountEntity {
	
	private String mName;
    private String mType;
    private CharSequence mTypeLabel;
    private Drawable mIcon;
    
    public AccountEntity() {
		// TODO Auto-generated constructor stub
	}
    
    
    /**
     * @param name The name of the account. This is usually the user's email address or
     *        username.
     * @param description The description for this account. This will be dictated by the
     *        type of account returned, and can be obtained from the system AccountManager.
     */
	public AccountEntity(String mName, String mType, CharSequence mTypeLabel,
			Drawable mIcon) {
		super();
		this.mName = mName;
		this.mType = mType;
		this.mTypeLabel = mTypeLabel;
		this.mIcon = mIcon;
	}



	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmType() {
		return mType;
	}

	public void setmType(String mType) {
		this.mType = mType;
	}

	public CharSequence getmTypeLabel() {
		return mTypeLabel;
	}

	public void setmTypeLabel(CharSequence mTypeLabel) {
		this.mTypeLabel = mTypeLabel;
	}

	public Drawable getmIcon() {
		return mIcon;
	}

	public void setmIcon(Drawable mIcon) {
		this.mIcon = mIcon;
	}
    
    
}
