package com.glezo.systemCommandExecutor;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * This class can be used to execute a system command from a Java application.
 * See the documentation for the public methods of this class for more
 * information.
 * 
 * Documentation for this class is available at this URL:
 * 
 * http://devdaily.com/java/java-processbuilder-process-system-exec
 *
 * 
 * Copyright 2010 alvin j. alexander, devdaily.com.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.

 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Please ee the following page for the LGPL license:
 * http://www.gnu.org/licenses/lgpl.txt
 * 
 */
public class SystemCommandExecutor
{
	private	List<String>			commandInformation;
	private	String					adminPassword;
	private	ThreadedStreamHandler	inputStreamHandler;
	private	ThreadedStreamHandler	errorStreamHandler;
  
  /**
   * Pass in the system command you want to run as a List of Strings, as shown here:
   * 
   * List<String> commands = new ArrayList<String>();
   * commands.add("/sbin/ping");
   * commands.add("-c");
   * commands.add("5");
   * commands.add("www.google.com");
   * SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
   * commandExecutor.executeCommand();
   * 
   * Note: I've removed the other constructor that was here to support executing
   *       the sudo command. I'll add that back in when I get the sudo command
   *       working to the point where it won't hang when the given password is
   *       wrong.
   *
   * @param commandInformation The command you want to run.
   */
	public SystemCommandExecutor(final List<String> commandInformation)
	{
		if (commandInformation==null) throw new NullPointerException("The commandInformation is required.");
		this.commandInformation = commandInformation;
		this.adminPassword = null;
	}

	public int executeCommand() throws IOException, InterruptedException
	{
		int exitValue = -99;

		try
		{
			ProcessBuilder pb = new ProcessBuilder(commandInformation);
			Process process = pb.start();

			// you need this if you're going to write something to the command's input stream
			// (such as when invoking the 'sudo' command, and it prompts you for a password).
			OutputStream stdOutput = process.getOutputStream();
      
			// i'm currently doing these on a separate line here in case i need to set them to null
			// to get the threads to stop.
			// see http://java.sun.com/j2se/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();

			// these need to run as java threads to get the standard output and error from the command.
			// the inputstream handler gets a reference to our stdOutput in case we need to write
			// something to it, such as with the sudo command
			this.inputStreamHandler = new ThreadedStreamHandler(inputStream, stdOutput, adminPassword);
			this.errorStreamHandler = new ThreadedStreamHandler(errorStream);

			// TODO the inputStreamHandler has a nasty side-effect of hanging if the given password is wrong; fix it
			this.inputStreamHandler.start();
			this.errorStreamHandler.start();

			// TODO a better way to do this?
			exitValue = process.waitFor();
 
			// TODO a better way to do this?
			inputStreamHandler.interrupt();
			errorStreamHandler.interrupt();
			inputStreamHandler.join();
			errorStreamHandler.join();
		}
		catch (IOException e)
		{
			// TODO deal with this here, or just throw it?
			e.printStackTrace();
			throw e;
		}
		catch (InterruptedException e)
		{
			// generated by process.waitFor() call
			// TODO deal with this here, or just throw it?
			throw e;
		}
		finally
		{
			return exitValue;
		}
	}

