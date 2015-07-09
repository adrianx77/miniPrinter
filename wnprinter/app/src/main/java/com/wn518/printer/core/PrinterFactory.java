package com.wn518.printer.core;

import java.util.Map;

import android.content.Context;
import android.os.Handler;

public interface PrinterFactory {
	Printer createPrinter(Context context, Handler _Handler);
	
}
