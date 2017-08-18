package menthallab.waffle;

import android.os.*;
import android.app.*;
import android.content.*;
import android.view.*;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	/** Called when the user clicks the Train button */
    public void startTraining(View view) {
		Intent intent = new Intent(this, GatherActivity.class);
		startActivity(intent);
    }
    
    /** Called when the user clicks the Classify button */
    public void startClassifying(View view) {
    	Intent intent = new Intent(this, ClassifyActivity.class);
		startActivity(intent);
    }

}
