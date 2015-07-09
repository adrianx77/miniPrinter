package com.wn518.receipt;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LineLayout {
	private ArrayList<CellLayout> layouts = new ArrayList<CellLayout>();
	private int mWidth;
	public enum LAYOUT_ALIGN{LA_LEFT,LA_CENTER,LA_RIGHT}

	public class CellLayout
	{
		int width;
		LAYOUT_ALIGN la;
	}
	
	public LineLayout(int width)
	{
		mWidth = width;
	}
	public boolean addCell(int width,LAYOUT_ALIGN fa)
	{
		int w = 0;

		for(int i=0;i<layouts.size();i++)
			w += layouts.get(i).width;
		
		if(w+width>mWidth)
			return false;

		CellLayout layout = new CellLayout();
		layout.width = width;
		layout.la = fa;
				
		layouts.add(layout);
		return true;
	}

	public static byte [] getStringBytes(String str)
	{
		try {
			return str.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			return str.getBytes();
		}
	}
	public static String getString(byte [] bytes)
	{
		try {
			return new String(bytes,"GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			bytes[bytes.length-1] = ' ';
			try {
				return new String(bytes,"GBK");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				return "";
			}
		}
	}
	public static String layout(CellLayout cl,String str)
	{
		byte [] outByte = new byte[cl.width];
		for(int i=0;i<cl.width;i++)
		{
			outByte[i] = ' ';
		}
		
		byte [] stByte = getStringBytes(str);
		
		if(stByte.length>=cl.width)
		{
			System.arraycopy(stByte, 0, outByte, 0, cl.width);
		}
		else
		{
			if(cl.la==LAYOUT_ALIGN.LA_LEFT)
			{
				System.arraycopy(stByte, 0, outByte, 0, stByte.length);
			}
			else if(cl.la==LAYOUT_ALIGN.LA_RIGHT)
			{
				int fillWidth = cl.width -stByte.length;
				System.arraycopy(stByte, 0, outByte, fillWidth, stByte.length);
			}
			else
			{
				int fw2 = cl.width -stByte.length;
				System.arraycopy(stByte, 0, outByte, fw2, stByte.length);
			}
			
		}
		return getString(outByte);
	}
	
	public String layout(String ...args)
	{
		if(args.length==0)
			return "";
			
		CellLayout cl;
		if(layouts.size()==0)
		{
			cl = new CellLayout();
			cl.width = mWidth;
			cl.la = LAYOUT_ALIGN.LA_LEFT;
			return layout(cl,args[0]);
		}
		
		int width = 0;
		String outstr="";
		int i=0;
		for(;i<layouts.size() && i< args.length;i++)
		{
			cl=layouts.get(i);
			 outstr += layout(cl,args[i]);
			 width += cl.width;
		}
		 if(args.length>layouts.size())
		 {
			 if(mWidth - width==0)
					return outstr;
			 cl = new CellLayout();
			 cl.width = mWidth - width;
			 cl.la = LAYOUT_ALIGN.LA_LEFT;
			 return outstr+layout(cl,args[i]);
		 }
		
		return outstr;
	}
	
	//for printer
}
