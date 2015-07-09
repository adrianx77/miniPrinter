package com.wn518.receipt;

import com.wn518.printer.core.PrintOption;
import com.wn518.printer.core.PrintTask;
import com.wn518.printer.core.Printer;

/**
 * Created by yoke on 2015-07-01.
 */
public class GoodsReceipt extends Receipt {

    private static Receipt _instance = null;
    public static Receipt instance()
    {
        if(_instance!=null)
            return _instance;
        _instance = new GoodsReceipt();
        return _instance;
    }

    @Override
    public PrintTask MakePrintTask(ReceiptInfo input_info, Printer printer) {

        if(input_info==null)
            return null;

        GoodsReceiptInfo info = (GoodsReceiptInfo)input_info;

        if(info==null || printer==null)
            return null;

        PrintTask task= printer.createTask(ReceiptTableFixedString.Table_GoodsCode);

        //准备打印信息
        task.addText(info.getCompany(), PrintOption.headerTextOption);
        //商品名字
        task.addText("\n\n\n", PrintOption.centerTextOption);//留空给切纸指令或者撕纸用
        task.addText(info.getGoodsName(), PrintOption.centerTextOption);
        //商品条形码文字
        task.addText(info.getGoodsCode(), PrintOption.centerTextOption);
        //商品条形码
        task.addBarcode(info.getGoodsCode());
        task.addText("\n\n\n", PrintOption.centerTextOption);//留空给切纸指令或者撕纸用
        return task;
    }
}
