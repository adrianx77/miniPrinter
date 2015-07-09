package com.wn518.printer.core.bt;

import java.util.Map;

import android.content.Context;
import android.os.Handler;

import com.wn518.printer.core.Printer;
import com.wn518.printer.core.PrinterFactory;

public class BTPrinterFactory implements PrinterFactory{
	
	static BTPrinterFactory mInstance = null; 
	public static PrinterFactory inistance()
	{
		if(mInstance!=null)
			return mInstance;
		
		mInstance = new BTPrinterFactory();
		return mInstance;
	}
	@Override
	public Printer createPrinter(Context context,Handler _Handler) {
		return new BTPrinter(context,_Handler);
	}
}
