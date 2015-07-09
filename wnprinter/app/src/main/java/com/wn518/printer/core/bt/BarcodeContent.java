package com.wn518.printer.core.bt;

import com.wn518.printer.core.PrintContent;
import com.wn518.printer.core.Printer;
import com.wn518.printer.core.PrinterInfo;

public class BarcodeContent  extends PrintContent {
	protected String  codeString;
	public BarcodeContent(String code) {
		super(CONTENT_TYPE.BARCODE);
		codeString = code;
	}


	@Override
	public boolean acceptPrint(PrinterInfo printerInfo, Printer printer) {
		byte [] printBytes = printerInfo.getBarcodeCommand(codeString);
		if(printerInfo.getWriteImageFull())
		{
			return printer.PrintImageBytes(printBytes);
		}
		else
		{
			if(printBytes!=null && printBytes.length!=0)
			{
				 printer.SendPrintCommand(printBytes);
				 printer.PrintText("\n");
			}
		}
		return false;
	}
	
}
