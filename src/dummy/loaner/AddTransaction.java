package dummy.loaner;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AddTransaction extends Activity {
	private static final String TAG = "AddTransaction";
	private int mPersonId;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtransaction);
        Log.i(TAG, "Startup");
        
        Bundle bundle = getIntent().getExtras();
        mPersonId = bundle.getInt("id");
        Log.i(TAG, "id: " + mPersonId);
        TextView lblPerson = (TextView)findViewById(R.id.TextView02);

        Person person = new Person(this, mPersonId);
		lblPerson.setText(person.getName());
	}

	public void OnSaveClick(View view) {
        Log.i(TAG, "OnSaveClick()");

		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		String sql = this.getResources().getString(R.string.insert_table_transactions);
		db.execSQL(sql, new Object[]{mPersonId, 2});
		
		db.close();

//		Intent myIntent = new Intent(view.getContext(), overview.class);
//		startActivity(myIntent);
		finish();
	}
}
