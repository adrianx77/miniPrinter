package com.wn518.receipt;

import java.io.Serializable;

public class PayMethod implements Serializable {
	PayMethod(double amount,String method)
	{
		Method = method;
		PayAmount = amount;
	}
	public String Method;
	public double PayAmount;
}
