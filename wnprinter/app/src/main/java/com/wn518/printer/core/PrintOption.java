package com.wn518.printer.core;

public class PrintOption {
	public boolean FontSizeMini  = false; //true char16 ; false char24
	public boolean VerticalScale = false; //纵向放大 :true ,2倍高; false,1倍高
	public boolean HoriticalScale = false;//横向放大 :true ,2倍宽; false,1倍宽
	public enum PrintOptionAlign{POA_LEFT,POA_CENTER,POA_RIGHT}

	public PrintOptionAlign Align = PrintOptionAlign.POA_LEFT;
	public boolean FontBold = false;
	public boolean FontUnderline = false;
	
	public PrintOption()
	{
		
	}
	
	public PrintOption(boolean fntSizeMini,boolean vertScal,boolean horitScal,PrintOptionAlign align,boolean bold,boolean underline)
	{
		FontSizeMini = fntSizeMini;
		VerticalScale = vertScal;
		HoriticalScale = horitScal;
		Align = align;
		FontBold = bold;
		FontUnderline = underline;
	}
	
	public static final PrintOption defaultOption = new PrintOption();
	public static final PrintOption headerTextOption = new PrintOption(false,true,true,PrintOptionAlign.POA_CENTER,true,false);
    public static final PrintOption subHeaderTextOption = new PrintOption(true,true,true,PrintOptionAlign.POA_CENTER,true,false);

	public static final String splitterContent = "------------------------------";
	public static final PrintOption splitterTextOption =  new PrintOption(true,false,false,PrintOptionAlign.POA_CENTER,false,false);

	public static final PrintOption footerTextOption= new PrintOption(false,false,false,PrintOptionAlign.POA_CENTER,false,false);
	
	public static final PrintOption centerTextOption = new PrintOption(false,false,false,PrintOptionAlign.POA_CENTER,false,false);
	
	public static final PrintOption rightTextOption = new PrintOption(false,false,false,PrintOptionAlign.POA_RIGHT,false,false);
	
	public static final PrintOption leftTextOption = new PrintOption(false,false,false,PrintOptionAlign.POA_LEFT,false,false);
	
	public static final PrintOption flowTextOption = new PrintOption(false,false,false,PrintOptionAlign.POA_CENTER,false,false);

}
