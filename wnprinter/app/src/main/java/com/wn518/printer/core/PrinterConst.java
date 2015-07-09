package com.wn518.printer.core;

import java.util.UUID;

/**
 * Created by adrianx on 2015/7/8.
 */
public class PrinterConst {
    //内部 cmd
    public static final int MSG_CMD_INITIALIZE = 1;
    public static final int MSG_CMD_RECONNNECT   = 2;
    public static final int MSG_CMD_PRINT_TASK = 4;
    public static final int MSG_CMD_STOP	   = 5;
    public static final int MSG_CMD_TIMER		= 6;

    //内部service 发出
    public static final int MSG_SVC_DEVICE_READ= 11;
    public static final int MSG_SVC_DEVICE_WRITE = 12;
    public static final int MSG_SVC_CONNECTED = 13;
    public static final int MSG_SVC_CONNECT_FAILED = 14;
    public static final int MSG_SVC_CONNECT_LOST = 15;
    //发出消息
    public static final int MSG_CONNECTING                  = 1000;                     //obj:address
    public static final int MSG_CONNECT_SUCCESSED           = MSG_CONNECTING+1;         //obj:address
    public static final int MSG_CONNECT_FAILED              = MSG_CONNECT_SUCCESSED+1;  //obj:address
    public static final int MSG_CONNECT_DISCONNECTED        = MSG_CONNECT_FAILED+1;     //obj:address
    public static final int MSG_BUFFER_FULL                 = MSG_CONNECT_DISCONNECTED+1;
    public static final int MSG_TASK_PRINTING               = MSG_BUFFER_FULL+1;        //obj:taskTitle
    public static final int MSG_TASK_PRINTED                = MSG_TASK_PRINTING+1;      //obj:taskTitle

    //打印机的Class字符串
    public static final String BTPrinterClasses = "1f00";

    //蓝牙SPP的服务UUID
    public static final UUID SPP_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //蓝牙状态
    public static final int BLUETOOTH_STATE_NONE = 0;
    public static final int BLUETOOTH_STATE_CONNECTING = 1;
    public static final int BLUETOOTH_STATE_CONNECTED = 2;

    //工作状态
    public static final int PRINTER_WORK_STATE_UNKNOWN = 0;
    public static final int PRINTER_WORK_STATE_INITING = 1;
    public static final int PRINTER_WORK_STATE_INITIALIZED = 2;
    public static final int PRINTER_WORK_STATE_CONNECTING = 3;
    public static final int PRINTER_WORK_STATE_CONNECTED = 4;
}
