package com.wn518.printer.core.bt;

import java.util.ArrayList;

import com.wn518.printer.core.PrintContent;
import com.wn518.printer.core.PrintTask;
import com.wn518.printer.core.PrintOption;
import com.wn518.printer.core.Printer;
import com.wn518.printer.core.PrinterInfo;

public class BTPrintTask implements PrintTask{

	private String mTitle;
	private ArrayList<PrintContent> contents =new ArrayList<PrintContent>();
	public BTPrintTask(String title)
	{
		mTitle = title;
	}
	@Override
	public String getTitile() {
		return mTitle;
	}

	@Override
	public boolean addText(String text, PrintOption pto) {
		return contents.add(createText(text, pto));
	}
	private PrintContent createText(String text, PrintOption pto)
	{
		return new TextContent(text+"\n",pto);
	}
	private PrintContent createBarcode(String text) {
		return new BarcodeContent(text);	
	}
	private PrintContent createQrcode(String text) {
		return new QrcodeContent(text);
	}

	@Override
	public boolean addBarcode(String text) {
		return contents.add(createBarcode(text));
	}
	@Override
	public boolean addQrcode(String text) {
		return contents.add(createQrcode(text));
	}
	@Override
	public boolean acceptPrint(PrinterInfo printerInfo,Printer printer)
	{
		printerInfo.beforeTask(printer);
		boolean bOK = true;
		for(int i=0;i<contents.size();i++)
		{
			PrintContent content =contents.get(i); 

			bOK =  content.acceptPrint(printerInfo,printer) && bOK;
		}
		printerInfo.finishTask(printer);
		return bOK;
	}
	
}
