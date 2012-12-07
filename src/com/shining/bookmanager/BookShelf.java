package com.shining.bookmanager;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;

public class BookShelf extends Activity {

	private WebView resultWeb;



	private Button cleanButton;

	private ImageButton deleteButton;

	private Handler handler = new Handler();

	private Set<String> deleteSet = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_bookshelf);
		
	
		
		
		cleanButton = (Button) findViewById(R.id.cleanButton);
		cleanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(BookShelf.this);
				builder.setTitle("警告");
				builder.setIcon(R.drawable.ic_launcher);
				builder.setMessage("是否真的要删除所有收藏夹条目?");
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								BookInfoDao.getInstance().deleteAll();
								new Handler().post(new Runnable() {
									@Override
									public void run() {
										resultWeb
												.loadUrl("javascript:listBook()");
									}
								});
								deleteSet.clear();
								dialog.dismiss();
							}

						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						});

				builder.create().show();
			}
		});

		deleteButton = (ImageButton) findViewById(R.id.deleteButton);
		deleteButton.setEnabled(false);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (String isbn : deleteSet) {
					BookInfoDao.getInstance().delete(isbn);
				}
				deleteButton.setEnabled(false);
				resultWeb.loadUrl("javascript:listBook()");
			}
		});
		
		resultWeb = (WebView) findViewById(R.id.bookShelfWeb);
		
		this.resultWeb.getSettings().setSupportZoom(false);
		this.resultWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(
				true);
		this.resultWeb.getSettings().setJavaScriptEnabled(true);

		this.resultWeb.loadUrl("file:///android_asset/book_list.html");

		this.resultWeb.addJavascriptInterface(new Object() {
			public String getBookResult() {
				return BookInfoDao.getInstance().list().toString();
			}

			public void addDeleteItem(String isbn) {
				deleteSet.add(isbn);
				updateDeleteButtonStatus();
			}

			public void removeDeleteItem(String isbn) {
				deleteSet.remove(isbn);
				updateDeleteButtonStatus();
			}

			public void getDetail(final String isbn) {
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent();
						intent.setClass(BookShelf.this,
								SearchBookActivity.class);
						intent.putExtra("ISBN", isbn);
						startActivity(intent);
					}
				});
			}

			private void updateDeleteButtonStatus() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						deleteButton.setEnabled(!deleteSet.isEmpty());
					}
				});
			}

		}, "bookShelfControl");


		
	

		

		
		
	}
	
	 protected void onResume() {  
		  
	        super.onResume();  
	        
	        resultWeb.reload();
	    }  
}
