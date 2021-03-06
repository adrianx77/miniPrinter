package com.wn518.receipt;

public class LayoutBuilderWidth31 extends LayoutBuilder{

	public LayoutBuilderWidth31()
	{
	}
	public boolean build()
	{
		map.clear();
		int Width = 31;
		LineLayout  format = new LineLayout(Width);
		format.addCell(5, LineLayout.LAYOUT_ALIGN.LA_LEFT);
		format.addCell(11, LineLayout.LAYOUT_ALIGN.LA_LEFT);
		format.addCell(5, LineLayout.LAYOUT_ALIGN.LA_LEFT);
		format.addCell(10, LineLayout.LAYOUT_ALIGN.LA_LEFT);
		map.put(CompanyStallBlockKey, format);
		
		//商品细节
		format = new LineLayout(Width);
		format.addCell(14, LineLayout.LAYOUT_ALIGN.LA_LEFT);
		format.addCell(5, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		format.addCell(12, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		map.put(GoodsDetailHeaderBlockKey, format);
		
		//客户欠款
		format = new LineLayout(Width);
		format.addCell(19, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		format.addCell(12, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		map.put(ClientDebtBlockKey, format);
				
		
		//优惠
		format = new LineLayout(Width);
		format.addCell(20, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		format.addCell(11, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		map.put(DiscountBlockKey, format);
		//付款方式
		format = new LineLayout(Width);
		format.addCell(20, LineLayout.LAYOUT_ALIGN.LA_LEFT);
		format.addCell(11, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		map.put(PaymentBlockKey, format);
		
		//欠款
		format = new LineLayout(Width);
		format.addCell(19, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		format.addCell(12, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		map.put(DebtBlockKey, format);
		
		//订单行
		format = new LineLayout(Width);
		format.addCell(20, LineLayout.LAYOUT_ALIGN.LA_LEFT);
		format.addCell(11, LineLayout.LAYOUT_ALIGN.LA_RIGHT);
		map.put(OrderLineBlockKey, format);
		return true;
	}
}
