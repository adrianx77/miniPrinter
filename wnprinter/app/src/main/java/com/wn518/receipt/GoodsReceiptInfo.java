package com.wn518.receipt;

/**
 * Created by yoke on 2015-07-01.
 */
public class GoodsReceiptInfo extends ReceiptInfo {
	private String Company;
    private String goodsName;
    private String goodsCode;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

	public String getCompany() {
		return Company;
	}

	public void setCompany(String company) {
		Company = company;
	}
}
