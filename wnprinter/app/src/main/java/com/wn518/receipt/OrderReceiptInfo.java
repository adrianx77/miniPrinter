package com.wn518.receipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class OrderReceiptInfo extends ReceiptInfo implements Serializable{
	private String Company; 		//商户
	private String Custom;
	private String Stall;			//档口
	private String Salesman;		//销售
	private String OrderId; 		//销售订单号
	private Date OrderTime;			//订单确认时间
	private ArrayList<OrderItem> Orders = new ArrayList<OrderItem>();
	
	private double TotoalQuality;	//总数量
	private double OriginAmount;	//总金额
	private double Discount;		//优惠
	private double ShouldCollect;	//应收
	
	public String getSalesman() {
		return Salesman;
	}

	public void setSalesman(String salesman) {
		Salesman = salesman;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public Date getOrderTime() {
		return OrderTime;
	}

	public void setOrderTime(Date orderTime) {
		OrderTime = orderTime;
	}

	public ArrayList<OrderItem> getOrders() {
		return Orders;
	}

	public double getDiscount() {
		return Discount;
	}

	public void setDiscount(double discount) {
		Discount = discount;
	}

	public double getShouldCollect() {
		return ShouldCollect;
	}

	public String getCustom() {
		return Custom;
	}

	public void setCustom(String custom) {
		Custom = custom;
	}

	public void addDetailLine(String goodsNo,double price,double quality,String goodsRemark)
	{
		OrderItem item = new OrderItem();
		item.goodsNo = goodsNo ;
		item.quality = quality;
		item.price   = price;
		item.goodsRemark = goodsRemark;
		Orders.add(item);
		flushData();
	}
	
	protected void flushData()
	{
		//准备数据
		TotoalQuality = 0;
		OriginAmount = 0;
		for(int i=0;i<Orders.size();i++)
		{
			TotoalQuality +=Orders.get(i).quality;
			OriginAmount += Orders.get(i).price * Orders.get(i).quality;
		}
		
		ShouldCollect = OriginAmount - Discount;		
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

	public double getOriginAmount() {
		return OriginAmount;
	}

	public double getTotoalQuality() {
		return TotoalQuality;
	}
}
