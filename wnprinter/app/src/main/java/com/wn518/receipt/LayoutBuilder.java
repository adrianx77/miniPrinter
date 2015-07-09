package com.wn518.receipt;

import java.util.HashMap;

public abstract  class LayoutBuilder {
	public final static String CompanyStallBlockKey = "CompanyStallBlockFormat";
	public final static String GoodsDetailHeaderBlockKey = "GoodsDetailHeader";
	public final static String ClientDebtBlockKey = "ClientDebtBlockKey";
	public final static String DiscountBlockKey = "Discount";
	public final static String PaymentBlockKey = "Payment";
	public final static String DebtBlockKey = "Debt";
	public final static String OrderLineBlockKey = "OrderLine";
	public abstract boolean build();
	protected HashMap<String,LineLayout> map= new HashMap<String,LineLayout>(); 
	
	public LineLayout getPrintFormat(String key)
	{
		if(map.containsKey(key))
		{
			return map.get(key);
		}
		return null;
	}
}
