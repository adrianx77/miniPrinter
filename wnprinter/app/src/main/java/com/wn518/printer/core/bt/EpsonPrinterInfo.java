package com.wn518.printer.core.bt;
import android.content.Context;
import android.graphics.Bitmap;

import com.wn518.printer.core.BarcodeCreater;
import com.wn518.printer.core.Printer;
import com.wn518.printer.core.PrinterInfo;
import com.wn518.printer.core.PrinterLib;

public class EpsonPrinterInfo extends PrinterInfo {
	public EpsonPrinterInfo(boolean barCmd,boolean qrCmd,boolean wif,boolean devinfo,Context ctx) {
		super("EPSON Pinter Info",barCmd, qrCmd,wif,devinfo, ctx);
		setPaperWidth(72);
		
		if(getPaperWidth()==48)
			charWidth = 31;
		else
			charWidth = 48;
		maxWriteBytes = 20;
		maxWriteChars = 100;
	}
	@Override
	public void beforeTask(Printer printer) {
		PrinterCommand.writeResetPrinter(printer);
	}
	public void finishTask(Printer printer) {
		PrinterCommand.writeCutPaper(printer);
	}
	@Override
	public void initializePrinter(Printer printer) {
		PrinterCommand.writeResetPrinter(printer);
	}
	@Override
	public byte[] getImageCommand(Bitmap bmp) {
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		
		int[] bitData = new int[w * h];
		bmp.getPixels(bitData, 0, w, 0, 0, w, h);
		
		int wX = (w % 8 == 0 ? w / 8 : (w / 8 + 1));
		int hX = h;
		
		byte [] header = new byte[]{0x1D,0x76,0x30,0x00,(byte)(wX&0xFF),(byte)((wX&0xFF00)>>8),(byte)(hX&0xFF),(byte)((hX&0xFF00)>>8)}; 
		return PrinterLib.getBitmapDataWithHeader(bitData, w, h, header);
	}
	@Override
	public byte[] getQrcodeCommand(String code) {
		if(getQrcodeByCommand())
		{
			return getQrcodeCommandBytes(code);
		}
		else
			return getQrcodeImageBytes(code);
	}
	@Override
	public byte[] getBarcodeCommand(String codeString) 
	{
		if(getBarcodeByCommand())
		{
			return getBarcodeCommandBytes(codeString);
		}
		else
			return getBarcodeImageBytes(codeString);
	}

	
	byte [] getQrcodeImageBytes(String code)
	{
		int Width = getPaperWidth()*8;
		int Height = Width;
		Bitmap image = BarcodeCreater.creatBarcode(getContext(),
				code, 
				Width, 
				Height, 
				 false ,2 );
		return getImageCommand(image);
	}
	byte [] getBarcodeImageBytes(String code)
	{
		Bitmap image = BarcodeCreater.creatBarcode(getContext(), 
				code, 
				getPaperWidth()*8, 
				100, 
				false ,1 );
		return getImageCommand(image);
	}
	

	byte[] getBarcodeCommandBytes(String codeString)
	{
		int Num = codeString.length() + 2;
		byte [] cmdBytes = new byte[]{0x1D,0x77,0x02, 0x1D,0x6B,0x49,(byte)Num,0x7B,0x42};
		byte [] printBytes = new byte[codeString.length()+cmdBytes.length+1];
		byte [] strBytes = codeString.getBytes();
		int destIndx = 0;
		System.arraycopy(cmdBytes, 0, printBytes, destIndx, cmdBytes.length);
		destIndx+=cmdBytes.length;
		
		System.arraycopy(strBytes, 0, printBytes, destIndx, strBytes.length);
		destIndx+=strBytes.length;
		
		printBytes[destIndx]='\n';
		return printBytes;
	}
	
	byte[] getQrcodeCommandBytes2(String codeString)
	{
		int num = codeString.length();
		byte [] precode = new byte[]{0x1B,0x5A,0x1,0x03,0x05,(byte)(num&0xFF),(byte)((num&0xFF00)>>8)};
		
		byte [] printBytes = new byte[num+precode.length];
		System.arraycopy(precode, 0, printBytes, 0, precode.length);
		System.arraycopy(codeString.getBytes(), 0, printBytes, precode.length, precode.length);
		return printBytes;
	}
	byte[] getQrcodeCommandBytes(String code)
	{
		byte [] printBytes;
		byte [] precode = new byte[]{0x1D,0x28,0x6B,0x03,0x00,0x31,0x43,0x0A,
				0x1D,0x28,0x6B,0x03,0x00,0x31,
				0x45,0x31,0x1D,0x28,0x6B,0x0};
		byte [] cntcode = new byte[]{0x00,0x31,0x50,0x30};
		byte [] csrc = code.getBytes();
		byte [] postcode = new byte[]{0x1D,0x28,0x6B,0x03,0x00,0x31,0x51,0x30,0x0A};
		
		if(code.length()==0)
			return null;
		int allLen = precode.length + cntcode.length + postcode.length + csrc.length;
		int dstPos = 0;
		byte [] cby = new byte[allLen];	
		System.arraycopy(precode, 0, cby, dstPos, precode.length);//precode
		dstPos+=precode.length;
		
		System.arraycopy(cntcode, 0, cby, dstPos, cntcode.length);//cntcode
		dstPos+=cntcode.length;
		
		System.arraycopy(csrc, 0, cby, dstPos, csrc.length);//csrc
		dstPos+=csrc.length;
		
		System.arraycopy(postcode, 0, cby, dstPos, postcode.length);//postcode
		dstPos+=postcode.length;
		
		cby[precode.length-1] =  (byte)(csrc.length+3);
		
		printBytes = cby;		
		return printBytes;		
	}

}
