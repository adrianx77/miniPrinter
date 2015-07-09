package com.wn518.receipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
/** 收款票据信息
 * 
 * @author adrianx
 *
 */

public class CollectReceiptInfo extends ReceiptInfo implements Serializable {
	private String Company; 		//商户
	private String CollectId;		//收款单
	private Date   CollectTime;		//收款时间
	private String Stall;			//档口
	private String Cashier;		//收银
	private String Custom;
	private double BetAmount;		//总欠款
	private ArrayList<WriteOffDebt> OffDebts = new ArrayList<WriteOffDebt>();
	private double OffAmount;		//收款金额
	private ArrayList<PayMethod> payments = new ArrayList<PayMethod>();
	
	public String getCustom() {
		return Custom;
	}

	public void setCustom(String custom) {
		Custom = custom;
	}

	public void addWriteOffBet(String orderId,String OrderStatus ,double debt)
	{
		WriteOffDebt off = new WriteOffDebt();
		off.OrderStatus = OrderStatus;
		off.OrderId = orderId;
		off.OffDebtAmount = debt;
		OffDebts.add(off);
		flushData();
	}
	public ArrayList<WriteOffDebt> getOffDebts()
	{
		return OffDebts;
	}
	protected void flushData()
	{
		//准备数据
		OffAmount=0;
		for(int i=0;i<OffDebts.size();i++)
		{
			OffAmount += OffDebts.get(i).OffDebtAmount;
		}
	}

	public String getCompany() {
		return Company;
	}

	public void setCompany(String Company) {
		this.Company = Company;
	}

	public String getStall() {
		return Stall;
	}

	public void setStall(String stall) {
		Stall = stall;
	}
	
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

	public double getBetAmount() {
		return BetAmount;
	}

	public void setBetAmount(double betAmount) {
		BetAmount = betAmount;
	}

	public double getOffAmount() {
		return OffAmount;
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
