package com.wn518.wnprinter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wn518.printer.core.PrintTask;
import com.wn518.printer.core.Printer;
import com.wn518.printer.core.PrinterConst;
import com.wn518.printer.core.bt.BTPrinterFactory;
import com.wn518.receipt.OrderReceipt;
import com.wn518.receipt.OrderReceiptInfo;
import com.wn518.receipt.Receipt;

import java.util.Date;


public class PrinterActivity extends Activity {
    private Handler mHandler;
    private TextView statLable;
    private Printer printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        statLable = (TextView) this.findViewById(R.id.lab_status);
        Button btnPrint = (Button) this.findViewById(R.id.btn_printer);

        statLable.setText("未连接");
        buildHandler();
        printer= BTPrinterFactory.inistance().createPrinter(this,mHandler);
        printer.init();
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderReceiptInfo info = new OrderReceiptInfo();
                info.setOrderId("DD34567890123456789012");
                info.setOrderTime(new Date());

                info.setCompany("兆丰百源");
                info.setStall("B-13");
                info.setCustom("姚明(18946531535)");

                info.setSalesman("王志峰");

                info.addDetailLine("01235", 20, 520, "副牌山竹4A(XL)泰国，马来西亚");
                info.addDetailLine("01232", 25, 520, "副牌山竹4A(XL)泰国，马来西亚");
                info.addDetailLine("01232", 120, 520, "副牌山竹4A(XL)泰国，马来西亚");

                info.setDiscount(1000);
                Receipt r = OrderReceipt.instance();

                PrintTask task = r.MakePrintTask(info, printer);

                printer.postTask(task);
            }
        });
    }

    void buildHandler()
    {
        if(mHandler!=null)
            return;
        mHandler = new Handler()
        {
            public void handleMessage(Message msg) {
                switch(msg.what)
                {
                    case PrinterConst.MSG_CONNECTING:
                        statLable.setText("正在连接:"+msg.obj.toString());
                        break;
                    case PrinterConst.MSG_CONNECT_SUCCESSED:
                        statLable.setText("连接成功:"+msg.obj.toString());
                        break;
                    case PrinterConst.MSG_CONNECT_FAILED:
                        statLable.setText("连接失败:"+msg.obj.toString());
                        break;
                    case PrinterConst.MSG_CONNECT_DISCONNECTED:
                        statLable.setText("连接断开:"+msg.obj.toString());
                        break;
                    case PrinterConst.MSG_BUFFER_FULL:
                        statLable.setText("缓冲区写满错误!");
                        break;
                    case PrinterConst.MSG_TASK_PRINTING:
                        statLable.setText("正在打印:"+msg.obj.toString());
                        break;
                    case PrinterConst.MSG_TASK_PRINTED:
                        statLable.setText("打印完成:"+msg.obj.toString());
                        break;
                }
            }
        };
    }
}
