package com.wn518.printer.core.bt;

import com.wn518.printer.core.PrintOption;
import com.wn518.printer.core.PrintOption.PrintOptionAlign;
import com.wn518.printer.core.Printer;

public class PrinterCommand {
	public static final byte HT = 0x9; // 水平制表
	public static final byte LF = 0x0A; // 打印并换行
	public static final byte CR = 0x0D; // 打印回车
	public static final byte ESC = 0x1B;
	public static final byte DLE = 0x10;
	public static final byte GS = 0x1D;
	public static final byte FS = 0x1C;
	public static final byte STX = 0x02;
	public static final byte US = 0x1F;
	public static final byte CAN = 0x18;
	public static final byte CLR = 0x0C;
	public static final byte EOT = 0x04;
	
	public static final byte[] LOCATION = {0x1B, 0x44, 0x04, 0x00};
	/* 默认颜色字体指令 */
	public static final byte[] ESC_FONT_COLOR_DEFAULT = new byte[] { ESC, 'r',0x00 };
	
	/* 标准大小 */
	public static final byte[] FS_FONT_ALIGN = new byte[] { FS, 0x21, 1, ESC, 0x21, 1 };
	/* 靠左打印命令 */
	public static final byte[] ESC_ALIGN_LEFT = new byte[] { ESC, 'a', 0x00 };
	
	/* 靠右打印命令 */
	public static final byte[] ESC_ALIGN_RIGHT = new byte[] { ESC, 'a', 0x02 };
	
	/* 居中打印命令 */
	public static final byte[] ESC_ALIGN_CENTER = new byte[] { ESC, 'a', 0x01 };
	
	/* 取消字体加粗 */
	public static final byte[] ESC_CANCEL_BOLD = new byte[] { ESC, 0x45, 0 };
	
	/* 设置字体加粗 */
	public static final byte[] ESC_SET_BOLD = new byte[] { ESC, 0x45, 0x01 };
	
	/* 取消字体下划线 */
	public static final byte[] ESC_CANCEL_UNDERLINE = new byte[] { FS,0x2D,0x00 };
	
	/* 设置字体下划线 */
	public static final byte[] ESC_SET_UNDERLINE = new byte[] {FS,0x2D,0x01 };
	
	/* 取消小字体 */
	public static final byte[] ESC_CANCEL_FONTMIN = new byte[] { ESC,0x4D,0x00 };
	
	/* 设置小字体 */
	public static final byte[] ESC_SET_FONTMIN = new byte[] {ESC,0x4D,0x01 };
	
	/* 取消字体放大 */
	public static final byte[] ESC_CANCEL_FONTSCAL = new byte[] { GS,0x21,0x00 };
	/* 字体宽2倍放大 */
	public static final byte[] ESC_SET_HORTISCAL = new byte[] { GS,0x21,0x10 };
	/* 字体高2倍放大 */
	public static final byte[] ESC_SET_VERTISCAL  = new byte[] { GS,0x21,0x01 };
	/* 字体宽高都2倍放大 */
	public static final byte[] ESC_SET_BOTHSCAL = new byte[] { GS,0x21,0x11 };
	
	/* 初始化打印机*/
	public static final byte[] ESC_RESETPRINTER = new byte[] { ESC,0x40};
	
	/*********************************************/
	/* 水平定位 */
	public static final byte[] ESC_HORIZONTAL_CENTERS = new byte[] { ESC, 0x44, 20, 28, 00};
	
	/* 取消水平定位 */
	public static final byte[] ESC_CANCLE_HORIZONTAL_CENTERS = new byte[] { ESC, 0x44, 00 };
	/*********************************************/
	
	
	// 进纸
	public static final byte[] ESC_ENTER = new byte[] { 0x1B, 0x4A, 0x40 };

	//切纸
	public static final byte[] ESC_CUTPAPER = new byte []{ESC,0x6D};
	
	// 自检
	public static final byte[] PRINTE_TEST = new byte[] { 0x1D, 0x28, 0x41 };
	
	
	//Device Info
	public static final byte[] DEVICE_INFO = new byte[] {0x1B, 0x2B };

	public static void writeCenter(Printer printer){
		printer.SendPrintCommand(ESC_ALIGN_CENTER);
		writeEnterLine(printer,1);
	}
	
	public static void writeEnterLine(Printer printer,int count) {
		for (int i = 0; i < count; i++) {
			printer.SendPrintCommand(ESC_ENTER);
		}
	}
	
	public static void writeResetPrinter(Printer printer)
	{
		printer.SendPrintCommand(ESC_RESETPRINTER);
	}
	
	public static void writeCutPaper(Printer printer) 
	{
		printer.SendPrintCommand(ESC_CUTPAPER);
	}
	
	public static void getPrinterInfo(Printer printer) 
	{
		printer.SendPrintCommand(DEVICE_INFO);
	}
	
	public static boolean accept(Printer printer,PrintOption po)
	{
		if(po.FontSizeMini)
			printer.SendPrintCommand(ESC_SET_FONTMIN);
		else
			printer.SendPrintCommand(ESC_CANCEL_FONTMIN);
		
		if(po.VerticalScale && po.HoriticalScale)
			printer.SendPrintCommand(ESC_SET_BOTHSCAL);
		else if(po.VerticalScale)
			printer.SendPrintCommand(ESC_SET_VERTISCAL);
		else if(po.HoriticalScale)
			printer.SendPrintCommand(ESC_SET_HORTISCAL);
		else
			printer.SendPrintCommand(ESC_CANCEL_FONTSCAL);
		
		if(po.Align==PrintOptionAlign.POA_LEFT)
			printer.SendPrintCommand(ESC_ALIGN_LEFT);
		else if(po.Align==PrintOptionAlign.POA_RIGHT)
			printer.SendPrintCommand(ESC_ALIGN_RIGHT);
		else
			printer.SendPrintCommand(ESC_ALIGN_CENTER);
		
		if(po.FontBold)
			printer.SendPrintCommand(ESC_SET_BOLD);
		else
			printer.SendPrintCommand(ESC_CANCEL_BOLD);
		
		if(po.FontUnderline)
			printer.SendPrintCommand(ESC_SET_UNDERLINE);
		else
			printer.SendPrintCommand(ESC_CANCEL_UNDERLINE);
		return true;
	}
	
}
