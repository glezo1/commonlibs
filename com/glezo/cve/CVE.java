package cve;

public class CVE 
{
	private String	raw;
	private int		year;
	private int		code;
	
	
	//---------------------------------------------------------
	public CVE(String s) throws UnparseableCVEException
	{
		s=s.trim();
		this.raw=s;
		String tokens[]=s.split("-");
		if(tokens.length!=3)
		{
			throw new UnparseableCVEException("Format must be CVE-year-code");
		}
		if(!tokens[0].equals("CVE"))	
		{	
			throw new UnparseableCVEException("Format must be CVE-year-code");	
		}
		try
		{
			this.year=Integer.parseInt(tokens[1]);
			this.code=Integer.parseInt(tokens[2]);
		}
		catch(NumberFormatException e)
		{
			throw new UnparseableCVEException("Format must be CVE-year-code");	
		}
	}
	//---------------------------------------------------------
	public int		getYear()	{	return this.year;	}
	public int		getCode()	{	return this.code;	}
	public String	toString()	{	return this.raw;	}
	//---------------------------------------------------------
	public static boolean isValidCVE(String s)
	{
		try
		{
			new CVE(s);
			return true;
		}
		catch(UnparseableCVEException e)
		{
			return false;
		}
	}
	//---------------------------------------------------------
}
