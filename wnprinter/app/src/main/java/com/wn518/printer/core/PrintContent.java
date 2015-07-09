package com.wn518.printer.core;

public abstract class PrintContent {
	public CONTENT_TYPE type;
	public PrintContent(CONTENT_TYPE _type)
	{
		type = _type;
	}
	public enum CONTENT_TYPE{TEXT,IMAGE,BARCODE,QRCODE}

	public abstract boolean acceptPrint(PrinterInfo printerInfo,Printer printer);
}

