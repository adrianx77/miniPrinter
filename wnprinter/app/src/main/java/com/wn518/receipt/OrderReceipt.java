package com.wn518.receipt;

import java.util.ArrayList;

import com.wn518.printer.core.PrintOption;
import com.wn518.printer.core.PrintTask;
import com.wn518.printer.core.Printer;

public class OrderReceipt extends Receipt{
	private static Receipt _instance = null;
	public static Receipt instance()
	{
		if(_instance!=null)
			return _instance;
		_instance = new OrderReceipt();
		return _instance;
	}
	
	private OrderReceipt() {
	}

	public PrintTask MakePrintTask(ReceiptInfo input_info,Printer printer)
	{
		if(input_info==null)
			return null;
		
		OrderReceiptInfo info = (OrderReceiptInfo)input_info;
		
		if(info==null || printer==null)
			return null;
		 
		LayoutBuilder layoutbuilder = printer.getLayoutBuilder();
		
		PrintTask task= printer.createTask(ReceiptTableFixedString.Table_Header);

		//准备打印信息
		//表头
		task.addText(info.getCompany(), PrintOption.headerTextOption);
		//订单
		task.addText(ReceiptTableFixedString.Table_OrderId + ":" + info.getOrderId(), PrintOption.centerTextOption);
		//订单时间
		task.addText(TimePrintFormat.format(info.getOrderTime()), PrintOption.centerTextOption);
		//订单条形码
		task.addBarcode(info.getOrderId());
		
		//客户
		task.addText(ReceiptTableFixedString.Table_Custom + "\n"+ info.getCustom(), PrintOption.leftTextOption);
		//分割条----------------------------------
		
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		//商户 + 档口
		
		LineLayout lpfCSB = layoutbuilder.getPrintFormat(LayoutBuilder.CompanyStallBlockKey);
		
		task.addText(lpfCSB.layout(ReceiptTableFixedString.Table_Company,
				info.getCompany(), 
				ReceiptTableFixedString.Table_Stall, 
				info.getStall()), PrintOption.leftTextOption);
		
		//销售
		task.addText(lpfCSB.layout(ReceiptTableFixedString.Table_Salesman, 
				info.getSalesman()), PrintOption.leftTextOption);
		
		//分割条----------------------------------
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		//商品细节表头
		LineLayout lpfGDHB = layoutbuilder.getPrintFormat(LayoutBuilder.GoodsDetailHeaderBlockKey);
		task.addText(lpfGDHB.layout(ReceiptTableFixedString.Table_Goods,
				ReceiptTableFixedString.Table_Quality,
				ReceiptTableFixedString.Table_Amount), PrintOption.leftTextOption);
		
		//各条商品信息
		ArrayList<OrderItem> orders = info.getOrders();
		for(int i=0;i<orders.size();i++)
		{
			OrderItem item=orders.get(i);
			task.addText(lpfGDHB.layout(item.goodsNo+"(￥"+ MoneyDecimalFormat.format(item.price) +")", 
					QualityDecimalFormat.format(item.quality), 
					MoneyDecimalFormat.format(item.price*item.quality)),
					PrintOption.leftTextOption);
			task.addText("  "+ item.goodsRemark, PrintOption.leftTextOption);
		}
		
		//分割条----------------------------------
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		
		//合计
		task.addText(lpfGDHB.layout(ReceiptTableFixedString.Table_Totoal, 
				QualityDecimalFormat.format(info.getTotoalQuality()), 
				MoneyDecimalFormat.format(info.getOriginAmount())),
				PrintOption.leftTextOption);
		
		//优惠
		LineLayout lpfDiscount = layoutbuilder.getPrintFormat(LayoutBuilder.DiscountBlockKey);
		
		task.addText(lpfDiscount.layout(ReceiptTableFixedString.Table_Discount,
				MoneyDecimalFormat.format(info.getDiscount())), PrintOption.leftTextOption);
		//应收
		task.addText(lpfDiscount.layout(ReceiptTableFixedString.Table_ShouldCollect,
				MoneyDecimalFormat.format(info.getShouldCollect())), PrintOption.leftTextOption);
		
		//分割条----------------------------------
		task.addText(PrintOption.splitterContent, PrintOption.splitterTextOption);
		task.addText(ReceiptTableFixedString.Table_Footer, PrintOption.footerTextOption);
		task.addText(" \n\n\n", PrintOption.centerTextOption);//留空给切纸指令或者撕纸用
		return task;
	}

}
