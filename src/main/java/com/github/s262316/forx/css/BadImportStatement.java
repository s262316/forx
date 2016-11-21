package com.github.s262316.forx.css;

public class BadImportStatement extends Exception
{
	public BadImportStatement(Exception e)
	{
		super(e);
	}

	public BadImportStatement(String msg)
	{
		super(msg);
	}
}
