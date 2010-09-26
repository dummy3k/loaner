package dummy.loaner;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

public class overview extends Activity {
	private static final String TAG = "overview";
	private static final int PICK_CONTACT = 1;
	private TextView lblContactUri;
	private ListView lv1;
	
	public class ListViewItem extends ListActivity {
		
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Startup");

        setContentView(R.layout.main);
        lblContactUri = (TextView)findViewById(R.id.TextView01);
        lv1 = (ListView)findViewById(R.id.ListView01);

		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		String sql = this.getResources().getString(R.string.select_all_table_transactions);
//		db.execSQL(sql);
		
		//Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		//selection = new String[] { "name" }
		List<String> items = new LinkedList<String>();
		Cursor cursor = db.query("transactions", null, null, null, null, null, "id desc");
		if (cursor.moveToFirst()) {
			do {
				Log.d(TAG, "--------");
				Log.d(TAG, Integer.toString(cursor.getInt(0)));
				Log.d(TAG, Integer.toString(cursor.getInt(1)));
				Log.d(TAG, Integer.toString(cursor.getInt(2)));

				long person_id = cursor.getInt(1); 
				Uri contactData = ContentUris.withAppendedId(People.CONTENT_URI, person_id);
				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c != null && c.moveToFirst()) {
					String name = c.getString(c.getColumnIndexOrThrow(People.NAME));
					Log.d(TAG, name);
					items.add(name);
				}

			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        
		
//		String myArray[] = (String[])items.toArray();
		lv1.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 
				items));
		
		
    }

	public void myClickHandler(View view) {
//		Intent.ACTION_GET_CONTENT
		Intent intent = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);  
	}
	
	public void debugHandler(View view) {
//		lblContactUri.setText("c isnull");
		Intent myIntent = new Intent(view.getContext(), AddTransaction.class);
		startActivity(myIntent);
	}

	public void debugHandler2(View view) {
//		String uri_str = "content://contacts/people/1";
//		String id = (String)lblContactUri.getText();
		long id = 1;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.appendEncodedPath("/contacts/people/1");
		Uri contactData = ContentUris.withAppendedId(People.CONTENT_URI, id);
//		content://contacts/people/1
		lblContactUri.setText(contactData.toString());
		
		Cursor c = managedQuery(contactData, null, null, null, null);
		if (c == null) {
			lblContactUri.setText("c isnull");
			return;
		}

		if (c.moveToFirst()) {
			String name = c.getString(c.getColumnIndexOrThrow(People.NAME));
			lblContactUri.setText(name);
		}

	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
			case (PICK_CONTACT):
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					long id = ContentUris.parseId(contactData);
					lblContactUri.setText(Long.toString(id));
//					lblContactUri.setText(contactData.toString());
				}
				break;
		}
	}
	
}