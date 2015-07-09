package com.wn518.printer.core.bt;


import com.wn518.printer.core.PrintContent;
import com.wn518.printer.core.PrinterInfo;
import com.wn518.printer.core.PrintOption;
import com.wn518.printer.core.Printer;

public class TextContent extends PrintContent
{
	protected PrintOption mTextOpt;
	protected String mContent;
	public TextContent(String content,PrintOption textopt) {
		super(CONTENT_TYPE.TEXT);
		mTextOpt = textopt;
		mContent = content;
	}

	@Override
	public boolean acceptPrint(PrinterInfo printerInfo, Printer printer) {
		PrinterCommand.accept(printer,mTextOpt);
		return printer.PrintText(mContent);
	}
}