	/**
	 * Get the standard output (stdout) from the command you just exec'd.
	 */
	public StringBuilder	getStandardOutputFromCommand()
	{
		return this.inputStreamHandler.getOutputBuffer();
	}
	public String			getStandardOutputFromCommand_string()
	{
		return this.inputStreamHandler.getOutputBuffer().toString();
	}
	public String[]			getStandardOutputFromCommand_string_lines()
	{
		return this.inputStreamHandler.getOutputBuffer().toString().split("\n");
	}
	public String[]			getStandardOutputFromCommand_string_clean_lines()	//remove non-printable colour stuff
	{
		String result[]=this.inputStreamHandler.getOutputBuffer().toString().split("\n");
		String g=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)108 +(char)27 +(char)62 +(char)27 +(char)91 +(char)57 +(char)52 +(char)109	;
		String o=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)104 +(char)27 +(char)61 +(char)27 +(char)91 +(char)52 +(char)109			;
		String m=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)104 +(char)27 +(char)61 +(char)27 +(char)91 +(char)52						;
		String n=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)104 +(char)27 +(char)61														;
		String a=""+(char)27 + (char)91 +(char)51 +(char)50  +(char)109																			; 
		String f=""+(char)27 + (char)91 +(char)51 +(char)49  +(char)109																			; 
		String b=""+(char)27 + (char)91 +(char)48 +(char)109																					;
		String c=""+(char)27 + (char)91 +(char)51 +(char)52  +(char)109																			;
		String d=""+(char)27 + (char)91 +(char)51 +(char)50  +(char)109																			;
		String e=""+(char)27 + (char)91 +(char)51 +(char)51  +(char)109																			;
		String h=""+(char)27 + (char)91 +(char)57 +(char)52  +(char)109																			;
		String j=""+(char)27 + (char)91 +(char)57 +(char)49  +(char)109																			;
		String k=""+(char)27 + (char)91 +(char)57 +(char)50  +(char)109																			;
		String l=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)108 +(char)27																;
		for(int i=0;i<result.length;i++)
		{
			result[i]=result[i].replace(g,"").replace(o,"").replace(m,"").replace(n,"").replace(a,"").replace(b,"").replace(c,"").replace(d,"").replace(e,"").replace(f,"").replace(h,"").replace(j,"").replace(k,"").replace(l,"");
		}
		return result;
	}

	/**
	 * Get the standard error (stderr) from the command you just exec'd.
	 */
	public StringBuilder	getStandardErrorFromCommand()
	{
		return this.errorStreamHandler.getOutputBuffer();
	}
	public String			getStandardErrorFromCommand_string()
	{
		return this.errorStreamHandler.getOutputBuffer().toString();
	}
	public String[]			getStandardErrorFromCommand_string_lines()
	{
		return this.errorStreamHandler.getOutputBuffer().toString().split("\n");
	}
	public String[]			getStandardErrorFromCommand_string_clean_lines()	//remove non-printable colour stuff
	{
		String result[]=this.errorStreamHandler.getOutputBuffer().toString().split("\n");
		String g=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)108 +(char)27 +(char)62 +(char)27 +(char)91 +(char)57 +(char)52 +(char)109	;
		String o=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)104 +(char)27 +(char)61 +(char)27 +(char)91 +(char)52 +(char)109			;
		String m=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)104 +(char)27 +(char)61 +(char)27 +(char)91 +(char)52						;
		String n=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)104 +(char)27 +(char)61														;
		String a=""+(char)27 + (char)91 +(char)51 +(char)50  +(char)109																			; 
		String f=""+(char)27 + (char)91 +(char)51 +(char)49  +(char)109																			; 
		String b=""+(char)27 + (char)91 +(char)48 +(char)109																					;
		String c=""+(char)27 + (char)91 +(char)51 +(char)52  +(char)109																			;
		String d=""+(char)27 + (char)91 +(char)51 +(char)50  +(char)109																			;
		String e=""+(char)27 + (char)91 +(char)51 +(char)51  +(char)109																			;
		String h=""+(char)27 + (char)91 +(char)57 +(char)52  +(char)109																			;
		String j=""+(char)27 + (char)91 +(char)57 +(char)49  +(char)109																			;
		String k=""+(char)27 + (char)91 +(char)57 +(char)50  +(char)109																			;
		String l=""+(char)27 + (char)91 +(char)63 +(char)49  +(char)108 +(char)27																;
		for(int i=0;i<result.length;i++)
		{
			result[i]=result[i].replace(g,"").replace(o,"").replace(m,"").replace(n,"").replace(a,"").replace(b,"").replace(c,"").replace(d,"").replace(e,"").replace(f,"").replace(h,"").replace(j,"").replace(k,"").replace(l,"");
		}
		return result;
	}

	public String			getExecutedCommand()
	{
		String result="";
		Iterator<String> it=this.commandInformation.iterator();
		while(it.hasNext())
		{
			result+=it.next()+" ";
		}
		result=result.substring(0,result.length()-1);
		return result;
	}
}






