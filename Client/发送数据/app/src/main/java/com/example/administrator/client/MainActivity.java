package com.example.administrator.client;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter=null;
    private boolean isWorking;
    Button btnStart;
    TextView tvLabel;
    HashMap<String,String> dataMap;
    ArrayList<String> bluetooth_dev_list;
    ArrayList<String> wifi_dev_list;
    ArrayList<String> geomagnetic_dev_list;

    private WifiManager wifi;
    public final static String BLUE_TOOTH_DEV_PREFIX = "abeacon_";
    public final static String WIFI_DEV_PREFIX = "TP-LINK_";
//    public final static String HTTP_ADDR = "https://www.xiaosong1234.cn/test.php";
    public final static String HTTP_ADDR = "http://10.19.200.7:9100/handler";
    RequestQueue mQueue;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener mSensorListener;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        bluetooth_dev_list =new ArrayList<>();
        wifi_dev_list =new ArrayList<>();
        geomagnetic_dev_list =new ArrayList<>();
        isWorking = false;
        dataMap = new HashMap<>();
        mQueue = Volley.newRequestQueue(MainActivity.this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorListener = new MSensorListener();
        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        dataInit();
        viewInit();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void dataInit() {
        bluetooth_dev_list.add("abeacon_DC91");
        bluetooth_dev_list.add("abeacon_F4E1");
        bluetooth_dev_list.add("abeacon_F11E");
        bluetooth_dev_list.add("abeacon_EE04");
        bluetooth_dev_list.add("abeacon_E2B6");
        bluetooth_dev_list.add("abeacon_F435");
        bluetooth_dev_list.add("abeacon_DDFD");
        bluetooth_dev_list.add("abeacon_EEB7");

        wifi_dev_list.add("TP-LINK_EE43");
        wifi_dev_list.add("TP-LINK_0772");
        wifi_dev_list.add("TP-LINK_07E1");
        wifi_dev_list.add("TP-LINK_B61C");
        wifi_dev_list.add("TP-LINK_B26B");


        geomagnetic_dev_list.add("Geomagnetic1");
        geomagnetic_dev_list.add("Geomagnetic2");
        geomagnetic_dev_list.add("Geomagnetic3");

        //先都初始化为0 然后进行时间周期内扫描
        for(String str:bluetooth_dev_list){
            dataMap.put(str,0+"");
        }
        for(String str:wifi_dev_list){
            dataMap.put(str,0+"");
        }
//        地磁数据比较及时 不用预先初始化

    }

    private void viewInit()
    {
        btnStart = (Button)findViewById(R.id.btnStart);
        tvLabel = (TextView) findViewById(R.id.tvLabel);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isWorking)
        {
            registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            //注册蓝牙
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiver, filter);
            mBluetoothAdapter.startDiscovery();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isWorking)
        {
            unregisterReceiver(mReceiver);
            unregisterReceiver(rssiReceiver);
        }
    }



    // 蓝牙部分
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{

                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short B_rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);

                String deviceName = device.getName();

                if(deviceName.startsWith(BLUE_TOOTH_DEV_PREFIX))
                {

                    if (bluetooth_dev_list.contains(deviceName))
                            dataMap.put(deviceName,B_rssi+"");
                }

                mBluetoothAdapter.startDiscovery();
            }
            catch (Exception e)
            {

            }
        }
    };

    //WiFi 部分
    BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try
            {

                List<ScanResult> scanResults = wifi.getScanResults();
                for (ScanResult scanResult : scanResults) {

                    String ssid = scanResult.SSID;
                    int rssi = scanResult.level;

                    if(ssid.startsWith(WIFI_DEV_PREFIX))
                    {
                        String dev_name = ssid;

                        if(wifi_dev_list.contains(dev_name)){
                            dataMap.put(dev_name,rssi+"");
                        }
                    }

                }

                wifi.startScan();
            }
            catch (Exception e)
            {

            }
        }
    };

    private void send_request() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "send to network!", Toast.LENGTH_LONG).show();
            }
        });


        StringRequest stringRequest = new StringRequest(Request.Method.POST, HTTP_ADDR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("TAG", response);
                        String label = resToLabel(response);
                        tvLabel.setText(label);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> reqMap = new HashMap<>();
                StringBuilder str = new StringBuilder();
                for(String key:bluetooth_dev_list)
                    str.append(dataMap.get(key)+",");
                for(String key:wifi_dev_list)
                    str.append(dataMap.get(key)+",");
                for(String key:geomagnetic_dev_list)
                    str.append(dataMap.get(key)+",");

                Log.i("str",str.toString());
                reqMap.put("data",str.toString());

//                Server和Client 格式暂时不匹配25 != 18 先发送假数据
                String fakeStr = "45,70,44,70,86,36,35,19,85,46,26,78,45,36,35,19,85,46,26,78,9,85,46,26,17";
//                Log.i("fakeStr","45,70,44,70,86,36,35,19,85,46,26,78,45,36,35,19,85,46,26,78,9,85,46,26,17");
                reqMap.put("data",fakeStr);

                return reqMap;
            }
        };
        mQueue.add(stringRequest);

    }

    //处理返回结果 加工出Label
    private String resToLabel(String response) {

        return response;
    }

    public void startScane(View view)
    {
        if (isWorking)
        {
            Log.i("info","停止扫描");
            unregisterReceiver(rssiReceiver);
            unregisterReceiver(mReceiver);
            btnStart.setText("开始");
            isWorking = false;
            try
            {

            }
            catch (Exception exc)
            {
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage(exc.toString());
                ad.show();
            }

        }else{
            Log.i("info","开始扫描");
            isWorking = true;
            btnStart.setText("停止");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (isWorking) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        send_request();
                    }
                }
            }).start();

            //注册WIFI
            registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
            //注册蓝牙
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        }

    }


    private class MSensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {

            dataMap.put(geomagnetic_dev_list.get(0),(int)(event.values[0])+"");
            dataMap.put(geomagnetic_dev_list.get(1),(int)event.values[1]+"");
            dataMap.put(geomagnetic_dev_list.get(2),(int)event.values[2]+"");

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }



}
