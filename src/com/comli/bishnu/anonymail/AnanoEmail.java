package com.comli.bishnu.anonymail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import np.com.bidaribishnu.ananoemail.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AnanoEmail extends Activity {
	Button btnSend;
	EditText etSender, etReceiver, etSubject, etMessage;
	Context context;
	ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anano_email);
		
		context = this;
		etSender = (EditText) findViewById(R.id.etSenderEmail);
		etReceiver = (EditText) findViewById(R.id.etReceiverEmail);
		etSubject = (EditText) findViewById(R.id.etSubject);
		etMessage = (EditText) findViewById(R.id.etMessage);
		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(verifyRequirement()){
					sendEmail();
				}
			}
		});
		
		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(progressDialog!=null)
			progressDialog.dismiss();
	}
	
	private void sendEmail(){
		String emailUrl = "http://www.bishnu.comli.com/send.php?from=";
		
		emailUrl += space2Plus(etSender.getText().toString());
		emailUrl += "&to=";
		emailUrl += space2Plus(etReceiver.getText().toString());
		emailUrl += "&subject=";
		emailUrl += space2Plus(etSubject.getText().toString());
		emailUrl += "&message=";
		emailUrl += space2Plus(etMessage.getText().toString());
		emailUrl += "&send=Send+Email";
		
//		try {
//			emailUrl = URLEncoder.encode(emailUrl,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
		new AsyncSendMail().execute(emailUrl);
	}
	
	private class AsyncSendMail extends AsyncTask<String, Void, Boolean>{
		HttpClient httpClient;
		ResponseHandler<String> responseHandler;
		@Override
		protected void onPreExecute(){
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("sending Anano email");
			progressDialog.show();
			httpClient = new DefaultHttpClient();
			responseHandler = new BasicResponseHandler();
		}
		@Override
		protected Boolean doInBackground(String... arg0) {
			String url = arg0[0];
			Log.d("URL",arg0[0]);
			Boolean result;
			HttpGet httpGet = new HttpGet(url);
			try {
				String serverResponse = httpClient.execute(httpGet, responseHandler);

				Log.d("URL",serverResponse);
				if(serverResponse.startsWith("Message Send successfully"))
					result = true;
				else
					result = false;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				result=false;
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			progressDialog.dismiss();
			if(result)
				Toast.makeText(context, "Congrat. anano email sended successfully", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(context, "Sorry. errro occured while sending email", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private boolean verifyRequirement(){
		String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Matcher matcher;
		Pattern pattern;
		String SenderAddress = etSender.getText().toString();
		String ReceiverAddress = etReceiver.getText().toString();
		
		pattern = Pattern.compile(emailPattern);
		matcher = pattern.matcher(SenderAddress);
		
		if(!matcher.matches()){
			Toast.makeText(getApplicationContext(), "Invalide Sender Email", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		matcher = pattern.matcher(ReceiverAddress);
		if(!matcher.matches()){
			Toast.makeText(getApplicationContext(), "Invalide Receiver Email", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private String space2Plus(String orgi){
		int len = orgi.length();
		String spaceRemoved="";
		
//		#   $   %   &    +   ?  ;   :    ,  / 
	//
//		%23 %24 %25 %26 %2B %3F %3B %3A %2C %2F
	//	
		for(int i=0;i<len;i++)
		{
			if(orgi.charAt(i)==' ')
				spaceRemoved += "+";
			else if(orgi.charAt(i)=='@')
				spaceRemoved += "%40";
			else if(orgi.charAt(i)=='#')
				spaceRemoved += "%23";
			else if(orgi.charAt(i)=='$')
				spaceRemoved += "%24";
			else if(orgi.charAt(i)=='%')
				spaceRemoved += "%25";
			else if(orgi.charAt(i)=='&')
				spaceRemoved += "%26";
			else if(orgi.charAt(i)=='+')
				spaceRemoved += "%2B";
			else if(orgi.charAt(i)=='?')
				spaceRemoved += "%3F";
			else if(orgi.charAt(i)==';')
				spaceRemoved += "%3B";
			else if(orgi.charAt(i)==':')
				spaceRemoved += "%3A";
			else if(orgi.charAt(i)==',')
				spaceRemoved += "%2C";
			else if(orgi.charAt(i)=='/')
				spaceRemoved += "%2F";
			else
				spaceRemoved += orgi.charAt(i);
		}
		return spaceRemoved;
	}
}
