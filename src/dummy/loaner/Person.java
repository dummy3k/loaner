package dummy.loaner;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Person {
	private static final String TAG = "Person";
	private int mPersonId;
	private Cursor mCursor;
	private Context mContext;
	
	public Person(Activity activity, int id) {
		mPersonId = id;
		mContext = activity;
		getCursor(activity);
	}
	
	private void getCursor(Activity activity) {
//		Uri.Builder builder = new Uri.Builder();
//		builder.scheme("content");
//		builder.appendEncodedPath("/contacts/people/1");
		Uri contactData = ContentUris.withAppendedId(People.CONTENT_URI, mPersonId);
//		content://contacts/people/1

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
		if (mCursor == null) {
			return "Nobody";
		}
		return mCursor.getString(mCursor.getColumnIndexOrThrow(People.NAME));
	}
	
	public Bitmap getImage() {
		Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, mPersonId);
		return People.loadContactPhoto(mContext, uri, R.drawable.icon, null);
	}
	
	public float getSaldo() {
		DatabaseHelper openHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		Resources res = mContext.getResources();
		String sql = res.getString(R.string.select_person_saldo);
		Cursor cursor = db.rawQuery(sql, new String[]{Integer.toString(mPersonId)});
		
		if (cursor == null) {
			Log.e(TAG, "Cursor is null");
			return -12.34f;
		}        

		if (!cursor.moveToFirst()) {
			Log.w(TAG, "No row");
			return 0;
		}        
		
		float retVal = cursor.getFloat(0);
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        
		db.close();

		return retVal;
	}
	
	public void configureView(Context context, View row) {
		TextView label=(TextView)row.findViewById(R.id.txtPersonName);
		if (label != null) {
			label.setText(this.getName());
		}

		label=(TextView)row.findViewById(R.id.txtPersonSaldo);
		if (label != null) {
			float saldo = this.getSaldo();
			label.setText(String.format("%12.2f", saldo));
			if (saldo < 0) {
				label.setTextColor(context.getResources().getColor(R.color.red));
			} else {
				label.setTextColor(context.getResources().getColor(R.color.green));
			}
		}

		ImageView icon = (ImageView)row.findViewById(R.id.imgPerson);
		if (icon != null ) {
			icon.setImageBitmap(this.getImage());
		}
	}
}
