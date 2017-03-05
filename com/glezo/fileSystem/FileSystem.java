package com.glezo.fileSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/*
Abstract library with useful, high-level interface to interact with the file system
*/

public class FileSystem 
{
	//---------------------------------------------------
	//dir------------------------------------------------
	//---------------------------------------------------
	public static void				create_directory(String path)
	{
		File f=new File(path);
		f.mkdirs();
	}
	//---------------------------------------------------
	//files-----------------------------------------------
	//---------------------------------------------------
	public static void				deleteFile(String file)
	{
		File f=new File(file);
		f.delete();
	}
	//---------------------------------------------------
	public static void				delete_everything(String folder)
	{
		File f=new File(folder);
		File[] list=f.listFiles();
		for(int i=0;i<list.length;i++)
		{
			if(list[i].isFile())
			{
				FileSystem.deleteFile(list[i].getAbsolutePath());
			}
			else if(list[i].isDirectory())
			{
				FileSystem.delete_everything(list[i].getAbsolutePath());
				list[i].delete();
			}
		}
	}
	//---------------------------------------------------
	public static void				deleteAllFilesFromFolderMatching(String folder,String pattern)
	{
		File f=new File(folder);
		File[] list=f.listFiles();
		for(int i=0;i<list.length;i++)
		{
			if(list[i].getName().contains(pattern))
			{
				FileSystem.deleteFile(list[i].getAbsolutePath());
			}
		}
	}
	//---------------------------------------------------
	public static boolean			fileExists(String filepath)
	{
		File f=new File(filepath);
		return f.exists();
	}
	//----------------------------------------------------------------------------
	public static String			read_file(String filepath)
	{
		BufferedReader in=null;
		try
		{
			in=new BufferedReader(new FileReader(filepath));
		}
		catch(IOException e)
		{
			return null;
		}
		
		String result="";
		String currentLine;
		try 
		{
			while((currentLine=in.readLine())!=null)
			{
					result+=currentLine+"\n";
			}
			in.close();
		} 
		catch (IOException e) 
		{
			return null;
		}
		return result;
	}
	//----------------------------------------------------------------------------
	public static ArrayList<String>	read_file_by_lines(String filepath)
	{
		BufferedReader in=null;
		try
		{
			in=new BufferedReader(new FileReader(filepath));
		}
		catch(IOException e)
		{
			return null;
		}
		
		
		String currentLine;
		ArrayList<String> result=new ArrayList<String>();
		try 
		{
			while((currentLine=in.readLine())!=null)
			{
					result.add(currentLine);
			}
			in.close();
		} 
		catch (IOException e) 
		{
			return null;
		}
		return result;
	}
	//----------------------------------------------------------------------------
	public static int 				copyFile(String original,String copy)
	{
		File origin=new File(original);
		File destiny=new File(copy);
		InputStream in=null;
		OutputStream out=null;
		try
		{
			in=new FileInputStream(origin);
		}
		catch(FileNotFoundException e)
		{
			return -1;
		}
		try
		{
			out=new FileOutputStream(destiny);
		}
		catch(FileNotFoundException e)
		{
			try
			{
				in.close();
			}
			catch(IOException e1)
			{
			}
			return -2;
		}
		byte[] buffer=new byte[1024];
		int length;
		try
		{
			while((length=in.read(buffer))>0)
			{
				out.write(buffer,0,length);
			}
		}
		catch(IOException e)
		{
		}
		try
		{
			in.close();
			out.close();
		}
		catch(IOException e)
		{
		}
		return 0;
	}
	//---------------------------------------------------
	public static void				concatenateFiles(String originPath,String destinyPath)
	{
		BufferedWriter out=null;
		BufferedReader in=null;
		try
		{
			out=new BufferedWriter(new FileWriter(destinyPath,true));
			in=new BufferedReader(new FileReader(originPath));
		}
		catch(IOException e)
		{
		}
		
		String currentLine;
		try
		{
			while((currentLine=in.readLine())!=null)
			{
				out.write(currentLine+"\n");
			}
			in.close();
			out.close();
		}
		catch(IOException e)
		{
		}
	}
	//---------------------------------------------------
	public static void				truncate_and_write_to_file(String filepath,String content)
	{
		BufferedWriter bw=null;
		try 
		{
			bw=new BufferedWriter(new FileWriter(new File(filepath)));
			bw.write(content);
			bw.flush();
			bw.close();
		} 
		catch (IOException e) 
		{
		}
	}
	//---------------------------------------------------
	public static Date				get_last_modified(String filepath)
	{
		File f=new File(filepath);
		long last_mod_long=f.lastModified();
		return new Date(last_mod_long);
	}
	//---------------------------------------------------
}
