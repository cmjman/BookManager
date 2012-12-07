package com.shining.bookmanager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class SearchBookActivity extends Activity{
	
	private static String APIKey="003afe0642e755f700b0fa12c8b601e5";
	
	private static String URL = "http://api.douban.com/book/subject/isbn/";
	
	private static String PATH_COVER = Environment.getExternalStorageDirectory() + "/BookMangerData/";   

	private Button returnButton;

	private Button addFavoriteButton;

	private WebView resultWeb;

	private BookInfo bookInfo;
	
	private String isbn;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_book);
		
		try {
			isbn=getIntent().getExtras().getString(
					"ISBN");
		//	System.out.println(isbn);
			//bookInfo = getResultByIsbn(isbn);
			
			getBookInfoRun.run();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		returnButton = (Button) this.findViewById(R.id.returnButton);
		returnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchBookActivity.this.finish();
			}
		});

		addFavoriteButton = (Button)findViewById(R.id.favoriteButton);

	//	if (BookInfoDao.getInstance().get(bookInfo.getIsbn())==null) {
			setAddFavorite();
	//	} else {
	//		setHasAddFavorite();
	//	}

		resultWeb = (WebView)findViewById(R.id.resultWeb);
		resultWeb.getSettings().setSupportZoom(false);
		resultWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(
				true);
		resultWeb.getSettings().setJavaScriptEnabled(true);

		resultWeb.loadUrl("file:///android_asset/results.html");
	//	resultWeb.loadUrl("results.html");

		resultWeb.addJavascriptInterface(new Object() {
			public String getBookName() {
				return bookInfo.getName();
			}

			public String getBookSummary() {
				return bookInfo.getSummary();
			}

			public String getBookImageUrl() {
				return bookInfo.getImageUrl();
			}

			public String getBookAuthor() {
				return bookInfo.getAuthor();
			}
		}, "searchResult");



		
		
	}
	
	 Runnable getBookInfoRun = new Runnable(){  
		  
			
		  public void run() {  
			  try {
				  bookInfo = getResultByIsbn(isbn);
			
				  } catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				   
				  
				  handler.sendEmptyMessage(0);
		  }  
		    };  
	
	  private Handler handler =new Handler(){
			
		   public void handleMessage(Message msg){
			   
			   
			   super.handleMessage(msg);
			   
			   
			   /*
			  
			   
			   switch (msg.what) {   
              case Scan.GUIUPDATEIDENTIFIER:   
                   myBounceView.invalidate();  
			

			   }   */
			
		   }
		 };
	
		 

		 private void checkNetworkInfo(){
			  
				ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
				State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			 
				if(mobile==State.CONNECTED||mobile==State.CONNECTING) return;
				if(wifi==State.CONNECTED||wifi==State.CONNECTING) return;
				
				Toast.makeText(this, "无可用网络，请打开网络连接！", Toast.LENGTH_SHORT).show();  
				
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			 
			  }
			
		 
		 private void setAddFavorite(){
				addFavoriteButton.setText("加入书架");
				this.addFavoriteButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						BookInfoDao.getInstance().create(bookInfo);
					//	setHasAddFavorite();
					}
				});
			}

			private void setHasAddFavorite() {
				addFavoriteButton.setText("已加入");
				addFavoriteButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						BookInfoDao.getInstance().delete(bookInfo.getIsbn());
						setAddFavorite();
					}
				});
			}
		   
	
	
	private BookInfo getResultByIsbn(String isbn)/* throws ClientProtocolException, IOException, 
	IllegalStateException,XmlPullParserException */{
			BookInfo dbook=null;
			
		//	Looper.prepare();
			
			checkNetworkInfo();
			
		//	Looper.loop();
			
			
			/*
			try { 
			
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(URL + isbn +"?apikey="+APIKey );
			
			System.out.println(URL + isbn +"?apikey="+APIKey);
			
			
			
			HttpResponse response = client.execute(get);
			
			
			
			dbook=getBookInfo(response.getEntity().getContent());
			
			} catch (Exception e) {  
			e.printStackTrace();  
			}  
			
			
			
			return dbook;
*/

			
			try{
			
			URL url = new URL(URL+isbn+"?apikey="+APIKey);      
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();      
			conn.setConnectTimeout(5 * 1000);      
			conn.setRequestMethod("GET");      
			InputStream inStream = conn.getInputStream(); 
			
			dbook = getBookInfo(inStream);
			}catch (Exception e) {  
			e.printStackTrace();  
			}  
			
			
			return dbook;
			
			
			}

	
	private BookInfo getBookInfo(InputStream inputStream)/* throws XmlPullParserException, IOException*/ {
		
		BookInfo bookInfo1 = new BookInfo();
		
		try{
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		
		bookInfo1.setIsbn(isbn);
		
	
		
		
			for (int i = parser.getEventType(); i != XmlPullParser.END_DOCUMENT; i = parser.next()) {
			
			if (i == XmlPullParser.START_TAG
					&& parser.getName().equals("attribute")
					&& parser.getAttributeValue(0).equals("title")) {
				bookInfo1.setName(parser.nextText());
				Log.v("SearchBook", "title>>" + bookInfo1.getName());
				continue;
			}
			if (i == XmlPullParser.START_TAG
					&& parser.getName().equals("attribute")
					&& parser.getAttributeValue(0).equals("author")) {
				bookInfo1.setAuthor(parser.nextText());
				Log.v("SearchBook", "author>>" + bookInfo1.getAuthor());
				continue;
			}
			if (i == XmlPullParser.START_TAG && parser.getName().equals("link")) {
				if (parser.getAttributeValue(1).equals("image")) {
					bookInfo1.setImageUrl(parser.getAttributeValue(0));
					Log.v("SearchBook", "image>>" + bookInfo1.getImageUrl());
				}
				continue;
			}
			if (i == XmlPullParser.START_TAG
					&& parser.getName().equals("summary")) {
				bookInfo1.setSummary(parser.nextText());
				Log.v("SearchBook", "summary>>" + bookInfo1.getSummary());
				continue;
			}
			
		}
		Log.v("SearchBook", ">>>>> parse end.");
		
		}catch(Exception e){
			e.printStackTrace();
		}

		return bookInfo1;
	}



	public byte[] downImage(String imageUrl) {

			try{
			 URL url = new URL(imageUrl);      
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();      
			 conn.setConnectTimeout(5 * 1000);      
			 conn.setRequestMethod("GET");      
			 InputStream inStream = conn.getInputStream();      
			 
			 if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){      
			        
				 return readStream(inStream);      
			 }      
			 
			}
			catch(Exception e){
				e.printStackTrace();
			}
			    
			 return null;      
			}
			
			public static byte[] readStream(InputStream inStream) throws Exception{      
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();      
			byte[] buffer = new byte[1024];      
			int len = 0;      
			while( (len=inStream.read(buffer)) != -1){      
			    outStream.write(buffer, 0, len);      
			}      
			outStream.close();      
			inStream.close();      
			return outStream.toByteArray();      
			}    
			
			public void saveFile(Bitmap bm, String fileName) throws IOException {   
			File dirFile = new File(PATH_COVER);   
			if(!dirFile.exists()){   
			    dirFile.mkdir();   
			}   
			File captureFile = new File(PATH_COVER + fileName);   
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(captureFile));   
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);   
			bos.flush();   
			bos.close();   
			}   
}
