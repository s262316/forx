package com.github.s262316.forx.css;

public class BadValueException extends RuntimeException
{
	public BadValueException(Throwable cause) 
	{
		super(cause);
	}

	public BadValueException(String message)
	{
		super(message);
	}
}
