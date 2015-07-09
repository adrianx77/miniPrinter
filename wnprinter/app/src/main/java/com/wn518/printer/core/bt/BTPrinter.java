package com.wn518.printer.core.bt;

import com.wn518.printer.core.PrinterConst;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;

import com.wn518.printer.core.PrintService;
import com.wn518.printer.core.PrintTask;
import com.wn518.printer.core.Printer;
import com.wn518.receipt.LayoutBuilder;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class BTPrinter extends PrintService implements Printer {
//	private static String CFG_KEY_PAPER_50MM = "printer_paper_50mm";

	public Context outContext = null;
	//外部输入的Handler
	Handler outHandler = null;

	//work thread
	private HandlerThread wThread = null;
	private Handler wHandler = null;

	//检测timer
	Timer mTimer = null;
	boolean mTimerRunning = false;

	//当前打印机的状态
	private int workState = PrinterConst.PRINTER_WORK_STATE_UNKNOWN;
	//蓝牙服务
	BTService4Printer service = null;
	//任务队列
	private LinkedList<PrintTask> printerTaskes;
	//当然任务
	private PrintTask currentTask = null;
	public BTPrinter(Context context,Handler outHandler)
	{
		outContext = context;
		this.outHandler = outHandler;
		printerTaskes = new LinkedList<PrintTask> ();
		buildWorkThread();
	}

	//初始化
	@Override
	public boolean init() {
		sendMsgToSelf(PrinterConst.MSG_CMD_INITIALIZE);
		return true;
	}
	
	//发送打印任务至打印机
	@Override
	public boolean postTask(PrintTask task) {
		if(task==null)
			return false;
		
		synchronized(this)
		{
			printerTaskes.push(task);
		}
		postPrintMessage(0);
		return true;
	}

	//获得当前打印任务
	@Override
	public synchronized PrintTask getCurrentTask() {
		return currentTask;
	}

	//取消当前打印任务
	@Override
	public synchronized boolean cancelTask(PrintTask task) {
		if(printerTaskes.contains(task))
		{
			printerTaskes.remove(task);
			return true;
		}
		return false;
	}

	//清空打印队列所有任务
	@Override
	public synchronized void clearAllTasks() {
		printerTaskes.clear();
	}

	@Override
	public boolean stop() {
		sendMsgToSelf(PrinterConst.MSG_CMD_STOP);
		return false;
	}
	
	private synchronized void setWorkState(int ws)
	{
		workState = ws;
	}
	private synchronized int getWorkState()
	{
		return workState;
	}
	
	private void buildWorkThread()
	{
		wThread = new HandlerThread("Blue Tooth Printer Work Thread!");
		wThread.start();
		
		wHandler = new Handler(wThread.getLooper(),new Callback()
		{
			@Override
			public boolean handleMessage(Message msg) {

				switch(msg.what)
				{
				case PrinterConst.MSG_CMD_TIMER:
					OnTimer();
                    break;
				case PrinterConst.MSG_CMD_INITIALIZE:
					if(!process_init())
						startTimer();
					break;
				case PrinterConst.MSG_CMD_RECONNNECT:
					if(getWorkState()==PrinterConst.PRINTER_WORK_STATE_UNKNOWN)
						break;
					setWorkState(PrinterConst.PRINTER_WORK_STATE_INITIALIZED);
					if(process_connect())
						stopTimer();
					break;

				case PrinterConst.MSG_CMD_PRINT_TASK:
					process_print();
					break;
				case PrinterConst.MSG_CMD_STOP:
					if(getWorkState()==PrinterConst.PRINTER_WORK_STATE_UNKNOWN)
						break;
					setWorkState(PrinterConst.PRINTER_WORK_STATE_UNKNOWN);
					BTService4Printer tmp = service;
					service = null;
					tmp.DisConnected();
					break;
					
				case PrinterConst.MSG_SVC_CONNECT_LOST:
					if(getWorkState()==PrinterConst.PRINTER_WORK_STATE_UNKNOWN)
						break;
					setWorkState(PrinterConst.PRINTER_WORK_STATE_INITIALIZED);
					sendMsgToOuter(PrinterConst.MSG_CONNECT_DISCONNECTED,msg.obj);
					startTimer();
					break;
				case PrinterConst.MSG_SVC_CONNECT_FAILED:
					if(getWorkState()==PrinterConst.PRINTER_WORK_STATE_UNKNOWN)
						break;
					setWorkState(PrinterConst.PRINTER_WORK_STATE_INITIALIZED);
					sendMsgToOuter(PrinterConst.MSG_CONNECT_FAILED,msg.obj);
					startTimer();
					break;

				case PrinterConst.MSG_SVC_CONNECTED:
					if(getWorkState()==PrinterConst.PRINTER_WORK_STATE_UNKNOWN)
						break;
					
					stopTimer();
					
					sendMsgToOuter(PrinterConst.MSG_CONNECT_SUCCESSED,msg.obj);
					
					mPrinterInfo.initializePrinter(BTPrinter.this);
					
					if(!mPrinterInfo.getanDeviceInfo())
					{
						setWorkState(PrinterConst.PRINTER_WORK_STATE_CONNECTED);
						postPrintMessage(0);
					}
					
					break;
					//send to outer	
				case PrinterConst.MSG_SVC_DEVICE_READ:
					byte [] outBuf = (byte[])msg.obj;
					 String readMessage = new String(outBuf);
					 if(readMessage.contains("800"))//80mm paper
		                {
						 	mPrinterInfo.setPaperWidth(72);
		                }
					 else if(readMessage.contains("580"))//58mm paper
		                {
						 	mPrinterInfo.setPaperWidth(48);
		                }
					 
					if(service.getIsFull())
						sendMsgToOuter(PrinterConst.MSG_BUFFER_FULL);
					
					setWorkState(PrinterConst.PRINTER_WORK_STATE_CONNECTED);
					
					sendMsgToOuter(msg.what,msg.obj);
					break;
				case PrinterConst.MSG_SVC_DEVICE_WRITE:
					sendMsgToOuter(msg.what,msg.obj);
					break;
				}
				return true;
			}
		});
		
	}

	// internal process procedure
	private boolean process_init()
	{
		synchronized(this)
		{
			if(PrinterConst.PRINTER_WORK_STATE_CONNECTED  == workState
					|| PrinterConst.PRINTER_WORK_STATE_CONNECTING ==workState 
					|| PrinterConst.PRINTER_WORK_STATE_INITING == workState
					|| PrinterConst.PRINTER_WORK_STATE_INITIALIZED ==workState)
				return true;
			
			workState = PrinterConst.PRINTER_WORK_STATE_INITING;
		}
		
		service = new BTService4Printer(wHandler);

		setWorkState(PrinterConst.PRINTER_WORK_STATE_INITIALIZED);
		
		return process_connect();
	}
	private boolean process_connect()
	{
		if(PrinterConst.PRINTER_WORK_STATE_INITIALIZED!=getWorkState())
			return false;
		
		
		if(!service.IsOpen())
		{
			return false;
		}
		
		if(!service.checkBoundPrinter())
			return false;

		BluetoothDevice device = service.getBoundPrinter();
		String addr = device!=null?device.getAddress():"";
		if(!service.ConnectToPrinter())
		{
			sendMsgToOuter(PrinterConst.MSG_CONNECT_FAILED,addr);
			return false;
		}
		else
			setWorkState(PrinterConst.PRINTER_WORK_STATE_CONNECTING);
		sendMsgToOuter(PrinterConst.MSG_CONNECTING,addr);
		afterConnect();
		return true;		
	}

	private void process_print() {
		if(getWorkState()!=PrinterConst.PRINTER_WORK_STATE_CONNECTED)
			return;
		
		PrintTask task = pop_Task();

		if(task!=null)
		{
			synchronized(this)
			{
				currentTask = task;
			}
			
			sendMsgToOuter(PrinterConst.MSG_TASK_PRINTING,task.getTitile());
			
			print_task(task);
			
			sendMsgToOuter(PrinterConst.MSG_TASK_PRINTED,task.getTitile());

			synchronized(this)
			{
				currentTask = null;
			}
		}
		postPrintMessage(3500);
	}
	
	private void print_task(PrintTask task)
	{
		task.acceptPrint(mPrinterInfo, this);
	}
	private synchronized PrintTask pop_Task()
	{
		if(printerTaskes.size()!=0)
			return printerTaskes.pop();
		return null;
	}
	private synchronized boolean hasPrintTask()
	{
		return printerTaskes.size()!=0;
	}
	
	private void postPrintMessage(int time)
	{
		if(getWorkState() ==PrinterConst.PRINTER_WORK_STATE_CONNECTED && hasPrintTask())
		{
			if(time!=0)
			{
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sendMsgToSelf(PrinterConst.MSG_CMD_PRINT_TASK);
		}
	}
	@Override
	public PrintTask createTask(String title) {
		return new BTPrintTask(title);
	}

	@Override
	public boolean SendPrintCommand(byte[] cmdCode) {
		return writeBytes(mPrinterInfo.getMaxWriteBytes(),cmdCode);
	}

	@Override
	public boolean PrintText(String content) {

		byte[] buffer = getText(content);
		return writeBytes(mPrinterInfo.getMaxWriteChars(),buffer);
	}

	private boolean writeBytes(int maxLen,byte[]bytes)
	{
		if (bytes.length <= maxLen) {
			service.write(bytes);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
	
		int sendSize =  maxLen;
		for (int j = 0; j < bytes.length; j += sendSize) 
		{
			byte[] btPackage = null;
			
			if (bytes.length - j < sendSize) 
			{
				btPackage = new byte[bytes.length - j];
			}
			else
			{
				btPackage = new byte[sendSize];
			}
			System.arraycopy(bytes, j, btPackage, 0, btPackage.length);
			service.write(btPackage);
		}
		return true;
	}
	public boolean PrintImageBytes(byte [] imgBytes)
	{
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		service.write(imgBytes);
		service.write(new byte[]{0x0a});
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	void waitForIdle(int maxTimes)
	{
		if (service.getIsFull()) {
			System.out.printf("Printer is full!!!!\n");
			int index = 0;
			while (index++ < maxTimes) {
				if (!service.getIsFull()) {
					break;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public LayoutBuilder getLayoutBuilder() {
		return mPrinterInfo.getLayoutBuilder();
	}


	private void afterConnect() 
	{
		BluetoothDevice device = service.getConnectedDevice();
		String devName = device.getName();
		boolean is50mm = true;
		if(devName.compareToIgnoreCase("BMV2")==0)
		{
			mPrinterInfo = new ZKCPrinterInfo(outContext);
		}
		else if(devName.compareToIgnoreCase("HMSoft")==0)
		{
			mPrinterInfo = new EpsonPrinterInfo(true,true,false,false,outContext);
            is50mm = false;
		}
		else if(devName.compareToIgnoreCase("Winpos")==0)
		{
			mPrinterInfo = new EpsonPrinterInfo(true,true,false,false,outContext);
		}
		else if(devName.compareToIgnoreCase("WP-T630")==0)
		{
			mPrinterInfo = new EpsonPrinterInfo(false,false,false,false,outContext);
		}
		else
			mPrinterInfo = new EpsonPrinterInfo(false,false,false,false,outContext);
			
		if(!is50mm)
			mPrinterInfo.setPaperWidth(72);
		else
			mPrinterInfo.setPaperWidth(48);
	}
	void sendMsgToSelf(int msg)
	{
		wHandler.obtainMessage(msg).sendToTarget();
	}
	void sendMsgToOuter(int msg)
	{
		if(outHandler!=null)
			outHandler.obtainMessage(msg).sendToTarget();
	}
	void sendMsgToOuter(int msg,Object obj)
	{
		if(outHandler!=null)
			outHandler.obtainMessage(msg,obj).sendToTarget();
	}

	synchronized void startTimer()
	{
		if(mTimerRunning)
			return;
		
		mTimer = new Timer();
		
		mTimer.schedule(new TimerTask(){
			public void run() {
				sendMsgToSelf(PrinterConst.MSG_CMD_TIMER);
			}
		}, 5000, 5000);
		mTimerRunning = true;
	}
	synchronized void stopTimer()
	{
		if(mTimerRunning)
			mTimer.cancel();
		mTimerRunning = false;
	}
	
	void OnTimer()
	{
		if(!mTimerRunning)
			return;
		
		if(service!=null)
			if(service.IsOpen())
				sendMsgToSelf(PrinterConst.MSG_CMD_RECONNNECT);
	}
}
