package com.glezo.htmlParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import com.glezo.generalizedList.GeneralizedList;
import com.glezo.generalizedList.GeneralizedListNode;

public class GhettoHTMLParser 
{
	//bloody ghetto, indeed.
	
	private String						text_to_tokenize;
	private ArrayList<String>			list_of_tokens_as_string;
	private ArrayList<HtmlToken>		list_of_tokens;
	private GeneralizedList<HtmlToken>	list_tree_of_tokens;
	private ArrayList<Exception>		list_of_parsing_warnings;
	private int							current_token_number;
	
	//--------------------------------------------------------------------
	public GhettoHTMLParser(String text_to_tokenize)
	{
		this.text_to_tokenize			=text_to_tokenize;
		this.list_of_parsing_warnings	=new ArrayList<Exception>();
		this.list_of_tokens_as_string	=new ArrayList<String>();
		boolean reading_a_token=false;
		boolean reading_a_script=false;	//as long as it's true, we'll add every character to a single htmltoken, representing the script itself
		String current_token="";
		for(int i=0;this.text_to_tokenize!=null && i<this.text_to_tokenize.length();i++)
		{
			char current_char=this.text_to_tokenize.charAt(i);
			if(current_char==' ' || current_char=='\t')
			{
				if(reading_a_token)		{	current_token+=current_char;	}
				else					{	/*nothing at all*/				}
			}
			else if(current_char=='<')
			{
				//begin of token?
				if(reading_a_token==false)
				{
					if(!current_token.trim().isEmpty() && !current_token.trim().equals("\n"))
					{
						this.list_of_tokens_as_string.add(current_token.trim());
					}
					reading_a_token=true;
					current_token="<";
				}
				else if(reading_a_script==true)
				{
					current_token+=current_char;
				}
				else
				{
					//damned... could be <div> this is my token with special chars as < but still goes on</div>
					//could not know:
					if(!current_token.trim().isEmpty())
					{
						String aux_string="";
						for(int j=0;j<current_token.trim().length();j++)
						{
							if(!Integer.toHexString((int)current_token.trim().charAt(j)).equals("a0")) //non breaking space, a0. Translation of &nbsp;
							{
								aux_string+=current_token.trim().charAt(j);
							}
						}
						if(!aux_string.isEmpty())
						{
							this.list_of_tokens_as_string.add(current_token.trim());
						}
					}
					reading_a_token=true;
					current_token="<";
					
					//current_token+=current_char;
				}
			}
			else if(current_char=='>')
			{
				//end of token?
				if(reading_a_token==true && current_token.startsWith("<"))
				{
					current_token+=current_char;
					this.list_of_tokens_as_string.add(current_token.trim());
					try 
					{
						HtmlToken syntatic_aux_token=new HtmlToken(current_token.trim(),0);
						if(syntatic_aux_token.get_type()!=null && syntatic_aux_token.get_type().equals("script") && syntatic_aux_token.is_opening())
						{
							reading_a_script=true;
						}
						else if(reading_a_script && syntatic_aux_token.get_type()!=null && syntatic_aux_token.get_type().equals("script") && syntatic_aux_token.is_closing())
						{
							reading_a_script=false;
						}
					} 
					catch (UnparseableHtmlTokenException e) 
					{
					}
					current_token="";
					reading_a_token=false;
				}
				else
				{
					current_token+=current_char;
					if(reading_a_script && current_token.endsWith("</script>"))	//GHETTO!
					{
						String aux="</script>";
						current_token=current_token.substring(0,current_token.length()-aux.length());
						this.list_of_tokens_as_string.add(current_token.trim());
						this.list_of_tokens_as_string.add(aux);
						reading_a_script=false;
					}
				}
			}
			else
			{
				if(reading_a_token==false)	//<div>THIS_IS_INDEED_A_TOKEN_WITH_>_<_AND_'</div>
				{
					reading_a_token=true;
				}
				current_token+=current_char;
			}
		}
		
		try 
		{
			this.parse();
		} 
		catch (UnparseableHtmlTokenException | UnparseableHtmlException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//--------------------------------------------------------------------
	public boolean 					hasNext()
	{
		return this.current_token_number!=this.list_of_tokens_as_string.size();
	}
	//--------------------------------------------------------------------
	public String					getNext()
	{
		if(this.hasNext())
		{
			String current_token=this.list_of_tokens_as_string.get(this.current_token_number);
			this.current_token_number++;
			return current_token;
		}
		else
		{
			return null;
		}
	}
	//--------------------------------------------------------------------
	public ArrayList<String>		getListOfTokensAsString()
	{
		return this.list_of_tokens_as_string;
	}
	//--------------------------------------------------------------------
	public ArrayList<HtmlToken>		getListOfTokens()
	{
		return this.list_of_tokens;
	}
	//--------------------------------------------------------------------
	private void					parse() throws UnparseableHtmlTokenException, UnparseableHtmlException
	{
		//syntactical-ish analysis
		this.list_of_tokens=new ArrayList<HtmlToken>();
		for(int i=0;i<this.list_of_tokens_as_string.size();i++)
		{
			String current_token_string=this.list_of_tokens_as_string.get(i);
			try 
			{
				this.list_of_tokens.add(new HtmlToken(current_token_string,i));
			} 
			catch (UnparseableHtmlTokenException e) 
			{
				throw e;
			}
		}
		//semantical-ish analysis. 1:closing position
		for(int i=0;i<this.list_of_tokens.size();i++)
		{
			this.list_of_tokens.get(i).set_closing_token(this.list_of_tokens);
		}		

		//semantical-ish analysis. 2:comments and scripts
		for(int i=0;i<this.list_of_tokens.size();i++)
		{
			this.list_of_tokens.get(i).set_is_comment_and_script(this.list_of_tokens);
		}		
		
		//semantical-ish analysis. 3:generalized list
		GeneralizedList<HtmlToken>					g_list			=new GeneralizedList<HtmlToken>();
		Stack<GeneralizedListNode<HtmlToken>>		father_stack	=new Stack<GeneralizedListNode<HtmlToken>>();
		GeneralizedListNode<HtmlToken>				pointer_to_last	=null;
		for(int i=0;i<this.list_of_tokens.size();i++)
		{
			HtmlToken t=this.list_of_tokens.get(i);
			if(!t.is_comment())
			{
				if(t.is_opening())
				{
					boolean autoclosing=(t.get_token_number()==t.get_closing_token_position()) || t.get_closing_profile()==0;
					if(autoclosing)
					{
						GeneralizedListNode<HtmlToken> f=null;
						if(!father_stack.isEmpty())
						{
							f=father_stack.peek();
						}
						GeneralizedListNode<HtmlToken> n=new GeneralizedListNode<HtmlToken>(false,t,null,f);
						if(pointer_to_last==null)
						{
							g_list.set_root_node(n);
							pointer_to_last=n;
						}
						else
						{
							if(pointer_to_last.is_list())
							{
								if(pointer_to_last.get_son()==null)
								{
									pointer_to_last.set_son(n);
									pointer_to_last=n;
								}
								else
								{
									pointer_to_last.set_next_brother(n);
									pointer_to_last=n;
								}
							}
							else
							{
								pointer_to_last.set_next_brother(n);
								pointer_to_last=n;
							}
						}
					}
					else														//opening, non-autoclosing
					{
						GeneralizedListNode<HtmlToken> f=null;
						if(!father_stack.isEmpty())
						{
							f=father_stack.peek();
						}
						GeneralizedListNode<HtmlToken> n=new GeneralizedListNode<HtmlToken>(true,t,null,f);
						father_stack.push(n);
						if(pointer_to_last==null)
						{
							g_list.set_root_node(n);
							pointer_to_last=n;
						}
						else
						{
							if(pointer_to_last.is_list())
							{
								if(pointer_to_last.get_son()==null)
								{
									pointer_to_last.set_son(n);
									pointer_to_last=n;
								}
								else
								{
									pointer_to_last.set_next_brother(n);
									pointer_to_last=n;
								}
							}
							else
							{
								pointer_to_last.set_next_brother(n);
								pointer_to_last=n;
							}
						}
						
					}
				}
				else if(t.is_closing())											//closing
				{
					GeneralizedListNode<HtmlToken> f=null;
					if(father_stack.isEmpty())
					{
						//well... since this shit ACTUALLY happens, and any browser deals with it, so will we.
						//throw new UnparseableHtmlException("SYNTACTIC ERROR: token "+t+" closes nothing at all.");
						this.list_of_parsing_warnings.add(new UnparseableHtmlException("SYNTACTIC ERROR: token "+t+" closes nothing at all."));
					}
					else
					{
						f=father_stack.peek();
						if(!t.get_type().equals(f.get_data().get_type()))
						{
							//well... since this shit ACTUALLY happens, and any browser deals with it, so will we.
							//throw new UnparseableHtmlException("SYNTACTIC ERROR: token "+t+" closes non related token: "+f);
							this.list_of_parsing_warnings.add(new UnparseableHtmlException("SYNTACTIC ERROR: token "+t+" closes non related token: "+f));
						}
						else
						{
							GeneralizedListNode<HtmlToken> n=new GeneralizedListNode<HtmlToken>(true,t,null,f);
							if(pointer_to_last==f)
							{
								pointer_to_last.set_son(n);
								pointer_to_last=n;
								father_stack.pop();
								pointer_to_last=f;
							}
							else
							{
								pointer_to_last.set_next_brother(n);
								pointer_to_last=n;
								father_stack.pop();
								pointer_to_last=f;
							}
						}
					}
				}
				else
				{
					GeneralizedListNode<HtmlToken> f=null;
					if(!father_stack.isEmpty())
					{
						f=father_stack.peek();
					}
					GeneralizedListNode<HtmlToken> n=new GeneralizedListNode<HtmlToken>(false,t,null,f);
					if(pointer_to_last.is_list())
					{
						if(pointer_to_last.get_son()==null)
						{
							pointer_to_last.set_son(n);
							pointer_to_last=n;
						}
						else
						{
							pointer_to_last.set_next_brother(n);
							pointer_to_last=n;
						}
					}
					else
					{
						pointer_to_last.set_next_brother(n);
						pointer_to_last=n;
					}
				}
			}
				
		}
		if(!father_stack.isEmpty())
		{
			//well... since this shit ACTUALLY happens, and any browser deals with it, so will we.
			ArrayList<HtmlToken> unclosed=new ArrayList<HtmlToken>();
			while(!father_stack.isEmpty())
			{
				unclosed.add(father_stack.pop().get_data());
			}
			Collections.reverse(unclosed);
			for(int i=0;i<unclosed.size();i++)
			{
				this.list_of_parsing_warnings.add(new UnparseableHtmlException("SYNTACTIC ERROR: token(s) "+unclosed.get(i)+" is not closed"));
			}
		}
		
		this.list_tree_of_tokens=g_list;
	}
	//--------------------------------------------------------------------
	
	//small subset of jquery selector
	//operands must be separated by ' '. No extra ' ' allowed this far.
	/*
	public ArrayList<HtmlToken>		jquery_selector(String selector) 
	{
		String selectors_array[]=selector.split("[ >]");
		ArrayList<String> selectors=new ArrayList<String>();
		for(int i=0;i<selectors_array.length;i++)
		{
			if(!selectors_array[i].equals(""))
			{
				selectors.add(selectors_array[i]);
			}
		}
		//TODO! dejar de distinguir i=0 o i!=0
		//at the beginning, every token is a candidate
		ArrayList<HtmlToken> candidates=new ArrayList<HtmlToken>();
		candidates.addAll(this.getListOfTokens());
		String operand=" ";
		for(int i=0;i<selectors.size();i++)
		{
			String current_selector=selectors.get(i);
			operand=" ";
			if(current_selector.equals(">"))
			{
				operand=">";
			}
			else if(current_selector.startsWith("#"))
			{
				String current_selector_id=selectors.get(i).substring(1);
				ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
				if(i==0)
				{
					for(int j=0;j<this.getListOfTokens().size();j++)
					{
						HtmlToken current_token=this.getListOfTokens().get(j);
						String current_token_id=current_token.get_id();
						if(current_token.is_opening() && current_token_id!=null && current_token_id.equals(current_selector_id))
						{
							candidates.add(this.getListOfTokens().get(j));
						}
					}
				}
				else
				{
					while(!candidates.isEmpty())
					{
						HtmlToken current_candidate=candidates.get(0);
						candidates.remove(0);
						if(operand.equals(" "))
						{
							ArrayList<HtmlToken> current_candidate_descendants=this.list_tree_of_tokens.get_descendants(current_candidate);
							for(int k=0;k<current_candidate_descendants.size();k++)
							{
								if(	current_candidate_descendants.get(k).get_id()!=null 
									&& current_candidate_descendants.get(k).get_id().equals(current_selector_id))
								{
									new_candidates.add(current_candidate_descendants.get(k));
								}
							}
						}
						else if(operand.equals(">"))
						{
							ArrayList<HtmlToken> current_candidate_sons=this.list_tree_of_tokens.get_sons(current_candidate);
							for(int k=0;k<current_candidate_sons.size();k++)
							{
								if(	current_candidate_sons.get(k).get_id()!=null 
									&& current_candidate_sons.get(k).get_id().equals(current_selector_id)
									&& current_candidate_sons.get(k).is_opening())
								{
									new_candidates.add(current_candidate_sons.get(k));
								}
							}
						}
					}
					candidates=new_candidates;
				}
			}
			else if(current_selector.startsWith("."))
			{
				String current_selector_class=selectors.get(i).substring(1);
				if(i==0)
				{
					for(int j=0;j<this.getListOfTokens().size();j++)
					{
						HtmlToken current_token=this.getListOfTokens().get(j);
						ArrayList<String> current_token_classes=current_token.get_classes();
						if(current_token.is_opening() && current_token_classes.contains(current_selector_class))
						{
							candidates.add(this.getListOfTokens().get(j));
						}
					}
				}
				else
				{
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					while(!candidates.isEmpty())
					{
						HtmlToken current_candidate=candidates.get(0);
						candidates.remove(0);
						if(operand.equals(" "))
						{
							ArrayList<HtmlToken> current_candidate_descendants=this.list_tree_of_tokens.get_descendants(current_candidate);
							for(int k=0;k<current_candidate_descendants.size();k++)
							{
								if(	current_candidate_descendants.get(k).get_classes()!=null 
									&& current_candidate_descendants.get(k).get_classes().contains(current_selector_class))
								{
									new_candidates.add(current_candidate_descendants.get(k));
								}
							}
						}
						else if(operand.equals(">"))
						{
							ArrayList<HtmlToken> current_candidate_sons=this.list_tree_of_tokens.get_sons(current_candidate);
							for(int k=0;k<current_candidate_sons.size();k++)
							{
								if(	current_candidate_sons.get(k).get_classes()!=null 
									&& current_candidate_sons.get(k).get_classes().contains(current_selector_class)
									&& current_candidate_sons.get(k).is_opening())
								{
									new_candidates.add(current_candidate_sons.get(k));
								}
							}
						}
					}
					candidates=new_candidates;
				}
			}
			else if(current_selector.startsWith("[") && current_selector.endsWith("]"))	//properties: [href],[href="something"]
			{
				ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
				String current_property_and_value=current_selector.substring(1,current_selector.length()-1);
				String current_property=null;
				String current_value=null;
				String tokens[]=current_property_and_value.split("=");
				current_property=tokens[0];
				if(tokens.length>1)
				{
					current_value=tokens[1];
					if(current_value.startsWith("\"") && current_value.endsWith("\"")){current_value=current_value.substring(1,current_value.length()-1);}
				}
				for(int j=0;j<candidates.size();j++)
				{
					HtmlToken current_token=candidates.get(j);
					//System.out.println(current_token);
					int property_index=current_token.get_properties_names().indexOf(current_property);
					if(property_index!=-1)
					{
						//has property. Now, will it much its value? (if any)
						if(current_value!=null)
						{
							if(current_token.get_properties_values().get(property_index).equals(current_value))
							{
								new_candidates.add(current_token);
							}
						}
						else
						{
							new_candidates.add(current_token);
						}
					}
				}
				candidates=new_candidates;
			}
			else
			{
				//type assumed. =D [small subset, told ya]
				if(i==0)
				{
					for(int j=0;j<this.getListOfTokens().size();j++)
					{
						HtmlToken current_token=this.getListOfTokens().get(j);
						String current_token_type=current_token.get_type();
						if(current_token.is_opening() && current_token_type!=null && current_token_type.equals(selectors.get(i)))
						{
							candidates.add(this.getListOfTokens().get(j));
						}
					}
				}
				else
				{
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					while(!candidates.isEmpty())
					{
						HtmlToken current_candidate=candidates.get(0);
						candidates.remove(0);
						if(operand.equals(" "))
						{
							ArrayList<HtmlToken> current_candidate_descendants=this.list_tree_of_tokens.get_descendants(current_candidate);
							for(int k=0;k<current_candidate_descendants.size();k++)
							{
								if(	current_candidate_descendants.get(k).get_type()!=null 
									&& current_candidate_descendants.get(k).get_type().equals(selectors.get(i))
									&& current_candidate_descendants.get(k).is_opening())
								{
									new_candidates.add(current_candidate_descendants.get(k));
								}
							}
						}
						else if(operand.equals(">"))
						{
							ArrayList<HtmlToken> current_candidate_sons=this.list_tree_of_tokens.get_sons(current_candidate);
							for(int k=0;k<current_candidate_sons.size();k++)
							{
								if(	current_candidate_sons.get(k).get_type()!=null 
									&& current_candidate_sons.get(k).get_type().equals(selectors.get(i))
									&& current_candidate_sons.get(k).is_opening())
								{
									new_candidates.add(current_candidate_sons.get(k));
								}
							}
						}
					}
					candidates=new_candidates;
				}
			}
		}
		return candidates;
	}
	*/
	//--------------------------------------------------------------------
	//small subset of jquery selector
	//example: for $('meta[itemprop="name"]').attr('content')
	//			selector		=meta[itemprop="name"]
	//			post_functions	=new String[]{".attr('content')"}
	public ArrayList<HtmlToken>		jquery_selector2(String selector,String[] post_functions) 
	{
		ArrayList<String> selector_tokens	=new ArrayList<String>();
		ArrayList<String> selector_operands	=new ArrayList<String>();
		String current_token=null;
		String operand=null;
		boolean reading_string=false; //to insert blanks into token, instead of splitting
		for(int i=0;i<selector.length();i++)
		{
			char c=selector.charAt(i);
			if(c=='.' || c=='#' || c=='[')
			{
				if(current_token!=null && !current_token.equals(""))
				{
					selector_tokens.add(current_token);
					if(operand==null)
					{
						selector_operands.add("AND");
					}
					else
					{
						selector_operands.add(operand);
					}
					current_token=""+c;
					operand=null;
				}
				else
				{
					if(operand!=null)
					{
						selector_operands.add(operand);
						operand=null;
					}
					current_token=""+c;
				}
			}
			else if(c=='"')
			{
				current_token+=c;
				if(!reading_string)		{	reading_string=true;	}
				else					{	reading_string=false;	}
			}
			else if(c==' ' && !reading_string)
			{
				if(operand==null)
				{
					operand=" ";
					if(current_token!=null && !current_token.equals(""))
					{
						selector_tokens.add(current_token);
						current_token=null;
					}
				}
			}
			else if(c=='>' && !reading_string)
			{
				operand=">";
				if(current_token!=null && !current_token.equals(""))
				{
					selector_tokens.add(current_token);
					current_token=null;
				}
			}
			else if(c=='~' && !reading_string)
			{
				operand="~";
				if(current_token!=null && !current_token.equals(""))
				{
					selector_tokens.add(current_token);
					current_token=null;
				}
			}
			else
			{
				if(current_token==null)
				{
					current_token=""+c;
				}
				else
				{
					current_token+=c;
				}
			}
		}
		if(current_token!=null && !current_token.equals(""))
		{
			selector_tokens.add(current_token);
			current_token=null;
		}
		if(operand!=null)
		{
			selector_operands.add(operand);
		}
		
		
		//alright, we have parsed the selector. Now, let's apply it.
		ArrayList<HtmlToken> candidates=new ArrayList<HtmlToken>();
		candidates.addAll(this.getListOfTokens());
		int operand_pointer=0;
		int operator_pointer=0;
		int i=0;
		while(operator_pointer<selector_tokens.size())
		{
			if(i%2==0)
			{
				current_token=selector_tokens.get(operator_pointer);
				operator_pointer++;
				if(current_token.startsWith("#"))
				{
					String current_selector_id=current_token.substring(1);
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					for(int j=0;j<candidates.size();j++)
					{
						HtmlToken current_candidate_token=candidates.get(j);
						String current_candidate_token_id=current_candidate_token.get_id();
						if(current_candidate_token.is_opening() && current_candidate_token_id!=null && current_candidate_token_id.equals(current_selector_id))
						{
							new_candidates.add(current_candidate_token);
						}
					}
					candidates=new_candidates;
				}
				else if(current_token.startsWith("."))
				{
					String current_selector_class=current_token.substring(1);
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					for(int j=0;j<candidates.size();j++)
					{
						HtmlToken current_candidate_token=candidates.get(j);
						ArrayList<String> current_candidate_token_classes=current_candidate_token.get_classes();
						if(current_candidate_token_classes.contains(current_selector_class))
						{
							new_candidates.add(current_candidate_token);
						}
					}
					candidates=new_candidates;
				}
				else if(current_token.startsWith("["))
				{
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					String current_property_and_value=current_token.substring(1,current_token.length()-1);
					String current_property=null;
					String current_value=null;
					String tokens[]=current_property_and_value.split("=");
					current_property=tokens[0];
					if(tokens.length>1)
					{
						current_value=tokens[1];
						if(current_value.startsWith("\"") && current_value.endsWith("\"")){current_value=current_value.substring(1,current_value.length()-1);}
					}
					for(int j=0;j<candidates.size();j++)
					{
						HtmlToken current_candidate_token=candidates.get(j);
						//System.out.println(current_token);
						int property_index=current_candidate_token.get_properties_names().indexOf(current_property);
						if(property_index!=-1)
						{
							//has property. Now, will it much its value? (if any)
							if(current_value!=null)
							{
								String property_parts[]=current_candidate_token.get_properties_values().get(property_index).split(";");
								if(Arrays.asList(property_parts).contains(current_value))
								{
									new_candidates.add(current_candidate_token);
								}
							}
							else
							{
								new_candidates.add(current_candidate_token);
							}
						}
					}
					candidates=new_candidates;
					
				}
				else	//type of token
				{
					String current_selector_type_tokens[]=current_token.split(":");
					String current_selector_type=current_selector_type_tokens[0];
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					for(int j=0;j<candidates.size();j++)
					{
						HtmlToken current_candidate_token=candidates.get(j);
						String current_candidate_token_type=current_candidate_token.get_type();
						if(
								(current_candidate_token.is_opening() && current_candidate_token_type!=null && current_candidate_token_type.equals(current_selector_type)  )
								||
								(current_selector_type.equals("*"))
							)
						{
							new_candidates.add(current_candidate_token);
						}
					}
					for(int j=1;j<current_selector_type_tokens.length;j++)
					{
						String current_suffix_operand=current_selector_type_tokens[j];
						if(current_suffix_operand.startsWith("nth-child("))
						{
							int child_number=Integer.parseInt(current_suffix_operand.substring(10,current_suffix_operand.length()-1));
							ArrayList<HtmlToken> new_new_candidates=new ArrayList<HtmlToken>();
							new_new_candidates.add(new_candidates.get(child_number-1)); //jquery nth-child starts by 1-indexed instead of 0-indexed
							new_candidates=new_new_candidates;
						}
					}
					candidates=new_candidates;
				}
			}
			else
			{
				//operand
				String current_operand=selector_operands.get(operand_pointer);
				operand_pointer++;
				
				if(current_operand.equals("AND"))
				{
				}
				else if(current_operand.equals(" "))
				{
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					for(int j=0;j<candidates.size();j++)
					{
						HtmlToken current_candidate=candidates.get(j);
						ArrayList<HtmlToken> current_candidate_sons=this.list_tree_of_tokens.get_descendants(current_candidate);
						for(int k=0;k<current_candidate_sons.size();k++)
						{
							new_candidates.add(current_candidate_sons.get(k));
						}
					}
					candidates=new_candidates;
				}
				else if(current_operand.equals(">"))
				{
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					for(int j=0;j<candidates.size();j++)
					{
						HtmlToken current_candidate=candidates.get(j);
						ArrayList<HtmlToken> current_candidate_sons=this.list_tree_of_tokens.get_sons(current_candidate);
						for(int k=0;k<current_candidate_sons.size();k++)
						{
							new_candidates.add(current_candidate_sons.get(k));
						}
					}
					candidates=new_candidates;
				}
				else if(current_operand.equals("~"))
				{
					ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
					for(int j=0;j<candidates.size();j++)
					{
						HtmlToken current_candidate=candidates.get(j);
						ArrayList<HtmlToken> current_candidate_sons=this.list_tree_of_tokens.get_brothers(current_candidate);
						for(int k=0;k<current_candidate_sons.size();k++)
						{
							new_candidates.add(current_candidate_sons.get(k));
						}
					}
					candidates=new_candidates;
				}
			}
			i++;
		}
		ArrayList<HtmlToken> new_candidates=new ArrayList<HtmlToken>();
		for(int j=0;post_functions!=null && j<post_functions.length;j++)
		{
			String current_post_function=post_functions[j];
			for(int current_candidate_number=0;current_candidate_number<candidates.size();current_candidate_number++)
			{
				HtmlToken current_candidate=candidates.get(current_candidate_number);
				if(current_post_function.startsWith(".attr("))
				{
					String attr_value=current_post_function.substring(6+1,current_post_function.length()-1-1); //+1,-1 to avoid "'"
					int property_index=current_candidate.get_properties_names().indexOf(attr_value);
					String property_value=current_candidate.get_properties_values().get(property_index);
					try 
					{
						new_candidates.add(new HtmlToken(property_value,0));
					} 
					catch (UnparseableHtmlTokenException e) 
					{
					}
				}
			}
		}
		if(post_functions==null || post_functions.length==0)
		{
			new_candidates=candidates;
		}
		return new_candidates;
	}
	//--------------------------------------------------------------------
}
