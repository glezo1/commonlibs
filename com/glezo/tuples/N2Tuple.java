package com.glezo.tuples;

public class N2Tuple<A,B> 
{
	private final A	a;
	private final B b;
	
	public N2Tuple(A a,B b)
	{
		this.a=a;
		this.b=b;
	}
	public A getA()	{	return this.a;	}
	public B getB()	{	return this.b;	}
	
	public boolean	equals(Object o)
	{
		if(!(o instanceof N2Tuple<?,?>))
		{
			return false;
		}
		@SuppressWarnings("unchecked")
		N2Tuple<A,B> oo=(N2Tuple<A,B>)o;
		A oo_a=(A)oo.getA();
		B oo_b=(B)oo.getB();
		return	(	(this.a==null && oo_a==null) || (this.a!=null && oo_a!=null && this.a.equals(oo_a))	)
				&&
				(	(this.b==null && oo_b==null) || (this.b!=null && oo_b!=null && this.b.equals(oo_b))	);
	}
	public int		hashCode()	{	return this.a.hashCode() ^ this.b.hashCode();	}
	public String	toString()	{	return this.a.toString()+" "+this.b.toString();	}
}
