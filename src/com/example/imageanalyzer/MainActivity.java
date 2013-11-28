package com.example.imageanalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private Bitmap bitmap;
	private String theUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void analyzePic(View view){
		EditText et = (EditText)findViewById(R.id.editText1);
		theUrl = et.getText().toString();
		Thread thread = new Thread(){
			@Override
			public void run(){
				try {
					  bitmap = BitmapFactory.decodeStream((InputStream)new URL(theUrl).getContent());
					  mHandler.sendEmptyMessage(0);
				} catch (MalformedURLException e) {
					  e.printStackTrace();
				} catch (IOException e) {
					  e.printStackTrace();
				}
			};
		};
		thread.start();
		
		
		
	}
	
	public void getPixels(View view){
		int bytes = bitmap.getByteCount();
		ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
		bitmap.copyPixelsToBuffer(buffer); // Move the byte data to the buffer
		byte[] inputBytes = buffer.array();
		
		//boolean[] redFrequent = new boolean[256];
		byte[] redBytes = new byte[inputBytes.length/400];
		byte[] blueBytes = new byte[inputBytes.length/400];
		byte[] greenBytes = new byte[inputBytes.length/400];
		int pixels=0;
		boolean isNew;
		for (int i = 0; i < inputBytes.length / 400; i++) {
			isNew=true;
			byte r = inputBytes[400 * i];
			byte g = inputBytes[400 * i + 1];
			byte b = inputBytes[400 * i + 2];
			//if(i>1&&r==inputBytes[4 * i - 4]&&g==inputBytes[4 * i - 3]&&r==inputBytes[4 * i - 2]){
			//	isNew=false;
			//}else{
			for (int k=0; k<pixels; k++){
				if((r==redBytes[k])&&(b==blueBytes[k])&&(g==greenBytes[k])){
					isNew=false;
				}
			}
		//	}
			if(isNew){
				redBytes[pixels]=r;
				greenBytes[pixels]=g;
				blueBytes[pixels]=b;
				pixels++;
			}
			
		}
		byte[] topPixels = getTop20(redBytes, greenBytes, blueBytes, pixels);
		Log.d("Pixels",bytes/400+"bytes "+pixels+" pixels");
		for(byte e : topPixels){
			Log.d("Pixels", e+"");
		}
	}
	public byte[] getTop20(byte[] reds, byte[] greens, byte[] blues, int pix){
		byte[] tops = new byte[60];
		int p=0;
		boolean done=false;
		while(!done){
			byte[] common = getPopularElements(reds, greens, blues);
			tops[p] = common[0];
			tops[p+1] = common[1];
			tops[p+2] = common[2];
			p+=3;
			reds[popIndex] = -1;
			greens[popIndex] = -1;
			blues[popIndex] = -1;
			if(p>=59){
				done=true;
			}
		}
		return tops;
	}
	
	private int popIndex;
	
	public byte[] getPopularElements(byte[] a, byte[] b, byte[] c)
	{
	  int count = 1, tempCount;
	  byte popularR = a[0];
	  byte popularG = b[0];
	  byte popularB = c[0];
	  byte tempR = 0;
	  byte tempG = 0;
	  byte tempB = 0;
	  popIndex=0;
	  for (int i = 0; i < (a.length - 1); i++)
	  {
	    tempR = a[i];
	    tempG = b[i];
	    tempB = c[i];
	    tempCount = 0;
	    if(tempR>0&&tempG>0&&tempB>0){
		    for (int j = 1; j < a.length; j++)
		    {
		      if (tempR == a[j] && tempG == b[j] && tempB == c[j])
		        tempCount++;
		    }
		    if (tempCount > count)
		    {
		      popularR = tempR;
		      popularG = tempG;
		      popularB = tempB;
		      popIndex=i;
		      count = tempCount;
		    }
	    }
	  }
	  byte[] popularBytes = {popularR, popularG, popularB};
	  return popularBytes;
	}
	
	Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	ImageView i = (ImageView)findViewById(R.id.imageView1);
        	i.setImageBitmap(bitmap); 
        }
	};

}
