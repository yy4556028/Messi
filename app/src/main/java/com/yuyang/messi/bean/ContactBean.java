package com.yuyang.messi.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.yuyang.messi.MessiApp;

import java.io.InputStream;

/**
 * 联系人 bean
 */
public class ContactBean implements Comparable{

	private String displayName;

	/**
	 * 排序字母 : YU 于 YANG 洋
	 */
	private String sortKey;

	/**
	 * 头像ID
	 */
	private Uri photoUri;

	private String lookUpKey;

	private long contactId;

	private String firstSpell;

	private String fullSpell;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public Uri getPhotoUri() {
		return photoUri;
	}

	public Bitmap getPhotoBitmap() {
		if (photoUri == null) {
			return null;
		} else {
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(MessiApp.getInstance().getContentResolver(), photoUri);
			return BitmapFactory.decodeStream(input);
		}
	}

	public void setPhotoUri(Uri photoUri) {
		this.photoUri = photoUri;
	}

	public String getLookUpKey() {
		return lookUpKey;
	}

	public void setLookUpKey(String lookUpKey) {
		this.lookUpKey = lookUpKey;
	}

	public long getContactId() {
		return contactId;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public String getFirstSpell() {
		return firstSpell;
	}

	public void setFirstSpell(String firstSpell) {
		this.firstSpell = firstSpell;
	}

	public String getFullSpell() {
		return fullSpell;
	}

	public void setFullSpell(String fullSpell) {
		this.fullSpell = fullSpell;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ContactBean that = (ContactBean) o;

		return lookUpKey.equals(that.lookUpKey);
	}

	@Override
	public int hashCode() {
		return lookUpKey.hashCode();
	}

	@Override
	public int compareTo(Object another) {
		return 0;
	}
}
