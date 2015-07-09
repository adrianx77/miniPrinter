package com.wn518.receipt;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.wn518.printer.core.PrintTask;
import com.wn518.printer.core.Printer;

public abstract class Receipt {
	protected static DecimalFormat QualityDecimalFormat = new DecimalFormat("#0.0");
	protected static DecimalFormat MoneyDecimalFormat   = new DecimalFormat("#0.00");
	protected static SimpleDateFormat TimePrintFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Receipt()
	{
	}
	public abstract PrintTask MakePrintTask(ReceiptInfo input_info,Printer printer);
}

