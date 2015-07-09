package com.wn518.printer.core.bt;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

import com.wn518.printer.core.BarcodeCreater;
import com.wn518.printer.core.Printer;
import com.wn518.printer.core.PrinterInfo;
import com.wn518.printer.core.PrinterLib;

public class ZKCPrinterInfo extends PrinterInfo {
	
	static byte[] LineHeader48 = new byte[]{0x1F,0x10,48,0};
	static byte[] LineHeader72 = new byte[]{0x1F,0x10,72,0};
	byte[] lineHeader;
	public ZKCPrinterInfo(Context ctx) {
		super("ZKC Pinter Info",false,false,true,true,ctx);
		maxWriteChars=100;
		maxWriteBytes=100;
	}
	@Override
	public void beforeTask(Printer printer) {
	}
	@Override
	public void finishTask(Printer printer) {
	}
	@Override
	public void initializePrinter(Printer printer) {
		PrinterCommand.getPrinterInfo(printer);
	}
	public void setPaperWidth(int pWidth) 
	{
		super.setPaperWidth(pWidth);
		if(pWidth==48)
			lineHeader = LineHeader48;
		else
			lineHeader = LineHeader72;
	}
	@Override
	public byte[] getImageCommand(Bitmap bmp) {
		int mWidth = bmp.getWidth();
		int mHeight = bmp.getHeight();
		if(paperWidth * 8 != mWidth)
		{
			bmp=resizeImage(bmp, paperWidth * 8, mHeight);

			mWidth = bmp.getWidth();
			mHeight = bmp.getHeight();
		}
		int[] mIntArray = new int[mWidth * mHeight];
		bmp.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
		byte[]  bt = PrinterLib.getBitmapDataWithLineHeader(mIntArray, mWidth, mHeight, lineHeader);
		bmp.recycle(); 
		return bt;
	}
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
	@Override
	public byte[] getQrcodeCommand(String codeString) {
		int Width = getPaperWidth()*8;
		int Height = Width;
		Bitmap image = BarcodeCreater.creatBarcode(getContext(),
				codeString, 
				Width, 
				Height, 
				 false ,2 );
		return getImageCommand(image);
	}
	@Override
	public byte[] getBarcodeCommand(String codeString) {
		Bitmap image = BarcodeCreater.creatBarcode(getContext(), 
				codeString, 
				getPaperWidth()*8, 
				100, 
				false ,1 );
		return getImageCommand(image);
	}
}
