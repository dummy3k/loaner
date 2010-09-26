package dummy.loaner;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AddTransaction extends Activity {
	private static final String TAG = "AddTransaction";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtransaction);
//        lblContactUri = (TextView)findViewById(R.id.TextView01);
        Log.i(TAG, "Startup");
    }

	public void OnSaveClick(View view) {
        Log.i(TAG, "OnSaveClick()");

		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		String sql = this.getResources().getString(R.string.insert_table_transactions);
		db.execSQL(sql, new Object[]{1, 2});
		
		db.close();

		Intent myIntent = new Intent(view.getContext(), overview.class);
//		myIntent.se
		startActivity(myIntent);
	}
}
