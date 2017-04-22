package com.glezo.tuples;

public class N3Tuple<A,B,C> 
{
	private final A	a;
	private final B b;
	private final C c;
	
	public N3Tuple(A a,B b,C c)
	{
		this.a=a;
		this.b=b;
		this.c=c;
	}
	public A getA()	{	return this.a;	}
	public B getB()	{	return this.b;	}
	public C getC()	{	return this.c;	}
	
	public boolean	equals(Object o)
	{
		if(!(o instanceof N3Tuple))
		{
			return false;
		}
		@SuppressWarnings("unchecked")
		N3Tuple<A,B,C> oo=(N3Tuple<A,B,C>)o;
		A oo_a=(A)oo.getA();
		B oo_b=(B)oo.getB();
		C oo_c=(C)oo.getC();
		return	(	(this.a==null && oo_a==null) || (this.a!=null && oo_a!=null && this.a.equals(oo_a))	)
				&&
				(	(this.b==null && oo_b==null) || (this.b!=null && oo_b!=null && this.b.equals(oo_b))	)
				&&
				(	(this.c==null && oo_c==null) || (this.c!=null && oo_c!=null && this.c.equals(oo_c))	);
	}
	public int		hashCode()	{	return this.a.hashCode() ^ this.b.hashCode() ^ this.c.hashCode();		}
	public String	toString()	{	return this.a.toString()+" "+this.b.toString()+" "+this.c.toString();	}

}
