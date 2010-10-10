package dummy.loaner;

import java.io.InputStream;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Person {
	private static final String TAG = "Person";
	private int mPersonId;
	private Cursor mCursor;
	private Context mContext;
	private float mSaldo;
	private String mFirstTransaction;
	private String mLastTransaction;
	
	public Person(Activity activity, int id) {
		mPersonId = id;
		mContext = activity;
		getCursor(activity);
		
		DatabaseHelper openHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = openHelper.getReadableDatabase();

		Resources res = mContext.getResources();
		String sql = res.getString(R.string.aggregate_person);
		Cursor cursor = db.rawQuery(sql, new String[]{Integer.toString(mPersonId)});
		
		if (cursor == null || !cursor.moveToFirst()) {
			Log.e(TAG, "Cursor is null or no row");
		} else {
			mSaldo = cursor.getFloat(0);
			mFirstTransaction = cursor.getString(1);
			mLastTransaction = cursor.getString(2);
			Log.d(TAG, String.format("%s: %s, %s", getName(), mFirstTransaction, mLastTransaction));
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        
		db.close();
	}
	
	private void getCursor(Activity activity) {
//		Uri.Builder builder = new Uri.Builder();
//		builder.scheme("content");
//		builder.appendEncodedPath("/contacts/people/1");
		Uri contactData = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, mPersonId);
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
		return mCursor.getString(mCursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
	}
	
	public Bitmap getImage() {
		Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, mPersonId);
		InputStream photoDataStream = Contacts.openContactPhotoInputStream(mContext.getContentResolver(),uri); // <-- always null
		if (photoDataStream == null) return null;
	    Bitmap photo = BitmapFactory.decodeStream(photoDataStream);
	    return photo;
	}
	
	public float getSaldo() {
		return mSaldo;
	}
	
	public String getFirstTransaction() {
		return mFirstTransaction;
	}
	
	public String getLastTransaction() {
		return mLastTransaction;
	}
	
	public static float getOverallSaldo(Context mContext) {
		DatabaseHelper openHelper = new DatabaseHelper(mContext);
		SQLiteDatabase db = openHelper.getReadableDatabase();
		
		Resources res = mContext.getResources();
		String sql = res.getString(R.string.select_overall_saldo);
		Cursor cursor = db.rawQuery(sql, new String[]{});
		
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
	
	public void deleteAllTransactions() {
		SQLiteDatabase db = new DatabaseHelper(mContext).getWritableDatabase();
		String sql = mContext.getResources().getString(R.string.delete_person_transactions);
		db.execSQL(sql, new String[]{Integer.toString(mPersonId)});
	}
}
