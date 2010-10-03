package dummy.loaner;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.util.Log;

public class Person {
	private static final String TAG = "Person";
	private int mPersonId;
	private Cursor mCursor;
	
	public Person(Activity activity, int id) {
		mPersonId = id;
		getCursor(activity);
	}
	
	private void getCursor(Activity activity) {
//		Uri.Builder builder = new Uri.Builder();
//		builder.scheme("content");
//		builder.appendEncodedPath("/contacts/people/1");
		Uri contactData = ContentUris.withAppendedId(People.CONTENT_URI, mPersonId);
//		content://contacts/people/1
//		lblContactUri.setText(contactData.toString());

		Cursor c = activity.managedQuery(contactData, null, null, null, null);
		if (c == null) {
			Log.e(TAG, "c is null");
			return;
		}
		
		if (!c.moveToFirst()) {
			Log.e(TAG, "person not found");
			return;
		}
		mCursor = c;
	}
	
	public int getId() {
		return mPersonId;
	}
	
	public String getName() {
		return mCursor.getString(mCursor.getColumnIndexOrThrow(People.NAME));
	}
}
