package com.wn518.printer.core.bt;


import com.wn518.printer.core.PrintContent;
import com.wn518.printer.core.Printer;
import com.wn518.printer.core.PrinterInfo;

public class QrcodeContent extends PrintContent {
	
	String mCode;
	public QrcodeContent(String code)
	{
		super(PrintContent.CONTENT_TYPE.QRCODE);
		mCode = code;
	}
	@Override
	public boolean acceptPrint(PrinterInfo printerInfo, Printer printer) {
		byte [] printBytes = printerInfo.getQrcodeCommand(mCode);
		if(printerInfo.getWriteImageFull())
		{
			return printer.PrintImageBytes(printBytes);
		}
		else
		{
			if(printBytes!=null && printBytes.length!=0)
			{
				PrinterCommand.writeEnterLine(printer, 2);
				return printer.SendPrintCommand(printBytes);
			}
		}
		return false;
	}
}
	
