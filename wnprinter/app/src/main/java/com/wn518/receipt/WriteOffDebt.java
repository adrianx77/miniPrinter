package com.wn518.receipt;

import java.io.Serializable;

public class WriteOffDebt implements Serializable {
	String OrderId;
	String OrderStatus;
	double OffDebtAmount;
}
