package com.github.s262316.forx.css;

public class BadDeclarationException extends RuntimeException
{
	public BadDeclarationException(String message)
	{
		super(message);
	}

	public BadDeclarationException(Throwable cause)
	{
		super(cause);
	}
}
