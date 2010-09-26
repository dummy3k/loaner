package dummy.loaner;

import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class overview extends Activity {
	private static final int PICK_CONTACT = 1;
	private TextView foo;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        foo = (TextView)findViewById(R.id.TextView01);
    }

	public void myClickHandler(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);  
		startActivityForResult(intent, PICK_CONTACT);  
		
		//foo.setText("Hello World");
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
			case (PICK_CONTACT):
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					Cursor c = managedQuery(contactData, null, null, null, null);
					if (c.moveToFirst()) {
						String name = c.getString(c.getColumnIndexOrThrow(People.NAME));
						foo.setText(name);
					}
				}
				break;
		}
	}
	
}