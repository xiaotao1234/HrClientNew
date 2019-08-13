package com.huari.client;

import com.huari.dataentry.GlobalData;
import com.huari.tools.SysApplication;

import android.os.Bundle;
import android.app.Activity;

public class SearchinterferenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchinterference);
		SysApplication.getInstance().addActivity(this);
		Thread.setDefaultUncaughtExceptionHandler(GlobalData.myExceptionHandler);
	}

}
