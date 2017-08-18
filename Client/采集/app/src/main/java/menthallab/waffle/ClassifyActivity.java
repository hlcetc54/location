package menthallab.waffle;

import java.io.*;
import java.text.*;
import java.util.*;

import android.net.wifi.*;
import android.os.*;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

import menthallab.wafflelib.*;

public class ClassifyActivity extends Activity {
	
	private TextView resultRoomName;
	private WifiManager wifi;
	private	Classifier classifier;
	private boolean learningCompleted; // shows that network has been learned successfully!

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classify);
		
		resultRoomName = (TextView)findViewById(R.id.text_classificationReuslt);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		learningCompleted = false;
		
		File sdDir = android.os.Environment.getExternalStorageDirectory();
		File file = new File(sdDir, "/dataset.csv");
		String filePath = file.getAbsolutePath();
		try
		{
			Dataset dataset = DatasetManager.loadFromFile(filePath);
			classifier = new NeuralNetwork();
			classifier.asyncLearn(dataset);
			final double maxNetworkError = classifier.getDesiredLearningError();
			
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("Learning...");
		    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    pd.setButton("Stop", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) 
		        {
		    		classifier.stopLearning();
		    		stopTimer(pd, true);
		        }
		    });
		    pd.setCanceledOnTouchOutside(false);
		    
			new CountDownTimer(1800 * 1000, 200) {
			     public void onTick(long millisUntilFinished)
			     {
			    	 if (classifier.isCompleted())
			    	 {
			    		 this.cancel();
			    		 stopTimer(pd, false);
			    	 }
			    	 else
			    	 {
			    		 pd.setMessage("Learning...\nDesired error: " + maxNetworkError + "\nCurrent error: " + classifier.getCurrentLearningError());
			    		 //Remaining seconds: " + (millisUntilFinished / 1000) + "\n
			    	 }
			     }

			     public void onFinish()
			     {
			    	 if (!learningCompleted)
			    	 {
			    		 classifier.stopLearning();
			    		 stopTimer(pd, true);
			    	 }
			     }
			}.start();
			
		    pd.show();
		}
		catch (Exception exc)
		{
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setMessage(exc.toString()); 
			ad.show();
		}
	}
	
	private void stopTimer(ProgressDialog pd, boolean showAlert)
	{
	   	pd.cancel();
	   	learningCompleted = true;
	   	if (showAlert)
	   	{
		   	AlertDialog alertDialog = new AlertDialog.Builder(ClassifyActivity.this).create();
		   	alertDialog.setMessage("Learning process was interrupted. You can proceed, but results may be unreliable.");
		   	alertDialog.show();
	   	}
	   	registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifi.startScan();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.classify, menu);
		return true;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        if (learningCompleted)
        {
	        registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	        wifi.startScan();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (learningCompleted)
        {
        	unregisterReceiver(rssiReceiver);
        }
    }
	
    BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
    	@Override
        public void onReceive(Context context, Intent intent)
    	{
    		try
    		{
				Instance instance = new Instance();
    			List<ScanResult> scanResults = wifi.getScanResults();
	    		for (ScanResult scanResult : scanResults)
	    		{
	    			String bssid = scanResult.BSSID;
	    			int rssi = scanResult.level;
	    			int signalLevel = WifiLib.calculateSignalLevel(rssi, WifiLib.numberOfLevels + 1);
	    			instance.add(bssid, 1.0 * signalLevel / WifiLib.numberOfLevels);
	    		}
	    		String classificationLabel = classifier.classify(instance);
	    		DateFormat df = new SimpleDateFormat("HH:mm:ss");
	    		String currentTimeStr = df.format(new Date());
    			String network = String.format("λ��: %s. [%s]", classificationLabel, currentTimeStr);
    			resultRoomName.setText(network);
	    		wifi.startScan();
    		}
    		catch (Exception exc)
    		{
    			AlertDialog ad = new AlertDialog.Builder(context).create();
    			ad.setMessage(exc.toString());
    			ad.show();
    		}
        }
    };
	
    /** Called when the user clicks the Back button */
    public void returnBack(View view) {
    	btBackPressed();
    }
    
    
    /** Called when the user clicks the device Back button */
    @Override
    public void onBackPressed() {
    	btBackPressed();
    }
    
    private void btBackPressed()
    {
        ClassifyActivity.super.onBackPressed();
    }

}
