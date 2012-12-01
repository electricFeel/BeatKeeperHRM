package com.example.beatkeeperHRM;

import android.app.Activity;

import android.os.Bundle;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.R.*;
import android.app.Activity;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.json.*;

import zephyr.android.HxMBT.*;
import zephyr.android.*;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	BluetoothAdapter adapter = null;
	BTClient _bt;
	ZephyrProtocol _protocol;
	NewConnectedListener _NConnListener;
	private final int HEART_RATE = 0x100;
	private final int INSTANT_SPEED = 0x101;	
	private static final String SECURE_SETTINGS = android.Manifest.permission.WRITE_SECURE_SETTINGS;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*this.enforceCallingOrSelfPermission(SECURE_SETTINGS,
                "BLUETOOTH_ADMIN permission");*/
        
        
        setContentView(R.layout.activity_main);
        /*Sending a message to android that we are going to initiate a pairing request*/
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        /*Registering a new BTBroadcast receiver from the Main Activity context with pairing request event*/
       this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
        // Registering the BTBondReceiver in the application that the status of the receiver has changed to Paired
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
       this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);
        
      //Obtaining the handle to act on the CONNECT button
        final TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
		String ErrorText  = "Not Connected to HxM ! !";
		 tv.setText(ErrorText);

        Button btnConnect = (Button) findViewById(R.id.ButtonConnect);
        if (btnConnect != null)
        {
        	btnConnect.setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			String BhMacID = "00:07:80:9D:8A:E8";
        			//String BhMacID = "00:07:80:88:F6:BF";
        			adapter = BluetoothAdapter.getDefaultAdapter();
        			
        			Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        			
        			if (pairedDevices.size() > 0) 
        			{
                        for (BluetoothDevice device : pairedDevices) 
                        {
                        	if (device.getName().startsWith("HXM")) 
                        	{
                        		BluetoothDevice btDevice = device;
                        		BhMacID = btDevice.getAddress();
                                break;

                        	}
                        }
                        
                        
        			}
        			
        			//BhMacID = btDevice.getAddress();
        			BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
        			String DeviceName = Device.getName();
        			_bt = new BTClient(adapter, BhMacID);
        			_NConnListener = new NewConnectedListener(Newhandler,Newhandler);
        			_bt.addConnectedEventListener(_NConnListener);
        			
        			TextView tv1 = (TextView)findViewById(R.id.labelHeartRate);
        			tv1.setText("000");
        			
        			 tv1 = (TextView)findViewById(R.id.labelInstantSpeed);
        			 tv1.setText("0.0");
        			 
        			 //tv1 = 	(EditText)findViewById(R.id.labelSkinTemp);
        			 //tv1.setText("0.0");
        			 
        			 //tv1 = 	(EditText)findViewById(R.id.labelPosture);
        			 //tv1.setText("000");
        			 
        			 //tv1 = 	(EditText)findViewById(R.id.labelPeakAcc);
        			 //tv1.setText("0.0");
        			if(_bt.IsConnected())
        			{
        				_bt.start();
        				TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
        				String ErrorText  = "Connected to HxM "+DeviceName;
						 tv.setText(ErrorText);
						 
						 //Reset all the values to 0s

        			}
        			else
        			{
        				TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
        				String ErrorText  = "Unable to Connect !";
						 tv.setText(ErrorText);
        			}
        		}
        	});
        }
        /*Obtaining the handle to act on the DISCONNECT button*/
        Button btnDisconnect = (Button) findViewById(R.id.ButtonDisconnect);
        if (btnDisconnect != null)
        {
        	btnDisconnect.setOnClickListener(new OnClickListener() {
				
				/*Functionality to act if the button DISCONNECT is touched*/
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*Reset the global variables*/
					TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
    				String ErrorText  = "Disconnected from HxM!";
					 tv.setText(ErrorText);

					/*This disconnects listener from acting on received messages*/	
					_bt.removeConnectedEventListener(_NConnListener);
					/*Close the communication with the device & throw an exception if failure*/
					_bt.Close();
				
				}
        	});
        }
        
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        if(btnLogin != null){
        	btnLogin.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					//test logging into the server here
					//HTTPClient client = new HTTPClient();
					//String token = client.GetLoginToken("omar", "ayub");
					//tv.setText(token);
					
					
				}
        	});
        }
    }
    private class BTBondReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
			Log.d("Bond state", "BOND_STATED = " + device.getBondState());
		}
    }
    
    private class HTTPClient{
    	HttpClient httpClient = new DefaultHttpClient();
    	String baseURL = "http://ec2-50-17-145-141.compute-1.amazonaws.com:3002/";
    	String sessionToken;
    	
    	public HTTPClient(){
    		
    	}
    	
    	
    	
    	public String GetLoginToken(String userName, String password){
    		try{
    			// + "users/login_token"
    			String URI = baseURL + "users/login_token";
    			HttpPost request = new HttpPost(URI);
    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    			nameValuePairs.add(new BasicNameValuePair("user", userName));
    			nameValuePairs.add(new BasicNameValuePair("password", password));
    			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    			HttpResponse resp = httpClient.execute(request);
    			HttpEntity val = resp.getEntity();
    			System.out.println(val);
    			//String str_val =val.getContent().toString();
    			InputStream stream = val.getContent();
    			int readByte;
    			String output = "";
    			
    			while((readByte = stream.read()) != -1){
    				output += (char)readByte;
    			}
    			
    			System.out.println(output);
    			JSONObject jsonArray = new JSONObject(output);
    			this.sessionToken = jsonArray.getString("session_token");
    		}catch(Exception ex){
    			System.out.println(ex.getMessage());
    			System.out.println(ex.getStackTrace());
    		}
    		return this.sessionToken;
    	}
    	
    	public void sendData(List<Pair<Date, Double>> datas){
    		try{
    			String URI = baseURL + "/hbdata/add";
    			HttpPost request = new HttpPost(URI);
    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    			nameValuePairs.add(new BasicNameValuePair("session_token", this.sessionToken));
    			for(Pair<Date, Double> d: datas){
    				nameValuePairs.add(new BasicNameValuePair(d.first.toGMTString(), d.second.toString()));
    			}
    			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    			HttpResponse resp = httpClient.execute(request);
    		}catch(Exception ex){
    			
    		}
    	}
    }
    
    private class BTBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BTIntent", intent.getAction());
			Bundle b = intent.getExtras();
			Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
			Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
			try {
				BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
				Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[] {String.class} );
				byte[] pin = (byte[])m.invoke(device, "1234");
				m = device.getClass().getMethod("setPin", new Class [] {pin.getClass()});
				Object result = m.invoke(device, pin);
				Log.d("BTTest", result.toString());
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    

    final  Handler Newhandler = new Handler(){
    	public void handleMessage(Message msg)
    	{
    		TextView tv;
    		switch (msg.what)
    		{
    		case HEART_RATE:
    			String HeartRatetext = msg.getData().getString("HeartRate");
    			tv = (TextView)findViewById(R.id.labelHeartRate);
    			System.out.println("Heart Rate Info is "+ HeartRatetext);
    			if (tv != null)tv.setText(HeartRatetext);
    		break;
    		
    		case INSTANT_SPEED:
    			String InstantSpeedtext = msg.getData().getString("InstantSpeed");
    			tv = (TextView)findViewById(R.id.labelInstantSpeed);
    			if (tv != null)tv.setText(InstantSpeedtext);
    		
    		break;
    		
    		}
    	}

    };
    
}
