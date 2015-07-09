package com.wn518.printer.core.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.wn518.printer.core.PrinterConst;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class BTService4Printer {
	private BluetoothAdapter adapter;
	private int btState;
	private ConnectThread mConnectThread;
	private IoThread mIoThread;
	private BluetoothDevice mConnectedDevice = null;

	private Handler wHandler;

	private boolean isFull = false;
	public synchronized boolean  getIsFull()
	{
		return isFull;
	}
	private synchronized void putIsFull(boolean _isFull)
	{
		isFull = _isFull;
	}

	public BTService4Printer(Handler handler) {
		this.wHandler = handler;
		btState = PrinterConst.BLUETOOTH_STATE_NONE;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	public boolean HasDevice() {
		return adapter != null;
	}

	public boolean IsOpen() {
		synchronized (this) {
			return HasDevice() && adapter.isEnabled();
		}
	}

	private boolean checkPrinter(BluetoothDevice device)
	{
		if(device==null)
			return false;
		BluetoothClass btcls = device.getBluetoothClass();

		if(btcls==null)
			return false;
		
		return PrinterConst.BTPrinterClasses.equals(btcls.toString());
	}
	public boolean checkBoundPrinter()
	{
		int PrinterCount = 0;
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		for(BluetoothDevice device:devices)
		{
			if(checkPrinter(device))
				PrinterCount++;
		}
		return PrinterCount==1;
	}

	public BluetoothDevice getBoundPrinter()
	{
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		for(BluetoothDevice device:devices)
		{
			if(checkPrinter(device))
				return device;
		}
		return null;
	}
	
	// 获取已经配对的蓝牙设备的物理地址
	public Set<BluetoothDevice> GetBondedDevices() {
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		HashSet<BluetoothDevice> outDevices = new HashSet<BluetoothDevice>();
		for(BluetoothDevice dev:devices)
		{
			if(checkPrinter(dev))
				outDevices.add(dev);
		}
		return outDevices;
	}

	public boolean ConnectToPrinter(){
		mConnectedDevice = null;
		Set<BluetoothDevice> devices = GetBondedDevices();
		for (BluetoothDevice device : devices) {
            if(!checkPrinter(device))
                continue;
			connect(device);
			setState(PrinterConst.BLUETOOTH_STATE_CONNECTING);
			mConnectedDevice = device;
			return true;
		}  
		return false;
	}

	public void write(byte[] out) {
		// Create temporary object
		IoThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (btState != PrinterConst.BLUETOOTH_STATE_CONNECTED)
				return;
			r = mIoThread;
		}
		if (r != null) {
			r.write(out);
		} else {
			DisConnected();
		}
	}

	public synchronized void setState(int state) {
		btState = state;
	}

	public synchronized int getState() {
		return btState;
	}

	private void Stop()
	{
		ConnectThread tmp = null;
		synchronized (this)
		{
			if (mConnectThread != null) {
				tmp = mConnectThread;
				mConnectThread = null;
			}
		}
		if(tmp!=null)
		{
			tmp.cancel();
		}
		IoThread tmp2 = null;
		synchronized (this)
		{
			if (mIoThread != null) {
				tmp2 = mIoThread;
				mIoThread = null;
			}
		}

		if(tmp2!=null)
		{
			tmp2.cancel();
		}

	}
	public  void connect(BluetoothDevice device) {

		if (getState() == PrinterConst.BLUETOOTH_STATE_CONNECTING)
		{
			Stop();
		}

		synchronized(this)
		{
			mConnectThread = new ConnectThread(device);
			mConnectThread.start();
		}
	}

	public void DisConnected() {
		if (getState() == PrinterConst.BLUETOOTH_STATE_CONNECTED) {

			Stop();
			
			setState(PrinterConst.BLUETOOTH_STATE_NONE);
		}
	}

	public  void connected(BluetoothSocket socket,
			BluetoothDevice device) {

		Stop();

		synchronized(this)
		{
			mIoThread = new IoThread(socket,device.getAddress());
			mIoThread.start();
		}
		
		setState(PrinterConst.BLUETOOTH_STATE_CONNECTED);

		if(wHandler!=null)
			wHandler.obtainMessage(PrinterConst.MSG_SVC_CONNECTED,device.getAddress()).sendToTarget();
	}

	private void connectionFailed(BluetoothDevice device) {
		if(wHandler!=null)
			wHandler.obtainMessage(PrinterConst.MSG_SVC_CONNECT_FAILED,device.getAddress()).sendToTarget();
		setState(PrinterConst.BLUETOOTH_STATE_NONE);
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost(String addr) {
		if(wHandler!=null)
			wHandler.obtainMessage(PrinterConst.MSG_SVC_CONNECT_LOST,addr).sendToTarget();
		setState(PrinterConst.BLUETOOTH_STATE_NONE);
	}

	public BluetoothDevice getConnectedDevice() {
		return mConnectedDevice;
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
                try {
                    tmp = device.createRfcommSocketToServiceRecord(PrinterConst.SPP_SERVICE_UUID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            mmSocket = tmp;
		}

		@Override
		public void run() {
			setName("ConnectThread");

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed(mmDevice);
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
				}
				// Start the service over to restart listening mode
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BTService4Printer.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class IoThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final String devAddress;
		public IoThread(BluetoothSocket socket,String addr) {
			devAddress = addr;
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;
			setName("IoThread!!!!");
			// Keep listening to the InputStream while connected
			while (true) {
				
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					if (bytes <= 0) {
						return;
					}
					if (buffer[0] == 0x13) {
						putIsFull(true);
					} 
					else if (buffer[0] == 0x11) {
						putIsFull(false);
					} 
					else {

						// Send the obtained bytes to the UI Activity
						if(wHandler!=null) {
							byte [] outBuf = new byte[bytes];
							System.arraycopy(buffer,0,outBuf,0,bytes);
							wHandler.obtainMessage(PrinterConst.MSG_SVC_DEVICE_READ,outBuf).sendToTarget();
						}
					}
				} catch (IOException e) {
					connectionLost(devAddress);
					break;
				}
			}
		}

		public void write(byte[] buffer) 
		{
			try 
			{
				mmOutStream.write(buffer);
				// Share the sent message back to the UI Activity
				if(wHandler!=null)
					wHandler.obtainMessage(PrinterConst.MSG_SVC_DEVICE_WRITE, buffer).sendToTarget();
			} 
			catch (IOException e) 
			{

			}
		}

		public void cancel() {
			try {
				mmSocket.close();
				setState(PrinterConst.BLUETOOTH_STATE_NONE);
			} catch (IOException e) {
			}
		}
	}
}
