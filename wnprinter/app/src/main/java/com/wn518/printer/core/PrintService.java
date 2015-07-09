package com.wn518.printer.core;

import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import com.wn518.printer.core.PrinterLib;


/**
 * Print Service for image,GBK text,Unicode text
 */
public class PrintService{
	protected PrinterInfo mPrinterInfo = null;

	/**
	 * Image Width<br/>
	 * 58mm paper please set imageWidth=48<br/>
	 * 80mm paper please set imageWidth=72
	 */
//	public static int imageWidth=72;
	
	/**
	 * change the text to gbk byte array
	 * @param textStr String text
	 * @return gbk byte array
	 */
	public static byte[] getText(String textStr) {
		byte[] send=null;
		try {
			send = textStr.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			send = textStr.getBytes();
		}
		return send;
	}

	/**
	 * change the bitmap to byte array
	 * @param bitmap
	 * @return byte array
	 */
	public static byte[] getImage(Bitmap bitmap,int parerWidth) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if(parerWidth * 8 != w)
		{
			bitmap=resizeImage(bitmap, parerWidth * 8, h);

			w = bitmap.getWidth();
			h = bitmap.getHeight();
		}
		int[] mIntArray = new int[w * h];
		bitmap.getPixels(mIntArray, 0, w, 0, 0, w, h);
		byte [] header = new byte[]{0x1D,0x76,0x30,0x00,(byte)(w&0xFF),(byte)((w&0xFF00)>>8),(byte)(h&0xFF),(byte)((h&0xFF00)>>8)}; 
		byte[]  bt = PrinterLib.getBitmapDataWithLineHeader(mIntArray, w, h, header);

		bitmap.recycle(); 
		return bt; 
	}

	/**
	 * change the text to unicode byte array
	 * @param textStr String text
	 * @return unicode byte array
	 */
	public byte[] getTextUnicode(String textStr) {
		byte[] send=string2Unicode(textStr);
		return send;
	}
	
	/**
	 * resize the bitmap
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		if(width>w)
		{
			float scaleWidth = ((float) w) / width;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleWidth);
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		}else{
			Bitmap resizedBitmap = Bitmap.createBitmap(w, height+24, Config.RGB_565);
			Canvas canvas = new Canvas(resizedBitmap);
			Paint paint = new Paint();
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bitmap, (w-width)/2, 0, paint);
			return resizedBitmap;
		}
	}
	
	private static byte[] string2Unicode(String s) {   
	    try {   
	      byte[] bytes = s.getBytes("unicode");
	    	byte[] bt=new byte[bytes.length-2];
	      for (int i = 2,j=0; i < bytes.length - 1; i += 2,j += 2) {   
	    	  bt[j]= (byte)(bytes[i + 1] & 0xff);   
	    	  bt[j+1] = (byte)(bytes[i] & 0xff);    
	      }   
	      return bt;   
	    }   
	    catch (Exception e) {   
	      try {
			byte[] bt=s.getBytes("GBK");
			return bt;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
	    }   
	  }
}
