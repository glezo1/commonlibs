package com.glezo.cve;

public class UnparseableCVEException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public UnparseableCVEException(String message)
	{
		super(message);
	}
}
