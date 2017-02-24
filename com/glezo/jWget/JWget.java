package com.glezo.jWget;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/*
	I'm pretty sure I didn't write the codebase of this class. Sadly, I didn't keep reference to the author.
	So, if you, as author of this code, find this:
	  -Thanks, I found it useful.
	  -Let me know, so I can add a reference to your work. 
*/
public class JWget 
{
	//-------------------------------------------------------------------------------------------------------------
	public static void jwGet(String saveAsFile, String urlOfFile) throws MalformedURLException,IOException 
	{
		InputStream httpIn = null;
		OutputStream fileOutput = null;
		OutputStream bufferedOut = null;

		// check the http connection before we do anything to the fs
		httpIn = new BufferedInputStream(new URL(urlOfFile).openStream());
		// prep saving the file
		fileOutput = new FileOutputStream(saveAsFile);
		bufferedOut = new BufferedOutputStream(fileOutput, 1024);
		byte data[] = new byte[1024];
		boolean fileComplete = false;
		int count = 0;
		while (!fileComplete) 
		{
			count = httpIn.read(data, 0, 1024);
			if (count <= 0) 
			{
				fileComplete = true;
			} 
			else 
			{
				bufferedOut.write(data, 0, count);
			}
		}
		bufferedOut.close();
		fileOutput.close();
		httpIn.close();
	}
	//-------------------------------------------------------------------------------------------------------------
	public static ArrayList<Byte> jwGet(String urlOfFile) throws MalformedURLException,IOException 
	{
		ArrayList<Byte> result=new ArrayList<Byte>();
		InputStream httpIn = null;

		// check the http connection before we do anything to the fs
		httpIn = new BufferedInputStream(new URL(urlOfFile).openStream());
		byte data[] = new byte[1024];
		boolean fileComplete = false;
		int count = 0;
		while (!fileComplete) 
		{
			count = httpIn.read(data, 0, 1024);
			if (count <= 0) 
			{
				fileComplete = true;
			} 
			else 
			{
				//TODO! guetto code. Should try to use kind of result.addAll( (ArrayList<Byte)Arrays.asList(data) ) or sth
				for(int i=0;i<count;i++)
				{
					result.add(new Byte(data[i]));
				}
			}
		}
		httpIn.close();
		return result;
	}
	//-------------------------------------------------------------------------------------------------------------

}
