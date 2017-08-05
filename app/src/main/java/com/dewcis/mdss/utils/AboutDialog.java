package com.dewcis.mdss.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Window;
import android.widget.TextView;

import com.dewcis.mdss.R;

public class AboutDialog extends Dialog{

	private static Context mContext = null;
	
	public AboutDialog(Context context) {
		super(context);

		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	}


	
	/**
     * This is the standard Android on create method that gets called when the activity initialized.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.about);
		TextView tv = (TextView)findViewById(R.id.legal_text);
		tv.setText(readRawTextFile(R.raw.legal));
		tv = (TextView)findViewById(R.id.info_text);

		String text = "<h3>" + mContext.getResources().getString(R.string.app_name)+ "</h3>"
					+ "\nVersion " + MUtil.getAppVersionName(mContext)+ "<br>"
					+ "\nCopyright 2015"
					+ "\n<br><br>Powered by APHRC"
					+ "\n<b>www.aphrc.org</b>"
					+ "\n<br><br>Developed by\n<b>www.dewcis.com</b><br><br>";


		tv.setText(Html.fromHtml(text));
		tv.setLinkTextColor(mContext.getResources().getColor(R.color.app_link));
		Linkify.addLinks(tv, Linkify.ALL);
	}



	public static String readRawTextFile(int id) {
		InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {
        	while (( line = buf.readLine()) != null) text.append(line);
         } catch (IOException e) {
            return null;
         }
         return text.toString();
     }

}
