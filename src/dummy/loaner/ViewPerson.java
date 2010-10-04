package dummy.loaner;

import java.util.LinkedList;
import java.util.List;

import dummy.loaner.overview.OverviewListItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ViewPerson extends Activity {
	private static final String TAG = "ViewPerson";
	private int mPersonId;
	private TextView lblPerson;
	private ListView lv1;

	public class ViewPersonListItem {
		public int TransactionId;
		private String mMemo;
		private float mAmount;
		
		public ViewPersonListItem(Cursor cursor) {
			mMemo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));
			mAmount = cursor.getFloat(cursor.getColumnIndexOrThrow("amount"));
		}
		
		public void configureView(Context context, View row) {
			TextView label=(TextView)row.findViewById(R.id.txtMemo);
			label.setText(mMemo);

			label=(TextView)row.findViewById(R.id.txtAmount);
			label.setText(String.format("%12.2f", mAmount));
			if (mAmount < 0) {
				label.setTextColor(context.getResources().getColor(R.color.red));
			} else {
				label.setTextColor(context.getResources().getColor(R.color.green));
			}
		}
		
//		@Override
//		public String toString() {
//			return this.Person.getName();
//		}
	}

	public class ViewPersonAdapter extends ArrayAdapter<ViewPersonListItem> {
		private Context mContext;
		private List<ViewPersonListItem> mItems;
		
		public ViewPersonAdapter(Context context, int textViewResourceId,
				List<ViewPersonListItem> objects) {

			super(context, textViewResourceId, objects);
			mContext = context;
			mItems = objects;
		}

		public View getView(int position, View ConvertView, ViewGroup parent) {
			Log.d(TAG, "getView(" + position + ")");
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (inflater == null) {
				Log.e(TAG, "inflater is null!");
			}
			View row=inflater.inflate(R.layout.viewpersonlistitem, null);
			mItems.get(position).configureView(this.getContext(), row);

			return row;
		}
	}

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Startup");
        setContentView(R.layout.viewperson);
    
        lv1 = (ListView)findViewById(R.id.ListView01);
        
        Bundle bundle = getIntent().getExtras();
        mPersonId = bundle.getInt("id");
        Log.i(TAG, "id: " + mPersonId);
	}

	
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume()");

        Person p = new Person(this, mPersonId);
        p.configureView(this, findViewById(R.id.LinearLayout01));

		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		List<ViewPersonListItem> items = new LinkedList<ViewPersonListItem>();
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

				items.add(new ViewPersonListItem(cursor));
				
//				float amount = cursor.getFloat(2);
//				items.add(Float.toString(amount));
//				int person_id = cursor.getInt(0);
//				Log.d(TAG, "person_id: " + person_id);
//				Person p = new Person(this, person_id);
//				items.add(new OverViewListItem(p));

			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}        
		lv1.setAdapter(new ViewPersonAdapter(this,
				R.layout.viewpersonlistitem, 
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
