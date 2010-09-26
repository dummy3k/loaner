package dummy.loaner;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "loaner.db";
    private static final int DATABASE_VERSION = 3;
    private static final String NOTES_TABLE_NAME = "notes";
    private Context mContext;
    
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "Constructor()");
		mContext = context;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "onCreate()");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "onUpgrade()");
		Resources res = mContext.getResources();
		
		if (oldVersion < 3) {
			String sql = res.getString(R.string.create_table_transactions);
			db.execSQL(sql);
		}
		Log.i(TAG, "onUpgrade(), complete");
}

}
