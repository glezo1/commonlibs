package pruebasa;

import java.util.ArrayList;
import java.util.Collections;



public class TransitiveCongruenceClassCalculator 
{
	//-----------------------------------------------------------------------------------
	public static <T extends Comparable<T>> ArrayList<ArrayList<T>>		calculate_congruence_classes(ArrayList<ArrayList<T>> input)
	{
		ArrayList<ArrayList<T>> result=new ArrayList<ArrayList<T>>();
		return TransitiveCongruenceClassCalculator.calculate_congruence_classes(input,result);
	}
	//-----------------------------------------------------------------------------------
	private static <T extends Comparable<T>> ArrayList<ArrayList<T>>	calculate_congruence_classes(ArrayList<ArrayList<T>> input,ArrayList<ArrayList<T>> output)
	{
		boolean has_been_idle=true;
		ArrayList<T> first_element=input.get(0);
		output.add(first_element);
		input.remove(0);
		
		
		for(int i=0;i<input.size();i++)
		{
 			ArrayList<T> current_tokenized=input.get(i);
			boolean none_exists=true;
			boolean finished=false;
 			for(int j=0;j<current_tokenized.size();j++)
			{
 				T current_atomic_element=current_tokenized.get(j);
				for(int k=0;k<output.size();k++)
				{
					if(output.get(k).contains(current_atomic_element))
					{
						has_been_idle=false;
						ArrayList<T> modified_congruence_class=output.get(k);
						for(int l=0;l<current_tokenized.size();l++)
						{
							if(!modified_congruence_class.contains(current_tokenized.get(l)))
							{
								modified_congruence_class.add(current_tokenized.get(l));
							}
						}
						Collections.sort(modified_congruence_class);
						output.set(k,modified_congruence_class);
						none_exists=false;
						finished=true;
						break;
					}
				}
				if(finished)
				{
					break;
				}
			}
			if(none_exists)
			{
				output.add(current_tokenized);
			}
		}		
		
		if(has_been_idle)
		{
			return output;
		}
		else
		{
			return TransitiveCongruenceClassCalculator.calculate_congruence_classes(output);
		}
	}	
	//-----------------------------------------------------------------------------------
}
