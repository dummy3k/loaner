package dummy.loaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ViewPerson extends Activity {
	private static final String TAG = "ViewPerson";
	private int mPersonId;
	private TextView lblPerson;
	private ListView lv1;

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
	
	public void cmdAddTransaction(View view) {
		Log.d(TAG, "addTransaction()");
		Intent myIntent = new Intent(this, AddTransaction.class);
		myIntent.putExtra("id", mPersonId);
		startActivity(myIntent);
	}

}
