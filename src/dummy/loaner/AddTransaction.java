package dummy.loaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

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

        Person p = new Person(this, mPersonId);
        p.configureView(this, findViewById(R.id.LinearLayout01));
	}

	public void OnSaveClick(View view) {
        Log.i(TAG, "OnSaveClick()");

    	RadioButton optLoanedTo = (RadioButton)findViewById(R.id.RadioButton01);
    	RadioButton optLoanedFrom = (RadioButton)findViewById(R.id.RadioButton02);
    	if (!optLoanedTo.isChecked() && !optLoanedFrom.isChecked()) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.inputerror);
			adb.setMessage(R.string.select_option_error);
			adb.setPositiveButton("Ok", null);
			adb.show();
			Log.d(TAG, "After show");
			return;
    	}

		String sql = this.getResources().getString(R.string.insert_table_transactions);
		float amount = 0;
		try {
			amount = Float.parseFloat(txtAmount.getText().toString());
		} catch (NumberFormatException ex) {
			AlertDialog.Builder adb=new AlertDialog.Builder(this);
			adb.setTitle(R.string.inputerror);
			adb.setMessage(R.string.invalidamount);
			adb.setPositiveButton("Ok", null);
			adb.show();
			return;
		}
		
		if (optLoanedTo.isChecked()) {
			amount *= -1;
		}

		EditText txtMemo = (EditText)findViewById(R.id.EditText02);
		DatabaseHelper openHelper = new DatabaseHelper(this);
		SQLiteDatabase db = openHelper.getWritableDatabase();

		db.execSQL(sql, new Object[]{mPersonId, amount, txtMemo.getText()});
		db.close();
		
		Person p = new Person(this, mPersonId);
		if (p.getSaldo() == 0) {
			 AlertDialog.Builder adb=new AlertDialog.Builder(this);
			 adb.setMessage(getResources().getString(R.string.quit_pro_quo));
			 adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Person p = new Person(AddTransaction.this, mPersonId);
		        	   p.deleteAllTransactions();
		        	   finish();
		           }
		       });
			 adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
				 public void onClick(DialogInterface dialog, int id) {
						finish();
				 }
			 });
			 adb.show();
			
		} else {
			finish();
		}
		
	}
}
