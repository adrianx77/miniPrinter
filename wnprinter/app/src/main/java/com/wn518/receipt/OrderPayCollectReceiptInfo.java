package com.wn518.receipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class OrderPayCollectReceiptInfo extends OrderReceiptInfo implements Serializable {
	private String Cashier;			//收银员
	private String CollectId;		//收款单Id
	private Date CollectTime;		//收款时间
	
	private ArrayList<PayMethod> payments = new ArrayList<PayMethod>();

	public int getPaymentCount()
	{
		return payments.size();
	}
	public PayMethod getPayment(int index)
	{
		if(index>=payments.size())
			return null;
		return payments.get(index);
	}
	public void addPaymethod(double amount,String method)
	{
		payments.add(new PayMethod(amount,method));
	}
	public String getCashier() {
		return Cashier;
	}

	public void setCashier(String cashier) {
		Cashier = cashier;
	}

	public String getCollectId() {
		return CollectId;
	}

	public void setCollectId(String collectId) {
		CollectId = collectId;
	}

	public Date getCollectTime() {
		return CollectTime;
	}

	public void setCollectTime(Date collectTime) {
		CollectTime = collectTime;
	}

	public double getPaid() {
		double Paid = 0;
		for(int i=0;i<payments.size();i++)
		{
			Paid+= payments.get(i).PayAmount;
		}
		return Paid;
	}

}
