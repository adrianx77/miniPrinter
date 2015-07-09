package com.wn518.printer.core;

public interface PrintTask {
	String getTitile();
	boolean addText(String text, PrintOption pto);
	boolean addBarcode(String text);
	boolean addQrcode(String text);
	boolean acceptPrint(PrinterInfo printerInfo, Printer printer);
}
