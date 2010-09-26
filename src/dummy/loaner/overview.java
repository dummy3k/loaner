package dummy.loaner;

import android.app.Activity;
import android.app.Activity;
import android.content.ContentUris;
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
	private TextView lblContactUri;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lblContactUri = (TextView)findViewById(R.id.TextView01);
    }

	public void myClickHandler(View view) {
//		Intent.ACTION_GET_CONTENT
		Intent intent = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);  
	}
	
	public void debugHandler(View view) {
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
					//contactData.
					long id =ContentUris.parseId(contactData);
					lblContactUri.setText(Long.toString(id));
//					lblContactUri.setText(contactData.toString());
//					lblContactUri.setText(contactData.get);
				}
				break;
		}
	}
	
}