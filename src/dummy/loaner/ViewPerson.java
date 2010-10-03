package dummy.loaner;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ViewPerson extends Activity {
	private static final String TAG = "ViewPerson";
	private int mPersonId;
	private TextView lblPerson;
	private ListView lv1;

//	public class ViewPersonListItem {
//		private Person mTransactionId;
//		
//		public ViewPerson(int transactionId) {
//			this.mTransactionId = transactionId;
//		}
//		
//		@Override
//		public String toString() {
//			return this.Person.getName();
//		}
//	}


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Startup");
        setContentView(R.layout.viewperson);
    
        lblPerson = (TextView)findViewById(R.id.TextView01);
        lv1 = (ListView)findViewById(R.id.ListView01);
        
        Bundle bundle = getIntent().getExtras();
        mPersonId = bundle.getInt("id");
        Log.i(TAG, "id: " + mPersonId);

        Person p = new Person(this, mPersonId);
        lblPerson.setText(p.getName());
	}

	
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume()");
    	
		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		List<String> items = new LinkedList<String>();
		Cursor cursor = db.query("transactions", null, 
				"person_id = ?", new String[] {"" + mPersonId}, 
				null, null, null, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Log.d(TAG, "--------");
				Log.d(TAG, Integer.toString(cursor.getInt(0)));
				Log.d(TAG, Integer.toString(cursor.getInt(1)));
				Log.d(TAG, Integer.toString(cursor.getInt(2)));
				Log.d(TAG, Float.toString(cursor.getFloat((2))));

				float amount = cursor.getFloat(2);
				items.add(Float.toString(amount));
//				int person_id = cursor.getInt(0);
//				Log.d(TAG, "person_id: " + person_id);
//				Person p = new Person(this, person_id);
//				items.add(new OverViewListItem(p));

			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        
		lv1.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 
				items));

		db.close();
    }

	public void cmdAddTransaction(View view) {
		Log.d(TAG, "addTransaction()");
		Intent myIntent = new Intent(this, AddTransaction.class);
		myIntent.putExtra("id", mPersonId);
		startActivity(myIntent);
	}

}
