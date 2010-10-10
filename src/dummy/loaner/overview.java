package dummy.loaner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import android.view.MenuInflater;

public class overview extends Activity {
	private static final String TAG = "overview";
	private static final int PICK_CONTACT = 1;
	private ListView lv1;
	
	private enum SortBy {
		Name,
		Saldo,
		FirstTransaction,
		LastTransaction
	}
	private SortBy mSortBy = SortBy.Saldo;
	private boolean mSortRevervse = false;
	
	/**
	 * This is the selected element in the list
	 * for menu actions.
	 */
	private OverviewListItem mCurrentItem;
	
	private boolean mfInitialized = false; 

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
		private OverviewListItem[] mItems;
		
		public OverviewAdapter(Context context, int resource,
				int textViewResourceId, OverviewListItem[] objects) {
			
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
			Person p = mItems[position].Person;
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

        lv1 = (ListView)findViewById(R.id.ListView01);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Intent myIntent = new Intent(overview.this, ViewPerson.class);
				OverviewListItem lvit = (OverviewListItem)lv1.getItemAtPosition(position);
				Log.d(TAG, "PersonId: " + lvit.Person.getId());
				myIntent.putExtra("id", lvit.Person.getId());
				overview.this.startActivity(myIntent);
			 }});

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
		        Log.i(TAG, "OnItemLongClickListener()");
				mCurrentItem = (OverviewListItem)lv1.getItemAtPosition(position);
				Log.d(TAG, "OnItemLongClickListener(), PersonId: " + mCurrentItem.Person.getId());
				return false;
			}
        });
        
        registerForContextMenu(lv1);
        lv1.setOnCreateContextMenuListener(this);

        
        Spinner s = (Spinner)findViewById(R.id.Spinner01);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
                this, R.array.overview_sortby, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> a, View v, int position, long id) {
				if (!mfInitialized) return;
				Log.d(TAG, "sort order changed");
				mSortBy = SortBy.values()[position];
				onResume();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

        SharedPreferences prefs = getSharedPreferences(TAG,MODE_PRIVATE);
        mSortBy = SortBy.values()[prefs.getInt("sortBy", 0)];
        s.setSelection(mSortBy.ordinal());

        mfInitialized = true;
    }

    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();

        SharedPreferences.Editor ed = getSharedPreferences(TAG, MODE_PRIVATE).edit();
        ed.putInt("sortBy", mSortBy.ordinal());
        ed.commit();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		Log.d(TAG, "onCreateContextMenu(");
    	super.onCreateContextMenu(menu, v, menuInfo);

    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.person, menu);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume()");
		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		List<OverviewListItem> itemList = new LinkedList<OverviewListItem>();
		Cursor cursor = db.query(true, "transactions", new String[] {"person_id"}, 
				 null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				int person_id = cursor.getInt(0);
				Log.d(TAG, "person_id: " + person_id);
				Person p = new Person(this, person_id);
				Log.d(TAG, "Image: " + p.getImage().getHeight());
				itemList.add(new OverviewListItem(p));

			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        

		OverviewListItem[] itemArray = itemList.toArray(new OverviewListItem[]{});
		Arrays.sort(itemArray, new Comparator<OverviewListItem>() {
			public int compare(OverviewListItem object1,
					OverviewListItem object2) {
				
				switch (mSortBy){
				case Name:
					return object1.Person.getName().compareTo(object2.Person.getName());
				case Saldo:
					return Float.compare(object1.Person.getSaldo(), object2.Person.getSaldo());
				case FirstTransaction:
					return object1.Person.getFirstTransaction().compareTo(object2.Person.getFirstTransaction());
				case LastTransaction:
					return object1.Person.getLastTransaction().compareTo(object2.Person.getLastTransaction());
				}
				
				Log.wtf(TAG, "bad sort key");
				return 0;
			}});
		
		lv1.setAdapter(new OverviewAdapter(this,
				R.layout.overviewlistitem, R.id.TextView01, 
				itemArray));

		db.close();

        TextView label = (TextView)findViewById(R.id.txtOverallSaldo);
		float saldo = Person.getOverallSaldo(this);
		label.setText(String.format("%12.2f", saldo));
		if (saldo < 0) {
			label.setTextColor(getResources().getColor(R.color.red));
		} else {
			label.setTextColor(getResources().getColor(R.color.green));
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.w(TAG, "onCreateOptionsMenu()");
		MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.overview, menu);
    	return true;
    }
	 
	/**
	 * Called when menu item is selected
	 */
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
		int id = mCurrentItem.Person.getId();
		Log.d(TAG, "PersonId: " + id);

		switch (item.getItemId()) {
		// We have only one menu option
		case R.id.mnuShow: {
			Intent myIntent = new Intent(this, ViewPerson.class);
			myIntent.putExtra("id", id);
			startActivity(myIntent);
			break;
		}
			
		case R.id.mnuAddNew: {
			Intent myIntent = new Intent(this, AddTransaction.class);
			myIntent.putExtra("id", id);
			startActivity(myIntent);
			break;
		}

		case R.id.mnuDeleteAll: {
			 AlertDialog.Builder adb=new AlertDialog.Builder(overview.this);
			 adb.setMessage(getResources().getString(R.string.really_delete_transactions));
			 adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		  			 mCurrentItem.Person.deleteAllTransactions();
					 onResume();
		           }
		       });
			 adb.setNegativeButton("No", null);
			 adb.show();
			 break;
		}

		default:
			Log.d(TAG, "unknown menu");
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