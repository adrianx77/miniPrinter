package com.wn518.receipt;

import java.util.ArrayList;

import com.wn518.printer.core.PrintOption;
import com.wn518.printer.core.PrintTask;
import com.wn518.printer.core.Printer;
/**
 * 收款票据
 * @author adrianx
 *
 */
public class CollectReceipt extends Receipt{
	private static Receipt _instance = null;
	public static Receipt instance()
	{
		if(_instance!=null)
			return _instance;
		_instance = new CollectReceipt();
		return _instance;
	}
	
	private CollectReceipt() {
	}

	public PrintTask MakePrintTask(ReceiptInfo input_info,Printer printer)
	{
		if(input_info==null)
			return null;
		
		CollectReceiptInfo info = (CollectReceiptInfo)input_info;
		
		if(info==null || printer==null)
			return null;
		 
		LayoutBuilder layoutbuilder = printer.getLayoutBuilder();
		
		PrintTask task= printer.createTask(ReceiptTableFixedString.Table_Header);

		//准备打印信息
		//表头
		task.addText(info.getCompany(), PrintOption.headerTextOption);
		//订单
		task.addText(ReceiptTableFixedString.Table_ReceiptId +":" + info.getCollectId(), PrintOption.centerTextOption);
		//订单时间
		task.addText(TimePrintFormat.format(info.getCollectTime()), PrintOption.centerTextOption);
		//商户 + 档口
		
		LineLayout lpfCSB = layoutbuilder.getPrintFormat(LayoutBuilder.CompanyStallBlockKey);
		
		task.addText(lpfCSB.layout(ReceiptTableFixedString.Table_Company,
				info.getCompany(), 
				ReceiptTableFixedString.Table_Stall, 
				info.getStall()), PrintOption.leftTextOption);
		// 收银
		task.addText(lpfCSB.layout("", "",ReceiptTableFixedString.Table_Cashier,info.getCashier()), PrintOption.leftTextOption);
		
		
		//分割条----------------------------------
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		
		//客户
		task.addText(ReceiptTableFixedString.Table_Custom + "\n"+ info.getCustom(), PrintOption.leftTextOption);
		
		LineLayout lpfCDBK = layoutbuilder.getPrintFormat(LayoutBuilder.ClientDebtBlockKey);
		
		task.addText(lpfCDBK.layout(ReceiptTableFixedString.Table_TotoalDebt, "￥"+ MoneyDecimalFormat.format(info.getBetAmount())), PrintOption.leftTextOption);
		//分割条----------------------------------
		
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		
		
		//订单信息表头
		LineLayout lpfOLBK = layoutbuilder.getPrintFormat(LayoutBuilder.OrderLineBlockKey);
		task.addText(lpfOLBK.layout(ReceiptTableFixedString.Table_OrderId,ReceiptTableFixedString.Table_OffDebt), PrintOption.leftTextOption);
		
		//订单信息
		ArrayList<WriteOffDebt> debts = info.getOffDebts();
		for(int i=0;i<debts.size();i++)
		{
			WriteOffDebt item=debts.get(i);
			task.addText(lpfOLBK.layout(item.OrderId,MoneyDecimalFormat.format(item.OffDebtAmount)),PrintOption.leftTextOption);
			task.addText("  "+ item.OrderStatus, PrintOption.leftTextOption);
		}
		
		//分割条----------------------------------
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		
		//合计
		task.addText(lpfOLBK.layout(ReceiptTableFixedString.Table_Totoal,
				MoneyDecimalFormat.format(info.getOffAmount())),
				PrintOption.leftTextOption);
		
		//实收
				task.addText(lpfOLBK.layout(ReceiptTableFixedString.Table_Paid,
						MoneyDecimalFormat.format(info.getPaid())), PrintOption.leftTextOption);
				
		//分割条----------------------------------
		LineLayout lpfPayment = layoutbuilder.getPrintFormat(LayoutBuilder.PaymentBlockKey);
		
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		
		task.addText(ReceiptTableFixedString.Table_PayMethod, PrintOption.leftTextOption);
		for(int i=0;i<info.getPaymentCount();i++)
		{
			PayMethod method = info.getPayment(i);
			task.addText(lpfPayment.layout(method.Method,
					MoneyDecimalFormat.format(method.PayAmount)), PrintOption.leftTextOption);
		}
		//分割条----------------------------------
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
				
		task.addText(ReceiptTableFixedString.Table_Sign, PrintOption.leftTextOption);
		task.addText(" \n\n\n", PrintOption.centerTextOption);
		//分割条----------------------------------
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		task.addText(ReceiptTableFixedString.Table_Footer, PrintOption.footerTextOption);
		task.addText(" \n\n\n", PrintOption.centerTextOption);//留空给切纸指令或者撕纸用
		return task;
	}

}
