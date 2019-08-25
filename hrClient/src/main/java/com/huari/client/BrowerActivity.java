package com.huari.client;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class BrowerActivity extends AppCompatActivity {

	public WebView m_webview;
	public EditText m_edittext;
	private Button m_imgbutton;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		m_imgbutton = (Button)findViewById(R.id.search_btn1);
		m_edittext = (EditText)findViewById(R.id.search_editer);
		m_webview = (WebView)findViewById(R.id.webView1);
		m_edittext.setText("www.huari.com.cn");
		
		m_imgbutton.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View arg0)
			{
				String strURI = m_edittext.getText().toString();
				m_webview.loadUrl(strURI);
			}
		  }
		);
	}

	
}
