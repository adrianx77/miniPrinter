package com.wn518.printer.core;

public class PrinterLib {
	static
	{
		System.loadLibrary("printer");
	}
	public native static byte[] getBitmapData(int[] bitdata,int w,int h);
	public native static byte[] getBitmapDataWithHeader(int[] bitdata,int w,int h,byte[]Header);
	public native static byte[] getBitmapDataWithLineHeader(int[] bitdata,int w,int h,byte[]lineHeader);
}
