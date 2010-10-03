package dummy.loaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddTransaction extends Activity {
	private static final String TAG = "AddTransaction";
	private int mPersonId;
	private EditText txtAmount;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtransaction);
        Log.i(TAG, "Startup");
        
        Bundle bundle = getIntent().getExtras();
        mPersonId = bundle.getInt("id");
        Log.i(TAG, "id: " + mPersonId);
        txtAmount = (EditText)findViewById(R.id.EditText01);

        TextView lblPerson = (TextView)findViewById(R.id.TextView02);
        Person person = new Person(this, mPersonId);
		lblPerson.setText(person.getName());
	}

	public void OnSaveClick(View view) {
        Log.i(TAG, "OnSaveClick()");

		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		String sql = this.getResources().getString(R.string.insert_table_transactions);
		float amount = 0;
		try {
			amount = Float.parseFloat(txtAmount.getText().toString());
		} catch (NumberFormatException ex) {
	       	 AlertDialog.Builder adb=new AlertDialog.Builder(this);
	       	 adb.setTitle("Loaner");
	       	 adb.setMessage("Invalid amount");
	       	 adb.setPositiveButton("Ok", null);
	       	 adb.show();
	       	 return;
		}
		
		db.execSQL(sql, new Object[]{mPersonId, amount});
		
		db.close();
		finish();
	}
}
