package com.example.beatkeeperHRM;

import java.util.TimerTask;

import android.util.Log;

import zephyr.android.HxMBT.BTClient;

public class BTWatcherTimerTask extends TimerTask{

	BTClient _bt;
	MainActivity _main;
	
	public BTWatcherTimerTask(BTClient client, MainActivity activity){
		_bt = client;
		_main = activity;
	}
	
	@Override
	public void run() {
		try{
		_main.checkIfLoggedIn();
			if(_bt.isInterrupted()){
				_bt.start();
				
			}
		}catch(Exception ex){
			Log.e("Error in BTWatcherTimerTask", ex.getMessage());
			
		}
	}

}