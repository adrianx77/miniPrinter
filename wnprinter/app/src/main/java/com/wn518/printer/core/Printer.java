package com.wn518.printer.core;

import com.wn518.receipt.LayoutBuilder;

public interface Printer {
	boolean init();
	PrintTask createTask(String title);
	boolean postTask(PrintTask task);
	PrintTask getCurrentTask();
	boolean cancelTask(PrintTask task);
	void clearAllTasks();
	boolean stop();
	
	boolean SendPrintCommand(byte[] cmdCode);
	boolean PrintText(String content);
	boolean PrintImageBytes(byte[] imgBytes);
	LayoutBuilder getLayoutBuilder();
}
