package dummy.loaner;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.MenuInflater;


public class overview extends Activity {
	private static final String TAG = "overview";
	private static final int PICK_CONTACT = 1;
	private TextView lblContactUri;
	private ListView lv1;
	private OverviewListItem mCurrentItem;

	public class OverviewListItem {
		private Person Person;
		
		public OverviewListItem(Person p) {
			this.Person = p;
		}
		
		@Override
		public String toString() {
			return this.Person.getName();
		}
	}
	
	public class OverviewAdapter extends ArrayAdapter<OverviewListItem> {
		private Context mContext;
		private List<OverviewListItem> mItems;
		
		public OverviewAdapter(Context context, int resource,
				int textViewResourceId, List<OverviewListItem> objects) {
			
			super(context, resource, textViewResourceId, objects);
			mContext = context;
			mItems = objects;
		}

		public View getView(int position, View ConvertView, ViewGroup parent) {
			Log.d(TAG, "getView(" + position + ")");
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (inflater == null) {
				Log.e(TAG, "inflater is null!");
			}
			View row=inflater.inflate(R.layout.overviewlistitem, null);
			Person p = mItems.get(position).Person;
			p.configureView(this.getContext(), row);

			return row;
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Startup");

        setContentView(R.layout.main);
        lblContactUri = (TextView)findViewById(R.id.TextView01);
        lv1 = (ListView)findViewById(R.id.ListView01);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
//			        	 AlertDialog.Builder adb=new AlertDialog.Builder(overview.this);
//			        	 adb.setTitle("LVSelectedItemExample");
//			        	 adb.setMessage("Selected Item is = "+lv1.getItemAtPosition(position));
//			        	 adb.setPositiveButton("Ok", null);
//			        	 adb.show();
				 
				 
				Intent myIntent = new Intent(overview.this, ViewPerson.class);
				OverviewListItem lvit = (OverviewListItem)lv1.getItemAtPosition(position);
				Log.d(TAG, "PersonId: " + lvit.Person.getId());
				myIntent.putExtra("id", lvit.Person.getId());
				overview.this.startActivity(myIntent);
			 }});

        registerForContextMenu(lv1);
        lv1.setOnCreateContextMenuListener(this);

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		if (lv1.getSelectedItem() == null){
			Log.w(TAG, "lv1.getSelectedItem() is null, onCreateOptionsMenu");
			return;
		}
		mCurrentItem = (OverviewListItem)lv1.getSelectedItem();
		if (mCurrentItem == null){
			Log.w(TAG, "lvit is null, onCreateOptionsMenu");
			return;
		}
		Log.d(TAG, "PersonId: " + mCurrentItem.Person.getId());

		Log.d(TAG, "onCreateContextMenu(");
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.person, menu);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume()");
		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		List<OverviewListItem> items = new LinkedList<OverviewListItem>();
//		Cursor cursor = db.query("transactions", null, null, null, null, null, 
//								 "id desc");
		Cursor cursor = db.query(true, "transactions", new String[] {"person_id"}, 
				 null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Log.d(TAG, "--------");
				Log.d(TAG, Integer.toString(cursor.getInt(0)));
//				Log.d(TAG, Integer.toString(cursor.getInt(1)));
//				Log.d(TAG, Integer.toString(cursor.getInt(2)));

				int person_id = cursor.getInt(0);
				Log.d(TAG, "person_id: " + person_id);
				Person p = new Person(this, person_id);
				Log.d(TAG, "Image: " + p.getImage().getHeight());
				items.add(new OverviewListItem(p));

			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        

//		lv1.setAdapter(new ArrayAdapter<OverViewListItem>(this,
//				android.R.layout.simple_list_item_1, 
//				items));
		lv1.setAdapter(new OverviewAdapter(this,
				R.layout.overviewlistitem, R.id.TextView01, 
				items));

		db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.overview, menu);
    	return true;
    }

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG, "onOptionsItemSelected()");
		switch (item.getItemId()) {
		// We have only one menu option
		case R.id.addnew:
			addTransaction();
			break;
			
		default:
			Log.d(TAG, "unknown menu");
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public boolean onContextItemSelected (MenuItem item) {
		super.onContextItemSelected(item);
		Log.d(TAG, "onContextItemSelected()");
		switch (item.getItemId()) {
		// We have only one menu option
		case R.id.mnuShow:
			Log.d(TAG, "mnuShow");
			int id = mCurrentItem.Person.getId();
			Log.d(TAG, "PersonId: " + id);

			Intent myIntent = new Intent(this, AddTransaction.class);
			myIntent.putExtra("id", id);
			startActivity(myIntent);
			break;
			
		default:
			Log.d(TAG, "unknown menu");
//			return 
		}
		return true;
	}
	
	private void addTransaction() {
		// Launch new activity
		Intent intent = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);  
	}
	
	public void cmdAddTransaction(View view) {
		addTransaction();
	}
	
	public void debugHandler(View view) {
		Intent myIntent = new Intent(view.getContext(), AddTransaction.class);
		startActivity(myIntent);
	}

	public void debugHandler2(View view) {
		//	content://contacts/people/1
		long id = 1;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.appendEncodedPath("/contacts/people/1");
		Uri contactData = ContentUris.withAppendedId(People.CONTENT_URI, id);
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
					int id = (int)ContentUris.parseId(contactData);
					Log.d(TAG, "id: " + id);

					Intent myIntent = new Intent(this, AddTransaction.class);
					myIntent.putExtra("id", id);
					startActivity(myIntent);
				}
				break;
		}
	}
	
}