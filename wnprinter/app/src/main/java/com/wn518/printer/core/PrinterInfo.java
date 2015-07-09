package com.wn518.printer.core;

import com.wn518.receipt.LayoutBuilder;
import com.wn518.receipt.LayoutBuilderWidth31;
import com.wn518.receipt.LayoutBuilderWidth48;

import android.content.Context;
import android.graphics.Bitmap;

public abstract class PrinterInfo {
	protected String  Name;
	protected int     paperWidth;
	protected int     charWidth;
	private Context context;
	protected LayoutBuilder formatBuilder31 = null;			
	protected LayoutBuilder formatBuilder48 = null;			
	protected int maxWriteBytes;
	protected int maxWriteChars;
	private boolean barcodeByCommand = true;
	private boolean qrcodeByCommand = true;
	private boolean canDeviceInfo = false;
	private boolean writeImageFull = true;
	
	public PrinterInfo(String infoName,boolean barCmd,boolean qrCmd,boolean wif,boolean devinfo,Context ctx)
	{
		Name = infoName;
		barcodeByCommand = barCmd;
		qrcodeByCommand = qrCmd;
		writeImageFull = wif;
		canDeviceInfo = devinfo;
		context = ctx;
	}
	public abstract void beforeTask(Printer printer);
	public abstract void finishTask(Printer printer);
	public abstract void initializePrinter(Printer printer);
	public String getName() {
		return Name;
	}
	public int getPaperWidth() {
		return paperWidth;
	}
	public int getCharWidth() {
		return charWidth;
	}

	public void setPaperWidth(int pWidth) 
	{
		paperWidth = pWidth;
		if(paperWidth==48)
			charWidth = 31;
		else
			charWidth = 48;
	}

	public Context getContext() {
		return context;
	}
	
	public int getMaxWriteBytes() {
		return maxWriteBytes;
	}


	public int getMaxWriteChars() {
		return maxWriteChars;
	}
	public abstract byte[] getImageCommand(Bitmap bmp);
	public abstract byte[] getQrcodeCommand(String codeString);
	public abstract byte[] getBarcodeCommand(String codeString);
	
	public LayoutBuilder getLayoutBuilder()
	{
		if(getCharWidth()==31)
		{
			if(formatBuilder31!=null)
				return formatBuilder31;
			
			formatBuilder31= new LayoutBuilderWidth31();
			formatBuilder31.build();
			return formatBuilder31;
		}
		else
		{
			if(formatBuilder48!=null)
				return formatBuilder48;
			
			formatBuilder48= new LayoutBuilderWidth48();
			formatBuilder48.build();
			return formatBuilder48;
		}
	}
	public boolean getanDeviceInfo() {
		return canDeviceInfo;
	}
	public boolean getBarcodeByCommand() {
		return barcodeByCommand;
	}
	public void setBarcodeByCommand(boolean barcodeByCommand) {
		this.barcodeByCommand = barcodeByCommand;
	}
	public boolean getQrcodeByCommand() {
		return qrcodeByCommand;
	}
	public void setQrcodeByCommand(boolean qrcodeByCommand) {
		this.qrcodeByCommand = qrcodeByCommand;
	}
	public boolean getWriteImageFull() {
		return writeImageFull;
	}
	public void setWriteImageFull(boolean writeImageFull) {
		this.writeImageFull = writeImageFull;
	}

}
