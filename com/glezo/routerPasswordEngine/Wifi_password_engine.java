package com.glezo.routerPasswordEngine;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

import com.glezo.mac.Mac;
import com.glezo.mac.UnparseableMacException;
import com.glezo.passwordDictionary.PasswordDictionary;
import com.glezo.stringUtils.StringUtils;

import com.glezo.language.Language;
import com.glezo.language.Language_prefix_middle_suffix;

//TODO! cambiar los String bssid por Mac bssid

//based on https://github.com/routerkeygen/routerkeygenAndroid/blob/master/android/routerKeygen/src/main/java/org/exobel/routerkeygen/WirelessMatcher.java
//but caring 'bout model-view-controller and all that stuff nobody seems to give a damn about.


public class Wifi_password_engine 
{
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//PASSWORD ALGORITHMS----------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				get_easybox_password(Mac bssid)
	{
		//glezo
		/*
			EasyBox A 300
			EasyBox A 400
			EasyBox A 401
			EasyBox A 600
			EasyBox A 601
			EasyBox A 800
			EasyBox A 801
			EasyBox 402
			EasyBox 602
			EasyBox 802
			EasyBox 803 (no todos las versiones)
			Que corresponden a las siguientes direcciones MAC:
			74:31:70 
			84:9C:A6
			88:03:55
			1C:C6:3C 
			50:7E:5D
		 */
		String last_two_bytes=bssid.get_mac(":",true).substring(12).replace(":","");
		int last_two_bytes_hex=Integer.parseInt(last_two_bytes, 16);
		String last_two_bytes_hex_string=Integer.toString(last_two_bytes_hex);
		while(last_two_bytes_hex_string.length()!=5)
		{
			last_two_bytes_hex_string="0"+last_two_bytes_hex_string;
		}
		//String S6	=""+last_two_bytes_hex_string.charAt(0);
		String S7	=""+last_two_bytes_hex_string.charAt(1);
		String S8	=""+last_two_bytes_hex_string.charAt(2);
		String S9	=""+last_two_bytes_hex_string.charAt(3);
		String S10	=""+last_two_bytes_hex_string.charAt(4);
		//String M7	=""+bessid.charAt(9);
		//String M8	=""+bessid.charAt(10);
		String M9	=""+bssid.get_mac(":",true).charAt(12); 
		String M10	=""+bssid.get_mac(":",true).charAt(13);
		String M11	=""+bssid.get_mac(":",true).charAt(15);
		String M12	=""+bssid.get_mac(":",true).charAt(16);
		int k1_double_int=	Integer.parseInt(S7,16)+Integer.parseInt(S8,16)	+Integer.parseInt(M11,16)+Integer.parseInt(M12,16);
		String k1=Integer.toString(k1_double_int, 16);
		k1=k1.substring(k1.length()-1,k1.length()).toUpperCase();
		//6.- Lo mismo para K2: (M9 + M10 + S9 + S10) = 12 = 2
		int k2_double_int=	Integer.parseInt(M9,16)+Integer.parseInt(M10,16)+Integer.parseInt(S9,16)+Integer.parseInt(S10,16);
		String k2=Integer.toString(k2_double_int,16);
		k2=k2.substring(k2.length()-1,k2.length()).toUpperCase();
		String X1 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(S10,16),16).toUpperCase();
		String X2 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(S9,16) ,16).toUpperCase();
		String X3 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(S8,16) ,16).toUpperCase();
		String Y1 = Integer.toString(Integer.parseInt(k2,16)  ^  Integer.parseInt(M10,16),16).toUpperCase();
		String Y2 = Integer.toString(Integer.parseInt(k2,16)  ^  Integer.parseInt(M11,16),16).toUpperCase();
		String Y3 = Integer.toString(Integer.parseInt(k2,16)  ^  Integer.parseInt(M12,16),16).toUpperCase();
		String Z1 = Integer.toString(Integer.parseInt(M11,16) ^  Integer.parseInt(S10,16),16).toUpperCase();
		String Z2 = Integer.toString(Integer.parseInt(M12,16) ^  Integer.parseInt(S9,16) ,16).toUpperCase();
		String Z3 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(k2,16) ,16).toUpperCase();
		
		ArrayList<String> passwords=new ArrayList<String>();
		passwords.add(X1+Y1+Z1+X2+Y2+Z2+X3+Y3+Z3);
		PasswordDictionary result=new PasswordDictionary("get_easybox_password",passwords,"");
		return result;
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				vodafone_arcadyan_spain(Mac bssid)
	{
		PasswordDictionary pre_result=Wifi_password_engine.get_easybox_password(bssid);
		ArrayList<String> words=new ArrayList<String>();
		for(int i=0;i<pre_result.getWords().size();i++)
		{
			words.add(pre_result.getWords().get(i).replace("0","1"));
		}
		return new PasswordDictionary("vodafone_arcadyan_spain",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				SMC_7908_AISP_SMC_7908VoWBRA(Mac bssid,String essid)
	{
		//http://foro.seguridadwireless.net/desarrollo-112/wlan4xx-algoritmo-routers-yacom/
		//glezo
		//WLAN123456/WiFi123456/YaCom123456		/	00:13:F7:97:81:A6	/		88:2	/
		if(essid.length()<10)			
		{
			return new PasswordDictionary("SMC_7908_AISP_SMC_7908VoWBRA",new ArrayList<String>(),"Essid too short (should be 10>)");
		}
		for(int i=essid.length()-6;i<essid.length();i++)
		{
			char a=essid.charAt(i);
			String aa=""+a;
			if(Language.digits_upperhex.contains(aa))
			{
			}
			else
			{
				return new PasswordDictionary("SMC_7908_AISP_SMC_7908VoWBRA",new ArrayList<String>(),"Essid doesn't match (WLAN|WiFi|YaCom{6 upperhex})");
			}
		}
			
		//String M8	=""+eessid.charAt(4);
		String M9	=""+essid.charAt(5);
		String M10	=""+essid.charAt(6);
		String S8	=""+essid.charAt(7);
		String S9	=""+essid.charAt(8);
		String S10	=""+essid.charAt(9);
		String M11	=""+bssid.get_mac(":",true).charAt(15);
		String M12	=""+bssid.get_mac(":",true).charAt(16);
		
		int k2_double_int=	Integer.parseInt(M9,16)+Integer.parseInt(M10,16)+Integer.parseInt(S9,16)+Integer.parseInt(S10,16);
		String k2=Integer.toString(k2_double_int,16);
		k2=""+k2.charAt(k2.length()-1);
		
		ArrayList<String> words=new ArrayList<String>();
		for(int i=0;i<9;i++)
		{
			String current_S7=Integer.toString(i);
			int k1_double_int=	Integer.parseInt(current_S7,16)+Integer.parseInt(S8,16)+Integer.parseInt(M11,16)+Integer.parseInt(M12,16);
			String k1=Integer.toString(k1_double_int,16);
			k1=""+k1.charAt(k1.length()-1);
		
			String X1 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(S10,16),16).toUpperCase();
			String X2 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(S9,16) ,16).toUpperCase();
			String X3 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(S8,16) ,16).toUpperCase();
			String Y1 = Integer.toString(Integer.parseInt(k2,16)  ^  Integer.parseInt(M10,16),16).toUpperCase();
			String Y2 = Integer.toString(Integer.parseInt(k2,16)  ^  Integer.parseInt(M11,16),16).toUpperCase();
			String Y3 = Integer.toString(Integer.parseInt(k2,16)  ^  Integer.parseInt(M12,16),16).toUpperCase();
			String Z1 = Integer.toString(Integer.parseInt(M11,16) ^  Integer.parseInt(S10,16),16).toUpperCase();
			String Z2 = Integer.toString(Integer.parseInt(M12,16) ^  Integer.parseInt(S9,16) ,16).toUpperCase();
			String Z3 = Integer.toString(Integer.parseInt(k1,16)  ^  Integer.parseInt(k2,16) ,16).toUpperCase();
			String W1 = Integer.toString(Integer.parseInt(X1,16)  ^  Integer.parseInt(Z2,16) ,16).toUpperCase();
			String W2 = Integer.toString(Integer.parseInt(Y2,16)  ^  Integer.parseInt(Y3,16) ,16).toUpperCase();
			String W3 = Integer.toString(Integer.parseInt(Y1,16)  ^  Integer.parseInt(X3,16) ,16).toUpperCase();
			String W4 = Integer.toString(Integer.parseInt(Z3,16)  ^  Integer.parseInt(X2,16) ,16).toUpperCase();
			String Z  =W4+X1+Y1+Z1+W1+X2+Y2+Z2+W2+X3+Y3+Z3+W3;
			words.add(Z);
		}
		return new PasswordDictionary("SMC_7908_AISP_SMC_7908VoWBRA",words,"");
		
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				zyxel_P660_HW_B1A(Mac bssid,String eessid)	throws NoSuchAlgorithmException
	{
		//http://utilidades.gatovolador.net/wlan/
		//glezo
		//00:1F:A4 according to http://pof.eslack.org/archives/files/calcwlan-ng.sh [CalcWLAN-ng]
		if(eessid==null)
		{
			return new PasswordDictionary("zyxel_P660_HW_B1A",new ArrayList<String>(),"Null essid");
		}

		String a=bssid.get_mac("",false).substring(0, 8);
		String b=eessid.toLowerCase();
		String c=b.substring(b.length()-4,b.length());
		String d=StringUtils.md5(a+c);
		String f=d.substring(0,20);
		ArrayList<String> words=new ArrayList<String>();
		words.add(f.toUpperCase());
		return new PasswordDictionary("zyxel_P660_HW_B1A",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				comtrend_ct_536(Mac bssid,String eessid)	throws NoSuchAlgorithmException
	{
		//http://utilidades.gatovolador.net/wlan/
		String b=bssid.get_mac("",true);
		String e=eessid.toUpperCase();
		
		String o=b.substring(0,8);
		String p=e.substring(e.length()-4,e.length());
		String q=o+p;
		String r=StringUtils.md5("bcgbghgg"+q+b);
		String s=r.substring(0, 20);
		ArrayList<String> words=new ArrayList<String>();
		words.add(s);
		return new PasswordDictionary("comtrend_ct_536",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				comtrend_ct_536(Mac bssid)					throws NoSuchAlgorithmException
	{
		//http://utilidades.gatovolador.net/wlan/
		String b=bssid.get_mac("",true);
		String c=b.substring(b.length()-4,b.length());
		String A=Integer.toString(Integer.parseInt(c,16)-3,16).toUpperCase();
		String B=Integer.toString(Integer.parseInt(c,16)-1,16).toUpperCase();
		String C=c;
		
		String d=bssid.get_mac("",true).substring(0,8);

		ArrayList<String> result=new ArrayList<String>();
		result.add(StringUtils.md5("bcgbghgg"+d+A+bssid.get_mac("",true)).substring(0,20));
		result.add(StringUtils.md5("bcgbghgg"+d+B+bssid.get_mac("",true)).substring(0,20));
		result.add(StringUtils.md5("bcgbghgg"+d+C+bssid.get_mac("",true)).substring(0,20)); 
		//bug in the original algorithm: it substracts the last 4 chars to avoid the ' o ' when it should've removed 3 instead
		result.add(StringUtils.md5("bcgbghgg"+d+C+bssid.get_mac("",true)).substring(0,19)); 
		return new PasswordDictionary("comtrend_ct_536",result,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				speedtouch_keys(String eessid)					throws NoSuchAlgorithmException
	{
		String eessid_suffix=null;
		if(eessid.startsWith("SpeedTouch"))	{	eessid_suffix=eessid.substring(10);	}
		
		ArrayList<String> result=new ArrayList<String>();
		byte[] charectbytes0 = {'3', '3', '3', '3', '3', '3',
				'3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4',
				'4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5',
				'5', '5', '5', '5',};
		byte[] charectbytes1 = {'0', '1', '2', '3', '4', '5',
				'6', '7', '8', '9', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F', '0', '1', '2', '3', '4', '5', '6',
				'7', '8', '9', 'A',};
		
		byte[] cp = new byte[12];
		cp[0]	= (byte) (char) 'C';
		cp[1]	= (byte) (char) 'P';
		//years 2005 -> 2015
		for(int current_year=2005;current_year<=2016;current_year++)
		{
			int current_year_yy=current_year % 100;
			cp[2] = (byte) Character.forDigit((current_year_yy / 10), 10);
			cp[3] = (byte) Character.forDigit((current_year_yy % 10), 10);
			for(int current_week=1;current_week<=52;current_week++)
			{
				cp[4] = (byte) Character.forDigit((current_week / 10), 10);
				cp[5] = (byte) Character.forDigit((current_week % 10), 10);
				for(int x1=0;x1<36;x1++)
				{
					cp[6]	=charectbytes0[x1];
					cp[7]	=charectbytes1[x1];
					for(int x2=0;x2<36;x2++)
					{
						cp[8]	=charectbytes0[x2];
						cp[9]	=charectbytes1[x2];
						for(int x3=0;x3<36;x3++)
						{
							cp[10]	=charectbytes0[x3];
							cp[11]	=charectbytes1[x3];
							
							String current_serial_sha1=StringUtils.sha1(cp).toUpperCase();
							if(current_serial_sha1.endsWith(eessid_suffix))
							{
								String password=current_serial_sha1.substring(0,10);
								result.add(password);
							}
						}
					}
				}
			}
		}
		return new PasswordDictionary("speedtouch_keys",result,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				ZTE_W5(Mac bssid)
	{
		//twitter:
		String bssid_string=bssid.get_mac("",false);
		String result=bssid_string.substring(10,bssid_string.length());
		ArrayList<String> words=new ArrayList<String>();
		words.add(result);
		return new PasswordDictionary("ZTE_W5",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				xavi_comtrend_zyxel(String essid,Mac bssid)
	{
		String mac=bssid.get_mac(":",true);
		boolean essid_matches=false;
		if(essid.length()==7 && essid.matches("WLAN_[0-9a-fA-F]{2}") &&  essid.substring(5).equals(mac.substring(15)))	{	essid_matches=true;}
		
		String prefix=null;
		
		
        if(		mac.startsWith("00:01:36"))	{prefix="X000138";	}
        else if(mac.startsWith("00:01:38"))	{prefix="X000138";	}
        else if(mac.startsWith("00:02:CF"))	{prefix="Z0002CF";	}
        else if(mac.startsWith("00:03:C9"))	{prefix="C0030DA";	}
        else if(mac.startsWith("00:03:DA"))	{prefix="C0030DA";	}
        else if(mac.startsWith("00:13:49"))	{prefix="Z001349";	}
        else if(mac.startsWith("00:16:38"))	{prefix="C0030DA";	}
        else if(mac.startsWith("00:18:02"))	{prefix="H";		}
        else if(mac.startsWith("00:19:CB"))	{prefix="Z0019CB";	}
        else if(mac.startsWith("00:1D:20"))	{prefix="C001D20";	}
        else if(mac.startsWith("00:1F:9F"))	{prefix="T5YF69A";	}
        else if(mac.startsWith("00:23:F8"))	{prefix="Z0023F8";	}
        else if(mac.startsWith("00:30:DA"))	{prefix="C0030DA";	}
        else if(mac.startsWith("00:60:B3"))	{prefix="Z001349";	}
        else if(mac.startsWith("00:A0:C5"))	{prefix="Z0019CB";	}
        else if(mac.startsWith("40:4A:03"))	{prefix="Z404A03";	}
        else if(mac.startsWith("50:67:F0"))	{prefix="Z5067F0";	}
        else if(mac.startsWith("E0:91:53"))	{prefix="XE09153";	}

		if(essid_matches)
		{
			Language_prefix_middle_suffix l=new Language_prefix_middle_suffix(Language.digits_upperhex,prefix,essid.substring(5),13);
			return new PasswordDictionary("xavi_comtrend_zyxel",l.getWholeDictionary(),"essid matched");
		}
		else
		{
			Language_prefix_middle_suffix l=new Language_prefix_middle_suffix(Language.digits_upperhex,prefix,"",13);
			return new PasswordDictionary("xavi_comtrend_zyxel",l.getWholeDictionary(),"essid didn't match");
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				SitecomX5000Keygen(Mac bssid)
	{
		String strings[]=new String[4];	//wlm2500 , wlm3500 , wlm5500 5ghz , wlm5500 2.4ghz
		strings[0]=bssid.get_mac("",false);	
		strings[1]=bssid.get_mac("",true);
		try
		{
			strings[2]=new Mac(bssid.toInteger()+1).get_mac("",true);	
			strings[3]=new Mac(bssid.toInteger()+2).get_mac("",true);	
		}
		catch(UnparseableMacException e)
		{
		}
		ArrayList<String> words=new ArrayList<String>();
		for(int i=0;i<4;i++)
		{
			String CHARSET = "123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ"; // without
			String mac=strings[i];
			StringBuilder key = new StringBuilder();
			int numericMac = Integer.parseInt("0"+ mac.substring(6).split("[A-Fa-f]")[0]);
			key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(5))	*(mac.charAt(9) + mac.charAt(3) + mac.charAt(11)))  % CHARSET.length()));
			key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(6))	*(mac.charAt(8) + mac.charAt(10) + mac.charAt(11))) % CHARSET.length()));
			key.append(CHARSET.charAt(((numericMac + mac.charAt(3) + mac.charAt(5))		*(mac.charAt(7) + mac.charAt(9) + mac.charAt(11)))  % CHARSET.length()));
			key.append(CHARSET.charAt(((numericMac + mac.charAt(7) + mac.charAt(6))		*(mac.charAt(5) + mac.charAt(4) + mac.charAt(11)))  % CHARSET.length()));
			key.append(CHARSET.charAt(((numericMac + mac.charAt(7) + mac.charAt(6))		*(mac.charAt(8) + mac.charAt(9) + mac.charAt(11)))  % CHARSET.length()));
			key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(5))	*(mac.charAt(3) + mac.charAt(4) + mac.charAt(11)))  % CHARSET.length()));
			key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(4))	*(mac.charAt(6) + mac.charAt(8) + mac.charAt(11)))  % CHARSET.length()));
			key.append(CHARSET.charAt(((numericMac + mac.charAt(10) + mac.charAt(11))	*(mac.charAt(7) + mac.charAt(8) + mac.charAt(11)))  % CHARSET.length()));
	        words.add(key.toString());
		}
		return new PasswordDictionary("SitecomX5000Keygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				Sitecom2100Keygen(Mac bssid)
	{
		String CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ"; //Missing I,O
		
		String mac = bssid.get_mac("",true);
		MessageDigest md;
		try 
		{
			md = MessageDigest.getInstance("MD5");
		} 
		catch (NoSuchAlgorithmException e1) 
		{
			return null;
		}
		try 
		{
			md.reset();
			md.update(mac.toLowerCase().getBytes("ASCII"));
			byte[] hash = md.digest();
			String slicedHash = StringUtils.getHexString(hash);
			slicedHash = slicedHash.substring(slicedHash.length() - 16);
        	
			ArrayList<String> words=new ArrayList<String>();
			final StringBuilder key = new StringBuilder();
			final BigInteger divider = new BigInteger("24");
			BigInteger magicNrBig = new BigInteger(slicedHash, 16);
			for(int i=0;i<12;i++)
			{
				key.append(CHARSET.charAt(magicNrBig.mod(divider).intValue()));
				magicNrBig = magicNrBig.divide(divider);
			}
			words.add(key.toString());
			return new PasswordDictionary("Sitecom2100Keygen",words,"");
		} 
		catch (UnsupportedEncodingException e) 
		{
			return new PasswordDictionary("Sitecom2100Keygen",null,"UnsuportedEncodingException");
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				SitecomWLR341_400xKeygen(Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		String CHARSETS_341[] = {"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", "W0X1CDYNJU8VOZA0BKL46PQ7RS9T2E5HI3MFG"};
		String CHARSETS_4000[] = {"23456789ABCDEFGHJKLMNPQRSTUVWXYZ38BZ", "WXCDYNJU8VZABKL46PQ7RS9T2E5H3MFGPWR2"};
		String CHARSETS_4004[] = {"JKLMNPQRST23456789ABCDEFGHUVWXYZ38BK", "E5MFJUWXCDKL46PQHAB3YNJ8VZ7RS9TR2GPW"};
		long MAGIC1 = 0x98124557L;
		long MAGIC2 = 0x0004321aL;
		long MAGIC3 = 0x80000000L;

		//pseudocode is as follows, so let's create two arrays:
		/*
		generateKey(mac, CHARSETS_341);
		generateKey(mac, CHARSETS_4000);
		generateKey(mac, CHARSETS_4004);
		generateKey(incrementMac(mac, 1), CHARSETS_341);
		generateKey(incrementMac(mac, 1), CHARSETS_4000);
		generateKey(incrementMac(mac, 1), CHARSETS_4004);
		generateKey(incrementMac(mac, 4), CHARSETS_341);
		generateKey(incrementMac(mac, 4), CHARSETS_4000);
		generateKey(incrementMac(mac, 4), CHARSETS_4004);
		*/
		Mac bssid_next_one=null;
		Mac bssid_next_four=null;
		try{bssid_next_one=new Mac(bssid.toInteger()+1);} catch (UnparseableMacException e) {}
		try{bssid_next_four=new Mac(bssid.toInteger()+4);} catch (UnparseableMacException e) {}
		String[][] charsets	=new String[][]	{CHARSETS_341	,CHARSETS_4000	,CHARSETS_4004	,CHARSETS_341	,CHARSETS_4000	,CHARSETS_4004	,CHARSETS_341	,CHARSETS_4000	,CHARSETS_4004};
		Mac[] macs			=new Mac[]		{bssid			,bssid			,bssid			,bssid_next_one	,bssid_next_one	,bssid_next_one	,bssid_next_four,bssid_next_four,bssid_next_four};
		
		for(int k=0;k<9;k++)
		{
			String[] current_charset=charsets[k];
			Mac current_mac			=macs[k];

			long val = Long.parseLong(current_mac.get_mac("",true).substring(4), 16);
			int[] offsets = new int[12];
			for(int i = 0; i < 12; ++i) 
			{
				if ((val & 0x1) == 0) 
				{
					val = val ^ MAGIC2;
					val = val >> 1;
				} 
				else 
				{
					val = val ^ MAGIC1;
					val = val >> 1;
					val = val | MAGIC3;
				}
				long offset = val % current_charset[0].length();
				offsets[i] = (int) offset; //safe because length is tiny
			}
			StringBuilder wpakey = new StringBuilder();
			wpakey.append(current_charset[0].charAt(offsets[0]));
			for(int i=0;i<11;++i) 
			{
				if (offsets[i] != offsets[i + 1]) 
				{
					wpakey.append(current_charset[0].charAt(offsets[i + 1]));
				} 
				else 
				{
					int newOffset = (offsets[i] + i + 1) % current_charset[0].length();
					wpakey.append(current_charset[1].charAt(newOffset));
				}
			}
			words.add(wpakey.toString());
		}
		return new PasswordDictionary("SitecomWLR341_400xKeygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				HuaweiKeygen(String essid,Mac bssid)
	{
		ArrayList<String> result=new ArrayList<String>();
		// Java adaptation of mac2wepkey.py from http://websec.ca/blog/view/mac2wepkey_huawei
		final int[] a0 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		final int[] a1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		final int[] a2 = {0, 13, 10, 7, 5, 8, 15, 2, 10, 7, 0, 13, 15, 2, 5, 8};
		final int[] a3 = {0, 1, 3, 2, 7, 6, 4, 5, 15, 14, 12, 13, 8, 9, 11, 10};
		final int[] a4 = {0, 5, 11, 14, 7, 2, 12, 9, 15, 10, 4, 1, 8, 13, 3, 6};
		final int[] a5 = {0, 4, 8, 12, 0, 4, 8, 12, 0, 4, 8, 12, 0, 4, 8, 12};
		final int[] a6 = {0, 1, 3, 2, 6, 7, 5, 4, 12, 13, 15, 14, 10, 11, 9, 8};
		final int[] a7 = {0, 8, 0, 8, 1, 9, 1, 9, 2, 10, 2, 10, 3, 11, 3, 11};
		final int[] a8 = {0, 5, 11, 14, 6, 3, 13, 8, 12, 9, 7, 2, 10, 15, 1, 4};
		final int[] a9 = {0, 9, 2, 11, 5, 12, 7, 14, 10, 3, 8, 1, 15, 6, 13, 4};
		final int[] a10 = {0, 14, 13, 3, 11, 5, 6, 8, 6, 8, 11, 5, 13, 3, 0, 14};
		final int[] a11 = {0, 12, 8, 4, 1, 13, 9, 5, 2, 14, 10, 6, 3, 15, 11, 7};
		final int[] a12 = {0, 4, 9, 13, 2, 6, 11, 15, 4, 0, 13, 9, 6, 2, 15, 11};
		final int[] a13 = {0, 8, 1, 9, 3, 11, 2, 10, 6, 14, 7, 15, 5, 13, 4, 12};
		final int[] a14 = {0, 1, 3, 2, 7, 6, 4, 5, 14, 15, 13, 12, 9, 8, 10, 11};
		final int[] a15 = {0, 1, 3, 2, 6, 7, 5, 4, 13, 12, 14, 15, 11, 10, 8, 9};
		final int[] n1 = {0, 14, 10, 4, 8, 6, 2, 12, 0, 14, 10, 4, 8, 6, 2, 12};
		final int[] n2 = {0, 8, 0, 8, 3, 11, 3, 11, 6, 14, 6, 14, 5, 13, 5, 13};
		final int[] n3 = {0, 0, 3, 3, 2, 2, 1, 1, 4, 4, 7, 7, 6, 6, 5, 5};
		final int[] n4 = {0, 11, 12, 7, 15, 4, 3, 8, 14, 5, 2, 9, 1, 10, 13, 6};
		final int[] n5 = {0, 5, 1, 4, 6, 3, 7, 2, 12, 9, 13, 8, 10, 15, 11, 14};
		final int[] n6 = {0, 14, 4, 10, 11, 5, 15, 1, 6, 8, 2, 12, 13, 3, 9, 7};
		final int[] n7 = {0, 9, 0, 9, 5, 12, 5, 12, 10, 3, 10, 3, 15, 6, 15, 6};
		final int[] n8 = {0, 5, 11, 14, 2, 7, 9, 12, 12, 9, 7, 2, 14, 11, 5, 0};
		final int[] n9 = {0, 0, 0, 0, 4, 4, 4, 4, 0, 0, 0, 0, 4, 4, 4, 4};
		final int[] n10 = {0, 8, 1, 9, 3, 11, 2, 10, 5, 13, 4, 12, 6, 14, 7, 15};
		final int[] n11 = {0, 14, 13, 3, 9, 7, 4, 10, 6, 8, 11, 5, 15, 1, 2, 12};
		final int[] n12 = {0, 13, 10, 7, 4, 9, 14, 3, 10, 7, 0, 13, 14, 3, 4, 9};
		final int[] n13 = {0, 1, 3, 2, 6, 7, 5, 4, 15, 14, 12, 13, 9, 8, 10, 11};
		final int[] n14 = {0, 1, 3, 2, 4, 5, 7, 6, 12, 13, 15, 14, 8, 9, 11, 10};
		final int[] n15 = {0, 6, 12, 10, 9, 15, 5, 3, 2, 4, 14, 8, 11, 13, 7, 1};
		final int[] n16 = {0, 11, 6, 13, 13, 6, 11, 0, 11, 0, 13, 6, 6, 13, 0, 11};
		final int[] n17 = {0, 12, 8, 4, 1, 13, 9, 5, 3, 15, 11, 7, 2, 14, 10, 6};
		final int[] n18 = {0, 12, 9, 5, 2, 14, 11, 7, 5, 9, 12, 0, 7, 11, 14, 2};
		final int[] n19 = {0, 6, 13, 11, 10, 12, 7, 1, 5, 3, 8, 14, 15, 9, 2, 4};
		final int[] n20 = {0, 9, 3, 10, 7, 14, 4, 13, 14, 7, 13, 4, 9, 0, 10, 3};
		final int[] n21 = {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15};
		final int[] n22 = {0, 1, 2, 3, 5, 4, 7, 6, 11, 10, 9, 8, 14, 15, 12, 13};
		final int[] n23 = {0, 7, 15, 8, 14, 9, 1, 6, 12, 11, 3, 4, 2, 5, 13, 10};
		final int[] n24 = {0, 5, 10, 15, 4, 1, 14, 11, 8, 13, 2, 7, 12, 9, 6, 3};
		final int[] n25 = {0, 11, 6, 13, 13, 6, 11, 0, 10, 1, 12, 7, 7, 12, 1, 10};
		final int[] n26 = {0, 13, 10, 7, 4, 9, 14, 3, 8, 5, 2, 15, 12, 1, 6, 11};
		final int[] n27 = {0, 4, 9, 13, 2, 6, 11, 15, 5, 1, 12, 8, 7, 3, 14, 10};
		final int[] n28 = {0, 14, 12, 2, 8, 6, 4, 10, 0, 14, 12, 2, 8, 6, 4, 10};
		final int[] n29 = {0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
		final int[] n30 = {0, 15, 14, 1, 12, 3, 2, 13, 8, 7, 6, 9, 4, 11, 10, 5};
		final int[] n31 = {0, 10, 4, 14, 9, 3, 13, 7, 2, 8, 6, 12, 11, 1, 15, 5};
		final int[] n32 = {0, 10, 5, 15, 11, 1, 14, 4, 6, 12, 3, 9, 13, 7, 8, 2};
		final int[] n33 = {0, 4, 9, 13, 3, 7, 10, 14, 7, 3, 14, 10, 4, 0, 13, 9};
		final int[] key = {30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 61, 62, 63, 64, 65, 66};
		final char[] ssid = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

		int[] mac = new int[12];
		for (int i = 0; i < 12; ++i) 
		{
			mac[i] = Integer.parseInt(bssid.get_mac("",true).substring(i, i + 1), 16);
		}
		int s1 = (n1[mac[0]]) ^ (a4[mac[1]]) ^ (a6[mac[2]]) ^ (a1[mac[3]])
				^ (a11[mac[4]]) ^ (n20[mac[5]]) ^ (a10[mac[6]]) ^ (a4[mac[7]])
				^ (a8[mac[8]]) ^ (a2[mac[9]]) ^ (a5[mac[10]]) ^ (a9[mac[11]])
				^ 5;
		int s2 = (n2[mac[0]]) ^ (n8[mac[1]]) ^ (n15[mac[2]]) ^ (n17[mac[3]])
				^ (a12[mac[4]]) ^ (n21[mac[5]]) ^ (n24[mac[6]]) ^ (a9[mac[7]])
				^ (n27[mac[8]]) ^ (n29[mac[9]]) ^ (a11[mac[10]])
				^ (n32[mac[11]]) ^ 10;
		int s3 = (n3[mac[0]]) ^ (n9[mac[1]]) ^ (a5[mac[2]]) ^ (a9[mac[3]])
				^ (n19[mac[4]]) ^ (n22[mac[5]]) ^ (a12[mac[6]]) ^ (n25[mac[7]])
				^ (a11[mac[8]]) ^ (a13[mac[9]]) ^ (n30[mac[10]])
				^ (n33[mac[11]]) ^ 11;
		int s4 = (n4[mac[0]]) ^ (n10[mac[1]]) ^ (n16[mac[2]]) ^ (n18[mac[3]])
				^ (a13[mac[4]]) ^ (n23[mac[5]]) ^ (a1[mac[6]]) ^ (n26[mac[7]])
				^ (n28[mac[8]]) ^ (a3[mac[9]]) ^ (a6[mac[10]]) ^ (a0[mac[11]])
				^ 10;
		String ssidFinal = Character.toString(ssid[s1])
				+ Character.toString(ssid[s2]) + Character.toString(ssid[s3])
				+ Character.toString(ssid[s4]);
		int ya = (a2[mac[0]]) ^ (n11[mac[1]]) ^ (a7[mac[2]]) ^ (a8[mac[3]])
				^ (a14[mac[4]]) ^ (a5[mac[5]]) ^ (a5[mac[6]]) ^ (a2[mac[7]])
				^ (a0[mac[8]]) ^ (a1[mac[9]]) ^ (a15[mac[10]]) ^ (a0[mac[11]])
				^ 13;
		int yb = (n5[mac[0]]) ^ (n12[mac[1]]) ^ (a5[mac[2]]) ^ (a7[mac[3]])
				^ (a2[mac[4]]) ^ (a14[mac[5]]) ^ (a1[mac[6]]) ^ (a5[mac[7]])
				^ (a0[mac[8]]) ^ (a0[mac[9]]) ^ (n31[mac[10]]) ^ (a15[mac[11]])
				^ 4;
		int yc = (a3[mac[0]]) ^ (a5[mac[1]]) ^ (a2[mac[2]]) ^ (a10[mac[3]])
				^ (a7[mac[4]]) ^ (a8[mac[5]]) ^ (a14[mac[6]]) ^ (a5[mac[7]])
				^ (a5[mac[8]]) ^ (a2[mac[9]]) ^ (a0[mac[10]]) ^ (a1[mac[11]])
				^ 7;
		int yd = (n6[mac[0]]) ^ (n13[mac[1]]) ^ (a8[mac[2]]) ^ (a2[mac[3]])
				^ (a5[mac[4]]) ^ (a7[mac[5]]) ^ (a2[mac[6]]) ^ (a14[mac[7]])
				^ (a1[mac[8]]) ^ (a5[mac[9]]) ^ (a0[mac[10]]) ^ (a0[mac[11]])
				^ 14;
		int ye = (n7[mac[0]]) ^ (n14[mac[1]]) ^ (a3[mac[2]]) ^ (a5[mac[3]])
				^ (a2[mac[4]]) ^ (a10[mac[5]]) ^ (a7[mac[6]]) ^ (a8[mac[7]])
				^ (a14[mac[8]]) ^ (a5[mac[9]]) ^ (a5[mac[10]]) ^ (a2[mac[11]])
				^ 7;
		result.add(Integer.toString(key[ya]) + Integer.toString(key[yb]) + Integer.toString(key[yc]) + Integer.toString(key[yd]) + Integer.toString(key[ye]));
		return new PasswordDictionary("HuaweiKeygen",result,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				ComtrendKeygen(String essid,Mac bssid)
	{
		SUUUUU! corregir
		ArrayList<String> result=new ArrayList<String>();
		final String	magic		= "bcgbghgg";
		final String	lowermagic	= "64680C";
		final String	highermagic	= "3872C0";
		final String	mac001a2b	= "001A2B";
	
		final String ssidIdentifier = essid.substring(essid.length() - 4);
		//TODO! abstraer md5
		MessageDigest md;
		try 
		{
			md = MessageDigest.getInstance("MD5");
		} 
		catch (NoSuchAlgorithmException e1) 
		{
			return null;
		}
		final String mac = bssid.get_mac("",true);
		try 
		{
			if (mac.substring(0, 6).equalsIgnoreCase(mac001a2b)) 
			{
				for (int i = 0; i < 512; i++) 
				{
					md.reset();
					md.update(magic.getBytes("ASCII"));
					String xx;
					String yy;
					if (i < 256) 
					{
						md.update(lowermagic.getBytes("ASCII"));
						xx = Integer.toHexString(i).toUpperCase(Locale.US);
					} 
					else 
					{
						md.update(highermagic.getBytes("ASCII"));
						xx = Integer.toHexString(i - 256).toUpperCase(Locale.US);
					}
					while (xx.length() < 2)
					{
						xx = "0" + xx;
					}
					md.update(xx.getBytes("ASCII"));
                    md.update(ssidIdentifier.getBytes("ASCII"));
					md.update(mac.getBytes("ASCII"));
					byte[] hash = md.digest();
					result.add(StringUtils.getHexString(hash).substring(0, 20));
					String foo=StringUtils.getHexString(hash).substring(0, 20);
					String bar=null;
					if(i<256)
					{
						yy = Integer.toHexString(i).toUpperCase(Locale.US);
					}
					else
					{
						yy = Integer.toHexString(i - 256).toUpperCase(Locale.US);
					}
					while (yy.length() < 2)
					{
						yy = "0" + yy;
					}
					if(i<256)
					{
						bar=StringUtils.md5(StringUtils.concat_byte_arrays(lowermagic.getBytes("ASCII"),yy.getBytes("ASCII"),ssidIdentifier.getBytes("ASCII"),mac.getBytes("ASCII")));
					}
					else
					{
						bar=StringUtils.md5(StringUtils.concat_byte_arrays(highermagic.getBytes("ASCII"),yy.getBytes("ASCII"),ssidIdentifier.getBytes("ASCII"),mac.getBytes("ASCII")));
					}
					System.out.println(foo);
					System.out.println(bar.substring(0,20));
				}
			} 
			else 
			{
				final String macMod = mac.substring(0, 8) + ssidIdentifier;
				result.add(StringUtils.md5(StringUtils.concat_byte_arrays(	magic.getBytes("ASCII")
																			,macMod.toUpperCase(Locale.getDefault()).getBytes("ASCII")
																			,mac.toUpperCase(Locale.getDefault()).getBytes("ASCII")
																		)
											).substring(0,20));
			}
			return new PasswordDictionary("ComtrendKeygen",result,"");
		} 
		catch (UnsupportedEncodingException e) 
		{
		}
		catch(NoSuchAlgorithmException e)
		{
		}
		return null;
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				ArcadyanKeygen(String essid,Mac bssid)
	{
		ArrayList<String> result=new ArrayList<String>();
		
		String bssid_string=bssid.get_mac("",true);
		String C1 = Integer.toString(Integer.parseInt(bssid_string.substring(8), 16));

		while (C1.length() < 5)
		{
			C1 = "0" + C1;
		}

		final char S7 = C1.charAt(1);
		final char S8 = C1.charAt(2);
		final char S9 = C1.charAt(3);
		final char S10 = C1.charAt(4);
		final char M9 = bssid_string.charAt(8);
		final char M10 = bssid_string.charAt(9);
		final char M11 = bssid_string.charAt(10);
		final char M12 = bssid_string.charAt(11);

		final String tmpK1 = Integer.toHexString(Character.digit(S7, 16)
				+ Character.digit(S8, 16) + Character.digit(M11, 16)
				+ Character.digit(M12, 16));
		final String tmpK2 = Integer.toHexString(Character.digit(M9, 16)
				+ Character.digit(M10, 16) + Character.digit(S9, 16)
				+ Character.digit(S10, 16));

		final char K1 = tmpK1.charAt(tmpK1.length() - 1);
		final char K2 = tmpK2.charAt(tmpK2.length() - 1);

		final String X1 = Integer.toHexString(Character.digit(K1, 16)
				^ Character.digit(S10, 16));
		final String X2 = Integer.toHexString(Character.digit(K1, 16)
				^ Character.digit(S9, 16));
		final String X3 = Integer.toHexString(Character.digit(K1, 16)
				^ Character.digit(S8, 16));
		final String Y1 = Integer.toHexString(Character.digit(K2, 16)
				^ Character.digit(M10, 16));
		final String Y2 = Integer.toHexString(Character.digit(K2, 16)
				^ Character.digit(M11, 16));
		final String Y3 = Integer.toHexString(Character.digit(K2, 16)
				^ Character.digit(M12, 16));
		final String Z1 = Integer.toHexString(Character.digit(M11, 16)
				^ Character.digit(S10, 16));
		final String Z2 = Integer.toHexString(Character.digit(M12, 16)
				^ Character.digit(S9, 16));
		final String Z3 = Integer.toHexString(Character.digit(K1, 16)
				^ Character.digit(K2, 16));

		final String wpaKey = X1 + Y1 + Z1 + X2 + Y2 + Z2 + X3 + Y3 + Z3;
		result.add(wpaKey.toUpperCase(Locale.getDefault()));
		
		if (wpaKey.indexOf('0') != -1) 
		{
			result.add(wpaKey.replace("0", "1").toUpperCase(Locale.getDefault()));
		}
		return new PasswordDictionary("ArcadyanKeygen",result,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				ConnKeygen(String essid,Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		words.add(bssid.get_mac("",false));
		words.add("1234567890123");
		return new PasswordDictionary("ConnKeygen",words,""); 
	}	
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				ArnetPirelliKeygen(String essid,Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		
		final String LOOKUP = "0123456789abcdefghijklmnopqrstuvwxyz";
		final byte ALICE_SEED[/* 32 */] = {0x64, (byte) 0xC6,
				(byte) 0xDD, (byte) 0xE3, (byte) 0xE5, 0x79, (byte) 0xB6,
				(byte) 0xD9, (byte) 0x86, (byte) 0x96, (byte) 0x8D, 0x34, 0x45,
				(byte) 0xD2, 0x3B, 0x15, (byte) 0xCA, (byte) 0xAF, 0x12,
				(byte) 0x84, 0x02, (byte) 0xAC, 0x56, 0x00, 0x05, (byte) 0xCE,
				0x20, 0x75, (byte) 0x91, 0x3F, (byte) 0xDC, (byte) 0xE8};
		Mac bssid_next_one=null;
		try {bssid_next_one=new Mac(bssid.toInteger()+1);} catch (UnparseableMacException e1) {} //will never happen
		int length=10;
		
		String mac=bssid_next_one.get_mac("",true);
		byte[] macBytes = new byte[6];
		for(int i=0; i<12; i+=2) 
		{
			macBytes[i / 2] = (byte) ((Character.digit(mac.charAt(i), 16) << 4) + Character.digit(mac.charAt(i + 1), 16));
		}
		byte[] hash=null;
		try 
		{
			hash=StringUtils.sha256(StringUtils.concat_byte_arrays(ALICE_SEED,new String("1236790").getBytes("UTF-8"),macBytes));
		} 
		catch (NoSuchAlgorithmException e)		{	return new PasswordDictionary("ArnetPirelliKeygen",words,"NoSuchAlgorithmException:SHA-256");	}
		catch (UnsupportedEncodingException e)	{	return new PasswordDictionary("ArnetPirelliKeygen",words,"UnsupportedEncodingException:UTF-8");	}
		final StringBuilder key = new StringBuilder();
		for (int i = 0; i < length; ++i) 
		{
			key.append(LOOKUP.charAt((hash[i] & 0xFF) % LOOKUP.length()));
		}
		words.add(key.toString());
		return new PasswordDictionary("ArnetPirelliKeygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				MeoPirelliKeygen(String essid,Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		
		final String LOOKUP = "0123456789abcdefghijklmnopqrstuvwxyz";
		final byte ALICE_SEED[/* 32 */] = {0x64, (byte) 0xC6,
				(byte) 0xDD, (byte) 0xE3, (byte) 0xE5, 0x79, (byte) 0xB6,
				(byte) 0xD9, (byte) 0x86, (byte) 0x96, (byte) 0x8D, 0x34, 0x45,
				(byte) 0xD2, 0x3B, 0x15, (byte) 0xCA, (byte) 0xAF, 0x12,
				(byte) 0x84, 0x02, (byte) 0xAC, 0x56, 0x00, 0x05, (byte) 0xCE,
				0x20, 0x75, (byte) 0x91, 0x3F, (byte) 0xDC, (byte) 0xE8};
		Mac bssid_previous_one=null;
		try {bssid_previous_one=new Mac(bssid.toInteger()-1);} catch (UnparseableMacException e1) {} //will never happen
		int length=8;
		
		String mac=bssid_previous_one.get_mac("",true);
		byte[] macBytes = new byte[6];
		for(int i=0; i<12; i+=2) 
		{
			macBytes[i / 2] = (byte) ((Character.digit(mac.charAt(i), 16) << 4) + Character.digit(mac.charAt(i + 1), 16));
		}
		byte[] hash=null;
		try 
		{
			hash=StringUtils.sha256(StringUtils.concat_byte_arrays(ALICE_SEED,new String("1236790").getBytes("UTF-8"),macBytes));
		} 
		catch (NoSuchAlgorithmException e)		{	return new PasswordDictionary("MeoPirelliKeygen",words,"NoSuchAlgorithmException:SHA-256");		} 
		catch (UnsupportedEncodingException e)	{	return new PasswordDictionary("MeoPirelliKeygen",words,"UnsupportedEncodingException:UTF-8");	} 
		final StringBuilder key = new StringBuilder();
		for (int i = 0; i < length; ++i) 
		{
			key.append(LOOKUP.charAt((hash[i] & 0xFF) % LOOKUP.length()));
		}
		words.add(key.toString());
		return new PasswordDictionary("ArnetPirelliKeygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				PBSKeygen(String essid,Mac bssid)
	{
		final byte[] saltSHA256 = {0x54, 0x45, 0x4F, 0x74, 0x65, 0x6C,
				(byte) 0xB6, (byte) 0xD9, (byte) 0x86, (byte) 0x96, (byte) 0x8D,
				0x34, 0x45, (byte) 0xD2, 0x3B, 0x15, (byte) 0xCA, (byte) 0xAF,
				0x12, (byte) 0x84, 0x02, (byte) 0xAC, 0x56, 0x00, 0x05,
				(byte) 0xCE, 0x20, 0x75, (byte) 0x94, 0x3F, (byte) 0xDC,
				(byte) 0xE8};
		final String lookup = "0123456789ABCDEFGHIKJLMNOPQRSTUVWXYZabcdefghikjlmnopqrstuvwxyz";

		ArrayList<String> words=new ArrayList<String>();
		String mac = bssid.get_mac("",true);
		byte[] macHex = new byte[6];
		for (int i = 0; i < 12; i += 2)
		{
			macHex[i / 2] = (byte) ((Character.digit(mac.charAt(i), 16) << 4) + Character.digit(mac.charAt(i + 1), 16));
		}
		macHex[5] -= 5;
		byte[] hash=null;
		try 
		{
			hash = StringUtils.sha256(StringUtils.concat_byte_arrays(saltSHA256,macHex));
		}
		catch (NoSuchAlgorithmException e) 
		{
			return new PasswordDictionary("PBSKeygen",words,"NoSuchAlgorithmException:SHA-256");
		}
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < 13; ++i) 
		{
			key.append(lookup.charAt((hash[i] >= 0 ? hash[i] : 256 + hash[i])% lookup.length()));
		}
		words.add(key.toString());
		return new PasswordDictionary("PBSKeygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				Wlan6Keygen(String essid,Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		String macStr = bssid.get_mac("",true);
		char[] ssidSubPart = {'1', '2', '3', '4', '5', '6'};/*These values are not revelant.*/
		char[] bssidLastByte = {'6', '6'};
		String ssidIdentifier=essid.substring(essid.length()-6);
		ssidSubPart[0] = ssidIdentifier.charAt(0);
		ssidSubPart[1] = ssidIdentifier.charAt(1);
		ssidSubPart[2] = ssidIdentifier.charAt(2);
		ssidSubPart[3] = ssidIdentifier.charAt(3);
		ssidSubPart[4] = ssidIdentifier.charAt(4);
		ssidSubPart[5] = ssidIdentifier.charAt(5);
		bssidLastByte[0] = macStr.charAt(10);
		bssidLastByte[1] = macStr.charAt(11);
		for (int k = 0; k < 6; ++k)
		{
			if (ssidSubPart[k] >= 'A')
			{
				ssidSubPart[k] = (char) (ssidSubPart[k] - 55);
			}
		}
		if (bssidLastByte[0] >= 'A')
		{
			bssidLastByte[0] = (char) (bssidLastByte[0] - 55);
		}
		if (bssidLastByte[1] >= 'A')
		{
			bssidLastByte[1] = (char) (bssidLastByte[1] - 55);
		}

		for (int i = 0; i < 10; ++i) 
		{
			/*Do not change the order of this instructions*/
			int aux = i + (ssidSubPart[3] & 0xf) + (bssidLastByte[0] & 0xf) + (bssidLastByte[1] & 0xf);
			int aux1 = (ssidSubPart[1] & 0xf) + (ssidSubPart[2] & 0xf) + (ssidSubPart[4] & 0xf) + (ssidSubPart[5] & 0xf);
			int second = aux ^ (ssidSubPart[5] & 0xf);
			int sixth = aux ^ (ssidSubPart[4] & 0xf);
			int tenth = aux ^ (ssidSubPart[3] & 0xf);
			int third = aux1 ^ (ssidSubPart[2] & 0xf);
			int seventh = aux1 ^ (bssidLastByte[0] & 0xf);
			int eleventh = aux1 ^ (bssidLastByte[1] & 0xf);
			int fourth = (bssidLastByte[0] & 0xf) ^ (ssidSubPart[5] & 0xf);
			int eighth = (bssidLastByte[1] & 0xf) ^ (ssidSubPart[4] & 0xf);
			int twelfth = aux ^ aux1;
			int fifth = second ^ eighth;
			int ninth = seventh ^ eleventh;
			int thirteenth = third ^ tenth;
			int first = twelfth ^ sixth;
			String key = Integer.toHexString(first & 0xf) + Integer.toHexString(second & 0xf) +
						Integer.toHexString(third & 0xf) + Integer.toHexString(fourth & 0xf) +
						Integer.toHexString(fifth & 0xf) + Integer.toHexString(sixth & 0xf) +
						Integer.toHexString(seventh & 0xf) + Integer.toHexString(eighth & 0xf) +
						Integer.toHexString(ninth & 0xf) + Integer.toHexString(tenth & 0xf) +
						Integer.toHexString(eleventh & 0xf) + Integer.toHexString(twelfth & 0xf) +
						Integer.toHexString(thirteenth & 0xf);
			words.add(key.toUpperCase());
		}
		if (((ssidSubPart[0] != macStr.charAt(7)) || (ssidSubPart[1] != macStr.charAt(8)) || (ssidSubPart[2] != macStr.charAt(9))) && essid.startsWith("WiFi")) 
		{
			return new PasswordDictionary("Wlan6Keygen",new ArrayList<String>(),"Essid doesn't match");
		}
		return new PasswordDictionary("Wlan6Keygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				BelkinKeygen(String essid,Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		final int[][] ORDERS = {{6, 2, 3, 8, 5, 1, 7, 4} , {1, 2, 3, 8, 5, 1, 7, 4} , {1, 2, 3, 8, 5, 6, 7, 4}};
		final String[] CHARSETS = {"024613578ACE9BDF" , "944626378ace9bdf"};
		
		String mac = bssid.get_mac("",true);
		Mac bssid_next_one=null; try{bssid_next_one=new Mac(bssid.toInteger()+1);}catch(UnparseableMacException e){}
		String bssid_next_one_string=bssid_next_one.get_mac("",true);
		Mac bssid_next_two=null; try{bssid_next_two=new Mac(bssid.toInteger()+2);}catch(UnparseableMacException e){}
		String bssid_next_two_string=bssid_next_two.get_mac("",true);
		if(essid.startsWith("B")) 
		{
			//generateKey(mac.substring(4), CHARSETS[0], ORDERS[0]);
			String mac_aux=mac.substring(4);
			StringBuilder key = new StringBuilder();
			for(int i=0; i<mac_aux.length(); ++i) 
			{
				String k = mac_aux.substring(ORDERS[0][i] - 1, ORDERS[0][i]);
				key.append(CHARSETS[0].charAt(Integer.parseInt(k, 16)));
			}
			words.add(key.toString());
		} 
		else if(essid.startsWith("b")) 
		{
			//generateKey(mac+1.substring(4), CHARSETS[1], ORDERS[0]);
			if(!mac.startsWith("944452")) 
			{
				String mac_aux=null;
				StringBuilder key=null;
				//generateKey(mac+1.substring(4), CHARSETS[1], ORDERS[2]);
				mac_aux=bssid_next_one_string.substring(4);
				key = new StringBuilder();
				for(int i=0; i<mac_aux.length(); ++i) 
				{
					String k = mac_aux.substring(ORDERS[2][i] - 1, ORDERS[2][i]);
					key.append(CHARSETS[1].charAt(Integer.parseInt(k, 16)));
				}
				words.add(key.toString());
				
				//generateKey(mac+2.substring(4), CHARSETS[1], ORDERS[0]);
				mac_aux=bssid_next_two_string.substring(4);
				key = new StringBuilder();
				for(int i=0; i<mac_aux.length(); ++i) 
				{
					String k = mac_aux.substring(ORDERS[0][i] - 1, ORDERS[0][i]);
					key.append(CHARSETS[1].charAt(Integer.parseInt(k, 16)));
				}
				words.add(key.toString());
			}
		} 
		else 
		{
			//Bruteforcing
			String current_mac=bssid.get_mac("",true);
			for(int i=0; i<3; ++i) 
			{
				for(int[] ORDER : ORDERS) 
				{
					String mac_aux=null;
					StringBuilder key=null;
					
					//generateKey(current_mac.substring(4), CHARSETS[0], ORDER);
					mac_aux=current_mac.substring(4);
					key = new StringBuilder();
					for(int j=0; j<mac_aux.length(); ++j) 
					{
						String k = mac_aux.substring(ORDER[j] - 1, ORDER[j]);
						key.append(CHARSETS[0].charAt(Integer.parseInt(k, 16)));
					}
					words.add(key.toString());
					
					//generateKey(current_mac.substring(4), CHARSETS[1], ORDER);
					mac_aux=current_mac.substring(4);
					key = new StringBuilder();
					for(int j=0; j<mac_aux.length(); ++j) 
					{
						String k = mac_aux.substring(ORDER[j] - 1, ORDER[j]);
						key.append(CHARSETS[1].charAt(Integer.parseInt(k, 16)));
					}
					words.add(key.toString());
				}
				try {current_mac=new Mac(new Mac(current_mac).toInteger()+1).get_mac("",true);} catch (UnparseableMacException e) {} //will never happen
			}
		}
		return new PasswordDictionary("BelkinKeygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				ZyxelKeygen(String essid,Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		String mac=bssid.get_mac("",true);
		final String macMod = mac.substring(0, 8) + essid.substring(essid.length()-4);
		try 
		{
			words.add(StringUtils.md5(macMod.toLowerCase()).substring(0,20).toUpperCase());
		} 
		catch (NoSuchAlgorithmException e) 
		{
			return new PasswordDictionary("ZyxelKeygen",words,"NoSuchAlgorithmException:md5");
		}
		return new PasswordDictionary("ZyxelKeygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				HG824xKeygen(String essid,Mac bssid)
	{
		ArrayList<String> words=new ArrayList<String>();
		final String mac = bssid.get_mac("",true);
		final StringBuilder wpaPassword = new StringBuilder();
		wpaPassword.append(mac.substring(6,8));
		final int lastPair = Integer.parseInt(mac.substring(10), 16);
		if ( lastPair <= 8 ) 
		{
			int fifthPair = (Integer.parseInt(mac.substring(8, 10), 16) - 1) & 0xFF;
			wpaPassword.append(Integer.toString(fifthPair, 16));
		} 
		else 
		{
			wpaPassword.append(mac.substring(8,10));
		}
		final int lastChar = Integer.parseInt(mac.substring(11), 16);
		if ( lastChar <= 8 ) 
		{
			final int nextPart = (Integer.parseInt(mac.substring(10,11), 16)-1) & 0xF;
			wpaPassword.append(Integer.toString(nextPart, 16));
		} 
		else 
		{
			wpaPassword.append(mac.substring(10,11));
		}
		switch (lastChar) 
		{
			case 8:		wpaPassword.append("F");	break;
			case 9:		wpaPassword.append("0");	break;
			case 0xA:	wpaPassword.append("1");	break;
			case 0xB:	wpaPassword.append("2");	break;
			case 0xC:	wpaPassword.append("3");	break;
			case 0xD:	wpaPassword.append("4");	break;
			case 0xE:	wpaPassword.append("5");	break;
			case 0xF:	wpaPassword.append("6");	break;
			case 0:		wpaPassword.append("7");	break;
			case 1:		wpaPassword.append("8");	break;
			case 2:		wpaPassword.append("9");	break;
			case 3:		wpaPassword.append("A");	break;
			case 4:		wpaPassword.append("B");	break;
			case 5:		wpaPassword.append("C");	break;
			case 6:		wpaPassword.append("D");	break;
			case 7:		wpaPassword.append("E");	break;
			default:	return new PasswordDictionary("HG824xKeygen",words,"Unexpected char at first case:"+lastChar);
		}
		switch (mac.substring(0,2)) 
		{
			case "28":	wpaPassword.append("03");	break;
			case "08":	wpaPassword.append("05");	break;
			case "80":	wpaPassword.append("06");	break;
			case "E0":	wpaPassword.append("0C");	break;
			case "00":	wpaPassword.append("0D");	break;
			case "10":	wpaPassword.append("0E");	break;
			case "CC":	wpaPassword.append("12");	break;
			case "D4":	wpaPassword.append("35");	break;
			case "AC":	wpaPassword.append("1A");	break;
			case "20":	wpaPassword.append("1F");	break;
			case "70":	wpaPassword.append("20");	break;
			case "F8":	wpaPassword.append("21");	break;
			case "48":	wpaPassword.append("24");	break;
			default:	return new PasswordDictionary("HG824xKeygen",words,"Unexpected string at second case:"+mac.substring(0,2));
		}
		words.add(wpaPassword.toString().toUpperCase());
		return new PasswordDictionary("HG824xKeygen",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				Pirelli_Discuss_DRG_A225(String essid)
	{
		//pulwifi core
		ArrayList<String> words = new ArrayList<String>();
		words.add("YW0"+Integer.toString((Integer.parseInt(essid,16) - 0xD0EC31) >> 2));
		return new PasswordDictionary("Pirelli_Discuss_DRG_A225",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				Dlink(Mac bssid)
	{
		//pulwifi core
		ArrayList<String> words = new ArrayList<String>();
		// Delete dots from bssid and use caps only...
		String bssid_data = bssid.get_mac("",true);

		// Select inportant data from bssid...
		char[] data = new char[20];
		data[0] = bssid_data.charAt(11);
		data[1] = bssid_data.charAt(0);
		data[2] = bssid_data.charAt(10);
		data[3] = bssid_data.charAt(1);
		data[4] = bssid_data.charAt(9);
		data[5] = bssid_data.charAt(2);
		data[6] = bssid_data.charAt(8);
		data[7] = bssid_data.charAt(3);
		data[8] = bssid_data.charAt(7);
		data[9] = bssid_data.charAt(4);
		data[10] = bssid_data.charAt(6);
		data[11] = bssid_data.charAt(5);
		data[12] = bssid_data.charAt(1);
		data[13] = bssid_data.charAt(6);
		data[14] = bssid_data.charAt(8);
		data[15] = bssid_data.charAt(9);
		data[16] = bssid_data.charAt(11);
		data[17] = bssid_data.charAt(2);
		data[18] = bssid_data.charAt(4);
		data[19] = bssid_data.charAt(10);

		// Process key throught the real algorithm...
		char[] key = new char[20];
		char hash[] = { 'X', 'r', 'q', 'a', 'H', 'N', 'p', 'd', 'S', 'Y', 'w','8', '6', '2', '1', '5' };
		int index = 0;
		for (int i = 0; i < 20; i++) 
		{
			if ((data[i] >= '0') && (data[i] <= '9'))		{	index = data[i] - '0';								}
			else if ((data[i] >= 'A') && (data[i] <= 'F'))	{	index = data[i] - 'A' + 10;							}
			else											{	return new PasswordDictionary("",words,"error!");	}	//there was an error
			key[i] = hash[index];
		}

		// Return the key...
		words.add(String.valueOf(key, 0, 20));
		return new PasswordDictionary("Dlink",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				Eircom(Mac bssid)
	{
		String mac = bssid.get_mac("",false);
		byte[] routerMAC = new byte[4];
		routerMAC[0] = 1;
		for(int i = 0; i < 6; i += 2)
		{
			routerMAC[i / 2 + 1] = (byte) ((Character.digit(mac.charAt(i), 16) << 4) + Character.digit(mac.charAt(i + 1), 16));
		}
		int macDec = ((0xFF & routerMAC[0]) << 24) | ((0xFF & routerMAC[1]) << 16) | ((0xFF & routerMAC[2]) << 8) | (0xFF & routerMAC[3]);
		mac = Wifi_password_engine.dectoString(macDec) + "Although your world wonders me, ";
		ArrayList<String> words=new ArrayList<String>();
		try
		{
			//TODO! esto debería revisarlo
			byte[] hash=StringUtils.sha1(mac.getBytes()).getBytes();
			words.add(StringUtils.getHexString(hash).substring(0,26));
		}
		catch(NoSuchAlgorithmException e)
		{
			return new PasswordDictionary("Eircom",words,"NoSuchAlgorithmException: sha1");
		}
		catch(UnsupportedEncodingException e)
		{
			return new PasswordDictionary("Eircom",words,"UnsupportedEncodingException");
		}
		return new PasswordDictionary("Eircom",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private static String							dectoString(int mac) 
	{
		//used in Eircom
		String ret = "";
		while(mac > 0) 
		{
			switch(mac % 10) 
			{
				case 0:		ret = "Zero" + ret;		break;
				case 1:		ret = "One" + ret;		break;
				case 2:		ret = "Two" + ret;		break;
				case 3:		ret = "Three" + ret;	break;
				case 4:		ret = "Four" + ret;		break;
				case 5:		ret = "Five" + ret;		break;
				case 6:		ret = "Six" + ret;		break;
				case 7:		ret = "Seven" + ret;	break;
				case 8:		ret = "Eight" + ret;	break;
				case 9:		ret = "Nine" + ret;		break;
			}
			mac /= 10;
		}
		return ret;
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public static PasswordDictionary				Infostrada(Mac bssid)
	{
		ArrayList<String> words = new ArrayList<String>();
		// Delete dots from bssid and use caps only...
		String bssid_data = bssid.get_mac("",true);
		words.add(2 + bssid_data);
		return new PasswordDictionary("Infostrada",words,"");		
	}
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//FROM MAC TO PASSWORDS--------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	public static ArrayList<PasswordDictionary>		from_router_to_passwords(Mac bssid,String essid) throws NoSuchAlgorithmException
	{
		String mac=bssid.get_mac(":",true);
		
		ArrayList<PasswordDictionary> result=new ArrayList<PasswordDictionary>();

		if(essid.matches("Discus--([0-9a-fA-F]{6})"))
		{
			result.add(Wifi_password_engine.Pirelli_Discuss_DRG_A225(essid));
		}
		if(essid.matches("DLink-([0-9a-fA-F]{6})"))
		{
			result.add(Wifi_password_engine.Dlink(bssid));
		}
        if(essid.matches("[eE]ircom[0-7]{4} ?[0-7]{4}") || mac.startsWith("00:0F:CC")	) 
        {
        	result.add(Wifi_password_engine.Eircom(bssid));
        }
        if (essid.matches("InfostradaWiFi-[0-9a-zA-Z]{6}"))
        {
			result.add(Wifi_password_engine.Infostrada(bssid));
        }
		if(mac.startsWith("9C:41:7C"))
		{
			result.add(Wifi_password_engine.ZTE_W5(bssid));
		}
		
		if(			(mac.startsWith("64:68:0C") && essid.matches("WLAN_([0-9a-fA-F]{4})")				)
				||	(mac.startsWith("00:1D:20") && essid.matches("(?:WLAN|JAZZTEL)_([0-9a-fA-F]{4})")	)
				||	(mac.startsWith("00:1B:20") && essid.matches("(?:WLAN|JAZZTEL)_([0-9a-fA-F]{4})")	)
				||	(mac.startsWith("00:23:F8") && essid.matches("(?:WLAN|JAZZTEL)_([0-9a-fA-F]{4})")	)
				||	(mac.startsWith("38:72:C0") && essid.matches("WLAN_([0-9a-fA-F]{4})")				)
				||	(mac.startsWith("30:39:F2") && essid.matches("(?:WLAN|JAZZTEL)_([0-9a-fA-F]{4})")	)
			)
		{
			result.add(Wifi_password_engine.comtrend_ct_536(bssid,essid));
		}
		else if(	mac.startsWith("64:68:0C") || mac.startsWith("00:1D:20") || mac.startsWith("00:1B:20") 
				||	mac.startsWith("00:23:F8") || mac.startsWith("38:72:C0") || mac.startsWith("30:39:F2") 
				)
		{
			result.add(Wifi_password_engine.comtrend_ct_536(bssid));
		}
					 
		if(			mac.startsWith("00:1F:A4"))
		{
			result.add(Wifi_password_engine.zyxel_P660_HW_B1A(bssid, essid));
		}
		
		if(			mac.startsWith("00:13:F7"))
		{
			result.add(Wifi_password_engine.SMC_7908_AISP_SMC_7908VoWBRA(bssid,essid));
		}
		
		if(			mac.startsWith("74:31:70")	||	mac.startsWith("84:9C:A6")	||	mac.startsWith("88:03:55")
				||	mac.startsWith("1C:C6:3C")	||	mac.startsWith("50:7E:5D")	||	mac.startsWith("00:12:BF"))
		{
			result.add(Wifi_password_engine.get_easybox_password(bssid));
			result.add(Wifi_password_engine.vodafone_arcadyan_spain(bssid));
		}
		
		if(			mac.startsWith("00:14:7F")	||	mac.startsWith("00:90:D0") )
		{
			result.add(Wifi_password_engine.speedtouch_keys(essid));
		}
		
        if	(		mac.startsWith("00:01:36")	||	mac.startsWith("00:01:38")	||	mac.startsWith("00:02:CF")
        		||	mac.startsWith("00:03:C9")	||	mac.startsWith("00:03:DA")	||	mac.startsWith("00:13:49")
        		||	mac.startsWith("00:16:38")	||	mac.startsWith("00:18:02")	||	mac.startsWith("00:19:CB")
        		||	mac.startsWith("00:1D:20")	||	mac.startsWith("00:1F:9F")	||	mac.startsWith("00:23:F8")
        		||	mac.startsWith("00:30:DA")	||	mac.startsWith("00:60:B3")	||	mac.startsWith("00:A0:C5")	
        		||	mac.startsWith("40:4A:03")	||	mac.startsWith("50:67:F0")	||	mac.startsWith("E0:91:53")
        	)
        {
        	result.add(Wifi_password_engine.xavi_comtrend_zyxel(essid,bssid));
        }
		
		
		
		
		
		if (		mac.startsWith("00:19:C7") || mac.startsWith("18:80:F5") || mac.startsWith("A4:C7:DE")
				||	mac.startsWith("A8:AD:3D") || mac.startsWith("AC:9C:E4") || mac.startsWith("D0:54:2D")
				||	mac.startsWith("E0:1D:3B") || mac.startsWith("E0:30:05"))
		{
			//TODO!
			result.add(new PasswordDictionary("AlcatelLucentKeygen(eessid, mac)",null,"AlcatelLucentKeygen TODO! pending"));
		}
		
		if (mac.startsWith("00:1E:40") || mac.startsWith("00:25:5E"))
		{
			//TODO!
			result.add(new PasswordDictionary("AliceGermanyKeygen(eessid, mac)",null,"AliceGermanyKeygen TODO! pending"));
		}

		if (essid.equals("Andared"))
		{
			//TODO!
			result.add(new PasswordDictionary("AndaredKeygen(eessid, mac)",null,"AndaredKeygen TODO! pending"));
		}

		if (		mac.startsWith("00:12:BF") || mac.startsWith("00:1A:2A")
                ||	mac.startsWith("00:1D:19") || mac.startsWith("00:23:08")
                ||	mac.startsWith("00:26:4D") || mac.startsWith("50:7E:5D")
                ||	mac.startsWith("1C:C6:3C") || mac.startsWith("74:31:70")
                ||	mac.startsWith("7C:4F:B5") || mac.startsWith("7E:4F:B5")
                ||	mac.startsWith("88:25:2C") || mac.startsWith("84:9C:A6")
                ||	mac.startsWith("88:03:55"))
		{
			result.add(Wifi_password_engine.ArcadyanKeygen(essid,bssid));
		}

		if(			mac.startsWith("00:08:27") || mac.startsWith("00:13:C8")
                ||	mac.startsWith("00:17:C2") || mac.startsWith("00:19:3E")
                ||	mac.startsWith("00:1C:A2") || mac.startsWith("00:1D:8B")
                ||	mac.startsWith("00:22:33") || mac.startsWith("00:23:8E")
                ||	mac.startsWith("00:25:53") || mac.startsWith("00:8C:54")
                ||	mac.startsWith("30:39:F2") || mac.startsWith("38:22:9D")
                ||	mac.startsWith("64:87:D7") || mac.startsWith("74:88:8B")
                ||	mac.startsWith("84:26:15") || mac.startsWith("A4:52:6F")
                ||	mac.startsWith("A4:5D:A1") || mac.startsWith("D0:D4:12")
                ||	mac.startsWith("D4:D1:84") || mac.startsWith("DC:0B:1A")
                ||	mac.startsWith("F0:84:2F")) 
		{
			result.add(Wifi_password_engine.ArnetPirelliKeygen(essid,bssid));
			result.add(Wifi_password_engine.MeoPirelliKeygen(essid,bssid));
        }

		if (essid.matches("(AXTEL|AXTEL-XTREMO)-[0-9a-fA-F]{4}")) 
		{
			final String eessidSubpart = essid.substring(essid.length() - 4);
			final String macShort = mac.replace(":", "");
			if (macShort.length() < 12 || eessidSubpart.equalsIgnoreCase(macShort.substring(8)))
			{
				//TODO!
				result.add(new PasswordDictionary("AxtelKeygen(eessid, mac)",null,"AxtelKeygen TODO! pending"));
			}
        }

		if (essid.matches("^(B|b)elkin(\\.|_)[0-9a-fA-F]{3,6}$")
                || mac.startsWith("94:44:52") || mac.startsWith("08:86:3B")
                || mac.startsWith("EC:1A:59"))
		{
			result.add(Wifi_password_engine.BelkinKeygen(essid,bssid));
		}

		if (essid.matches("Cabovisao-[0-9a-fA-F]{4}")) 
		{
            if (mac.length() == 0 || mac.startsWith("C0:AC:54"))
            {
    			//TODO!
    			result.add(new PasswordDictionary("CabovisaoSagemKeygen(eessid, mac)",null,"CabovisaoSagemKeygen TODO! pending"));
            }
        }


        if (essid.matches("conn-x[0-9a-fA-F]{6}") || essid.equals("CONN-X") ||
        		mac.startsWith("48:28:2F") || mac.startsWith("B0:75:D5") ||
        		mac.startsWith("C8:7B:5B") || mac.startsWith("FC:C8:97") ||
        		mac.startsWith("68:1A:B2") || mac.startsWith("38:46:08") ||
        		mac.startsWith("4C:09:9B") || mac.startsWith("4C:09:B4") ||
        		mac.startsWith("8C:E0:81") || mac.startsWith("DC:02:8E") ||
        		mac.startsWith("2C:26:C5") || mac.startsWith("FC:C8:97") ||
        		mac.startsWith("CC:1A:FA") || mac.startsWith("A0:EC:80") ||
        		mac.startsWith("54:22:F8") || mac.startsWith("14:60:80")) 
        {
        	result.add(Wifi_password_engine.ConnKeygen(essid,bssid));
        }

        if (	mac.startsWith("00:1C:A2") || mac.startsWith("00:17:C2") || mac.startsWith("00:19:3E") ||
                mac.startsWith("00:23:8E") || mac.startsWith("00:25:53") || mac.startsWith("38:22:9D") || 
                mac.startsWith("64:87:D7") || mac.startsWith("DC:0B:1A")) 
        {
        	/*
        	if (supportedCytas == null) 
        	{
        		supportedCytas = CytaConfigParser.parse(getEntry("cyta_bases.txt", magicInfo));
            }
            final String filteredMac = mac.replace(":", "");
            if (filteredMac.length() == 12) 
            {
            	final String key = filteredMac.substring(0, 8);
            	final ArrayList<CytaMagicInfo> supportedCyta = supportedCytas.get(key);
            	if (supportedCyta != null) 
            	{
            		keygens.add(new CytaKeygen(essid, mac, supportedCyta));
            	}
            }
            */
			//TODO!
			result.add(new PasswordDictionary("CytaKeygen(eessid, mac)",null,"CytaKeygen TODO! pending"));
        }

        if (	mac.startsWith("CC:1A:FA") || mac.startsWith("14:60:80") || mac.startsWith("DC:02:8E") || mac.startsWith("CC:7B:35") ||
        		mac.startsWith("20:89:86") || mac.startsWith("2C:95:7F") || mac.startsWith("F8:DF:A8") || mac.startsWith("EC:8A:4C")) 
        {
        	/*
        	if (supportedCytaZTEs == null) 
        	{
        		supportedCytaZTEs = CytaZTEConfigParser.parse(getEntry("cyta_zte_bases.txt", magicInfo));
        	}
        	keygens.add(new CytaZTEKeygen(essid, mac, supportedCytaZTEs));
        	*/
			//TODO!
			result.add(new PasswordDictionary("CytaZTEKeygen(eessid, mac)",null,"CytaZTEKeygen TODO! pending"));
        }


        if (essid.matches("Discus--?[0-9a-fA-F]{6}"))
        {
			//TODO!
			result.add(new PasswordDictionary("CytaZTEKeygen(eessid, mac)",null,"CytaZTEKeygen TODO! pending"));
        }

        if (essid.matches("(DL|dl)ink-[0-9a-fA-F]{6}")
        		|| mac.startsWith("00:05:5D") || mac.startsWith("00:0D:88")
        		|| mac.startsWith("00:0F:3D") || mac.startsWith("00:11:95")
        		|| mac.startsWith("00:13:46") || mac.startsWith("00:15:E9")
        		|| mac.startsWith("00:17:9A") || mac.startsWith("00:19:5B")
        		|| mac.startsWith("00:1B:11") || mac.startsWith("00:1C:F0")
        		|| mac.startsWith("00:1E:58") || mac.startsWith("00:21:91")
        		|| mac.startsWith("00:22:B0") || mac.startsWith("00:24:01")
        		|| mac.startsWith("00:26:5A") || mac.startsWith("14:D6:4D")
        		|| mac.startsWith("1C:7E:E5") || mac.startsWith("28:10:7B")
        		|| mac.startsWith("34:08:04") || mac.startsWith("5C:D9:98")
        		|| mac.startsWith("84:C9:B2") || mac.startsWith("90:94:E4")
        		|| mac.startsWith("AC:F1:DF") || mac.startsWith("B8:A3:86")
        		|| mac.startsWith("BC:F6:85") || mac.startsWith("C8:BE:19")
        		|| mac.startsWith("CC:B2:55") || mac.startsWith("F0:7D:68")
        		|| mac.startsWith("FC:75:16"))
        {
			//TODO!
			result.add(new PasswordDictionary("DlinkKeygen(eessid, mac)",null,"DlinkKeygen TODO! pending"));
        }


        if (essid.matches("INFINITUM[0-9a-zA-Z]{4}") 
        		|| mac.startsWith("00:18:82") || mac.startsWith("00:1E:10")
        		|| mac.startsWith("00:22:A1") || mac.startsWith("00:25:68") || mac.startsWith("00:25:9E")
        		|| mac.startsWith("00:34:FE") || mac.startsWith("00:46:4B") || mac.startsWith("00:66:4B")
        		|| mac.startsWith("00:E0:FC") || mac.startsWith("00:F8:1C") || mac.startsWith("04:02:1F")
        		|| mac.startsWith("04:BD:70") || mac.startsWith("04:C0:6F") || mac.startsWith("04:F9:38")
        		|| mac.startsWith("08:19:A6") || mac.startsWith("08:63:61") || mac.startsWith("08:7A:4C")
        		|| mac.startsWith("08:E8:4F") || mac.startsWith("0C:37:DC") || mac.startsWith("0C:96:BF")
        		|| mac.startsWith("0C:D6:BD") || mac.startsWith("10:1B:54") || mac.startsWith("10:47:80")
        		|| mac.startsWith("10:51:72") || mac.startsWith("10:C6:1F") || mac.startsWith("14:B9:68")
        		|| mac.startsWith("18:C5:8A") || mac.startsWith("1C:1D:67") || mac.startsWith("1C:8E:5C")
        		|| mac.startsWith("20:08:ED") || mac.startsWith("20:0B:C7") || mac.startsWith("20:2B:C1")
                || mac.startsWith("20:F3:A3") || mac.startsWith("24:09:95") || mac.startsWith("24:1F:A0")
                || mac.startsWith("24:69:A5") || mac.startsWith("24:7F:3C") || mac.startsWith("24:9E:AB")
                || mac.startsWith("24:DB:AC") || mac.startsWith("28:31:52") || mac.startsWith("28:3C:E4")
                || mac.startsWith("28:5F:DB") || mac.startsWith("28:6E:D4") || mac.startsWith("2C:CF:58")
                || mac.startsWith("30:87:30") || mac.startsWith("30:D1:7E") || mac.startsWith("30:F3:35")
                || mac.startsWith("34:00:A3") || mac.startsWith("34:6B:D3") || mac.startsWith("34:CD:BE")
                || mac.startsWith("38:F8:89") || mac.startsWith("3C:47:11") || mac.startsWith("3C:DF:BD")
                || mac.startsWith("3C:F8:08") || mac.startsWith("40:4D:8E") || mac.startsWith("40:CB:A8")
                || mac.startsWith("44:55:B1") || mac.startsWith("48:46:FB") || mac.startsWith("48:62:76")
                || mac.startsWith("4C:1F:CC") || mac.startsWith("4C:54:99") || mac.startsWith("4C:8B:EF")
                || mac.startsWith("4C:B1:6C") || mac.startsWith("50:9F:27") || mac.startsWith("50:A7:2B")
                || mac.startsWith("54:39:DF") || mac.startsWith("54:89:98") || mac.startsWith("54:A5:1B")
                || mac.startsWith("58:1F:28") || mac.startsWith("58:2A:F7") || mac.startsWith("58:7F:66")
                || mac.startsWith("5C:4C:A9") || mac.startsWith("5C:7D:5E") || mac.startsWith("5C:B3:95")
                || mac.startsWith("5C:B4:3E") || mac.startsWith("5C:F9:6A") || mac.startsWith("60:DE:44")
                || mac.startsWith("60:E7:01") || mac.startsWith("64:16:F0") || mac.startsWith("64:3E:8C")
                || mac.startsWith("64:A6:51") || mac.startsWith("68:89:C1") || mac.startsWith("68:8F:84")
                || mac.startsWith("68:A0:F6") || mac.startsWith("68:A8:28") || mac.startsWith("70:54:F5")
                || mac.startsWith("70:72:3C") || mac.startsWith("70:7B:E8") || mac.startsWith("70:A8:E3")
                || mac.startsWith("74:88:2A") || mac.startsWith("74:A0:63") || mac.startsWith("78:1D:BA")
                || mac.startsWith("78:6A:89") || mac.startsWith("78:D7:52") || mac.startsWith("78:F5:FD")
                || mac.startsWith("7C:60:97") || mac.startsWith("7C:A2:3E") || mac.startsWith("80:38:BC")
                || mac.startsWith("80:71:7A") || mac.startsWith("80:B6:86") || mac.startsWith("80:D0:9B")
                || mac.startsWith("80:FB:06") || mac.startsWith("84:5B:12") || mac.startsWith("84:A8:E4")
                || mac.startsWith("84:DB:AC") || mac.startsWith("88:53:D4") || mac.startsWith("88:86:03")
                || mac.startsWith("88:A2:D7") || mac.startsWith("88:CE:FA") || mac.startsWith("88:E3:AB")
                || mac.startsWith("8C:34:FD") || mac.startsWith("90:17:AC") || mac.startsWith("90:4E:2B")
                || mac.startsWith("90:67:1C") || mac.startsWith("94:04:9C") || mac.startsWith("94:77:2B")
                || mac.startsWith("9C:28:EF") || mac.startsWith("9C:37:F4") || mac.startsWith("9C:C1:72")
                || mac.startsWith("A4:99:47") || mac.startsWith("A4:DC:BE") || mac.startsWith("AC:4E:91")
                || mac.startsWith("AC:85:3D") || mac.startsWith("AC:E2:15") || mac.startsWith("AC:E8:7B")
                || mac.startsWith("B0:5B:67") || mac.startsWith("B4:15:13") || mac.startsWith("B4:30:52")
                || mac.startsWith("B8:BC:1B") || mac.startsWith("BC:25:E0") || mac.startsWith("BC:76:70")
                || mac.startsWith("BC:9C:31") || mac.startsWith("C0:70:09") || mac.startsWith("C4:05:28")
                || mac.startsWith("C4:07:2F") || mac.startsWith("C8:51:95") || mac.startsWith("C8:D1:5E")
                || mac.startsWith("CC:53:B5") || mac.startsWith("CC:96:A0") || mac.startsWith("CC:A2:23")
                || mac.startsWith("CC:CC:81") || mac.startsWith("D0:2D:B3") || mac.startsWith("D0:3E:5C")
                || mac.startsWith("D0:7A:B5") || mac.startsWith("D4:40:F0") || mac.startsWith("D4:6A:A8")
                || mac.startsWith("D4:6E:5C") || mac.startsWith("D4:94:E8") || mac.startsWith("D4:B1:10")
                || mac.startsWith("D4:F9:A1") || mac.startsWith("D8:49:0B") || mac.startsWith("DC:D2:FC")
                || mac.startsWith("E0:19:1D") || mac.startsWith("E0:24:7F") || mac.startsWith("E0:36:76")
                || mac.startsWith("E0:97:96") || mac.startsWith("E4:35:C8") || mac.startsWith("E4:68:A3")
                || mac.startsWith("E4:C2:D1") || mac.startsWith("E8:08:8B") || mac.startsWith("E8:BD:D1")
                || mac.startsWith("E8:CD:2D") || mac.startsWith("EC:23:3D") || mac.startsWith("EC:38:8F")
                || mac.startsWith("EC:CB:30") || mac.startsWith("F4:55:9C") || mac.startsWith("F4:8E:92")
                || mac.startsWith("F4:9F:F3") || mac.startsWith("F4:C7:14") || mac.startsWith("F4:DC:F9")
                || mac.startsWith("F4:E3:FB") || mac.startsWith("F8:01:13") || mac.startsWith("F8:3D:FF")
                || mac.startsWith("F8:4A:BF") || mac.startsWith("F8:98:B9") || mac.startsWith("F8:BF:09")
                || mac.startsWith("F8:E8:11") || mac.startsWith("FC:48:EF") || mac.startsWith("FC:E3:3C")
                || mac.startsWith("00:19:15") || mac.startsWith("00:11:F5")	|| mac.startsWith("00:0F:E2")
			)
        {
        	result.add(Wifi_password_engine.HuaweiKeygen(essid,bssid));
        }

        if (		mac.startsWith("00:18:82") || mac.startsWith("00:1E:10") || mac.startsWith("00:22:A1")
                ||	mac.startsWith("00:25:68") || mac.startsWith("00:25:9E") || mac.startsWith("00:34:FE")
                ||	mac.startsWith("00:46:4B") || mac.startsWith("00:66:4B") || mac.startsWith("00:E0:FC")
                ||	mac.startsWith("00:F8:1C") || mac.startsWith("08:19:A6") || mac.startsWith("08:63:61")
                ||	mac.startsWith("08:7A:4C") || mac.startsWith("08:E8:4F") || mac.startsWith("10:1B:54")
                ||	mac.startsWith("10:47:80") || mac.startsWith("10:51:72") || mac.startsWith("10:C6:1F")
                ||	mac.startsWith("20:08:ED") || mac.startsWith("20:0B:C7") || mac.startsWith("20:2B:C1")
                ||	mac.startsWith("20:F3:A3") || mac.startsWith("28:31:52") || mac.startsWith("28:3C:E4")
                ||	mac.startsWith("28:5F:DB") || mac.startsWith("28:6E:D4") || mac.startsWith("48:46:FB")
                ||	mac.startsWith("48:62:76") || mac.startsWith("70:54:F5") || mac.startsWith("70:72:3C")
                ||	mac.startsWith("70:7B:E8") || mac.startsWith("70:A8:E3") || mac.startsWith("80:38:BC")
                ||	mac.startsWith("80:71:7A") || mac.startsWith("80:B6:86") || mac.startsWith("80:D0:9B")
                ||	mac.startsWith("80:FB:06") || mac.startsWith("AC:4E:91") || mac.startsWith("AC:85:3D")
                ||	mac.startsWith("AC:E2:15") || mac.startsWith("AC:E8:7B") || mac.startsWith("CC:53:B5")
                ||	mac.startsWith("CC:96:A0") || mac.startsWith("CC:A2:23") || mac.startsWith("CC:CC:81")
                ||	mac.startsWith("D4:40:F0") || mac.startsWith("D4:6A:A8") || mac.startsWith("D4:6E:5C")
                ||	mac.startsWith("D4:94:E8") || mac.startsWith("D4:B1:10") || mac.startsWith("D4:F9:A1")
                ||	mac.startsWith("E0:19:1D") || mac.startsWith("E0:24:7F") || mac.startsWith("E0:36:76")
                ||	mac.startsWith("E0:97:96") || mac.startsWith("F8:01:13") || mac.startsWith("F8:3D:FF")
                ||	mac.startsWith("F8:4A:BF") || mac.startsWith("F8:98:B9") || mac.startsWith("F8:BF:09")
                ||	mac.startsWith("F8:E8:11"))
        {
			result.add(Wifi_password_engine.HG824xKeygen(essid,bssid));
        }


        if (essid.startsWith("InterCable")	&& (mac.startsWith("00:15") || mac.startsWith("00:1D")))
        {
			//TODO!
			result.add(new PasswordDictionary("InterCableKeygen(eessid, mac)",null,"InterCableKeygen TODO! pending"));
        }

        if (essid.matches("MAXCOM[0-9a-zA-Z]{4}"))
        {
			//TODO!
			result.add(new PasswordDictionary("MaxcomKeygen(eessid, mac)",null,"MaxcomKeygen TODO! pending"));
        }

        if (essid.matches("Megared[0-9a-fA-F]{4}")) 
        {
        	// the final 4 characters of the essid should match the final
        	if (mac.length() == 0	|| essid.substring(essid.length() - 4).equals(mac.replace(":", "").substring(8)))
        	{
    			//TODO!
    			result.add(new PasswordDictionary("MegaredKeygen(eessid, mac)",null,"MegaredKeygen TODO! pending"));
        	}
        }

        if (mac.startsWith("00:05:59")) 
        {
        	/*
        	if (supportedNetfasters == null) 
        	{
        		supportedNetfasters = NetfasterConfigParser.parse(getEntry("netfaster_bases.txt", magicInfo));
        	}
        	keygens.add(new NetFasterKeygen(essid, mac, supportedNetfasters));
        	*/
			//TODO!
			result.add(new PasswordDictionary("NetFasterKeygen(eessid, mac)",null,"NetFasterKeygen TODO! pending"));
        }

        /* essid must be of the form P1XXXXXX0000X or p1XXXXXX0000X */
        if (essid.matches("[Pp]1[0-9]{6}0{4}[0-9]"))
        {
			//TODO!
			result.add(new PasswordDictionary("OnoKeygen(eessid, mac)",null,"TODO! pending"));
        }

        if (essid.matches("OTE[0-9a-fA-F]{4}") && (mac.startsWith("00:13:33")))
        {
			//TODO!
			result.add(new PasswordDictionary("OteBAUDKeygen(eessid, mac)",null,"OteBAUDKeygen TODO! pending"));
        }

        if (essid.matches("OTE[0-9a-fA-F]{6}")
             &&	(	mac.startsWith("C8:7B:5B")	||	mac.startsWith("FC:C8:97")
            	||	mac.startsWith("68:1A:B2")	||	mac.startsWith("B0:75:D5")
            	|| 	(mac .startsWith("38:46:08"))))
        {
			//TODO!
			result.add(new PasswordDictionary("OteBAUDKeygen(eessid, mac)",null,"OteBAUDKeygen TODO! pending"));
        }

        if (essid.toUpperCase().startsWith("OTE")
        		&& (mac.startsWith("E8:39:DF:F5") || mac.startsWith("E8:39:DF:F6") || mac.startsWith("E8:39:DF:FD"))) 
        {
        	/*
        	if (supportedOTE == null) 
        	{
        		supportedOTE = OTEHuaweiConfigParser.parse(getEntry("ote_huawei.txt", magicInfo));
        	}
        	final String filteredMac = mac.replace(":", "");
        	final int target = Integer.parseInt(filteredMac.substring(8), 16);
        	if (filteredMac.length() == 12 && target > (OteHuaweiKeygen.MAGIC_NUMBER - supportedOTE.length))
        	{
                keygens.add(new OteHuaweiKeygen(essid, mac,supportedOTE[OteHuaweiKeygen.MAGIC_NUMBER - target]));
        	}
        	*/
			//TODO!
			result.add(new PasswordDictionary("OteHuaweiKeygen(eessid, mac)",null,"OteHuaweiKeygen TODO! pending"));
        }

        if (essid.matches("PBS-[0-9a-fA-F]{6}") 
        		|| mac.startsWith("00:08:27")
        		|| mac.startsWith("00:13:C8") || mac.startsWith("00:17:C2")
        		|| mac.startsWith("00:19:3E") || mac.startsWith("00:1C:A2")
        		|| mac.startsWith("00:1D:8B") || mac.startsWith("00:22:33")
        		|| mac.startsWith("00:23:8E") || mac.startsWith("00:25:53")
        		|| mac.startsWith("30:39:F2") || mac.startsWith("38:22:9D")
        		|| mac.startsWith("64:87:D7") || mac.startsWith("74:88:8B")
        		|| mac.startsWith("A4:52:6F") || mac.startsWith("D4:D1:84"))
        {
        	result.add(Wifi_password_engine.PBSKeygen(essid,bssid));
        }

        if (essid.matches("FASTWEB-1-(000827|0013C8|0017C2|00193E|001CA2|001D8B|"+ "002233|00238E|002553|00A02F|080018|3039F2|38229D|6487D7)[0-9A-Fa-f]{6}")) 
        {
        	if (mac.length() == 0) 
        	{
        		final String end = essid.substring(essid.length() - 12);
        		mac = end.substring(0, 2) + ":" + end.substring(2, 4) + ":"+ end.substring(4, 6) + ":" + end.substring(6, 8) + ":"+ end.substring(8, 10) + ":" + end.substring(10, 12);
        	}
			//TODO!
			result.add(new PasswordDictionary("PirelliKeygen(eessid, mac)",null,"PirelliKeygen TODO! pending"));
        }

        if (essid.matches("(PTV-|ptv|ptv-)[0-9a-zA-Z]{6}"))
        {
			//TODO!
			result.add(new PasswordDictionary("PtvKeygen(eessid,mac)",null,"PtvKeygen TODO! pending"));
        }

        if (mac.startsWith("00:0C:F6") || mac.startsWith("64:D1:A3")) 
        {
			result.add(Wifi_password_engine.SitecomX5000Keygen(bssid));
			result.add(Wifi_password_engine.Sitecom2100Keygen(bssid));
        }

        if (essid.toLowerCase().matches("^sitecom[0-9a-f]{6}$") || (mac.startsWith("00:0C:F6") || mac.startsWith("64:D1:A3"))) 
        {
			try 
			{
				String essid_suffix=essid.substring(essid.length()-6).toUpperCase();
				String essid_suffix_ieee=""+essid_suffix.charAt(0)+essid_suffix.charAt(1)+":"+essid_suffix.charAt(2)+essid_suffix.charAt(3)+":"+essid_suffix.charAt(4)+essid_suffix.charAt(5);
				Mac mac_a=new Mac("00:0C:F6"+":"+essid_suffix_ieee);
	       		Mac mac_b=new Mac("64:D1:A3"+":"+essid_suffix_ieee);
	       		result.add(Wifi_password_engine.SitecomWLR341_400xKeygen(mac_a));
	       		result.add(Wifi_password_engine.SitecomWLR341_400xKeygen(mac_b));
			} 
			catch (UnparseableMacException e) 
			{
				//might be non-default essid:
	       		result.add(Wifi_password_engine.SitecomWLR341_400xKeygen(bssid));
			}
        }

        if (essid.matches("SKY[0-9]{5}")
                && (mac.startsWith("C4:3D:C7") || mac.startsWith("E0:46:9A")
                || mac.startsWith("E0:91:F5")
                || mac.startsWith("00:09:5B")
                || mac.startsWith("00:0F:B5")
                || mac.startsWith("00:14:6C")
                || mac.startsWith("00:18:4D")
                || mac.startsWith("00:26:F2")
                || mac.startsWith("C0:3F:0E")
                || mac.startsWith("30:46:9A")
                || mac.startsWith("00:1B:2F")
                || mac.startsWith("A0:21:B7")
                || mac.startsWith("00:1E:2A")
                || mac.startsWith("00:1F:33")
                || mac.startsWith("00:22:3F") || mac.startsWith("00:24:B2")))
        {
			//TODO!
			result.add(new PasswordDictionary("SkyV1Keygen(eessid, mac)",null,"SkyV1Keygen TODO! pending"));
        }

        if (essid.matches("WLAN-[0-9a-fA-F]{6}")	&& (mac.startsWith("00:12:BF") || mac.startsWith("00:1A:2A") || mac.startsWith("00:1D:19")))
        {
			//TODO!
			result.add(new PasswordDictionary("Speedport500Keygen(eessid, mac)",null,"Speedport500Keygen TODO! pending"));
        }

        if (essid.matches("TECOM-AH4(021|222)-[0-9a-zA-Z]{6}"))
        {
			//TODO!
			result.add(new PasswordDictionary("TecomKeygen(eessid, mac)",null,"TecomKeygen TODO! pending"));
        }

        if (essid.toLowerCase().startsWith("teletu")) 
        {
        	/*
        	if (supportedTeletu == null) 
        	{
        		supportedTeletu = TeleTuConfigParser.parse(getEntry("tele2.txt", magicInfo));
        	}
        	String filteredMac = mac.replace(":", "");
        	if (filteredMac.length() != 12	&& essid.matches("TeleTu_[0-9a-fA-F]{12}"))
        	{
                mac = filteredMac = essid.substring(7);
        	}
        	if (filteredMac.length() == 12) 
        	{
        		final List<TeleTuMagicInfo> supported = supportedTeletu.get(filteredMac.substring(0, 6));
        		if (supported != null && supported.size() > 0) 
        		{
        			final int macIntValue = Integer.parseInt(filteredMac.substring(6), 16);
                    for (TeleTuMagicInfo magic : supported) 
                    {
                    	if (macIntValue >= magic.getRange()[0] && macIntValue <= magic.getRange()[1]) 
                    	{
                    		keygens.add(new TeleTuKeygen(essid, mac, magic));
                    	}
                    }
        		}
        	}
        	*/
			//TODO!
			result.add(new PasswordDictionary("TeleTuKeygen(eessid, mac)",null,"TeleTuKeygen TODO! pending"));
        }
        
        if (essid.matches("FASTWEB-(1|2)-(002196|00036F)[0-9A-Fa-f]{6}")) 
        {
        	/*
        	if (mac.length() == 0) 
        	{
        		final String end = essid.substring(essid.length() - 12);
        		mac = end.substring(0, 2) + ":" + end.substring(2, 4) + ":"+ end.substring(4, 6) + ":" + end.substring(6, 8) + ":"+ end.substring(8, 10) + ":" + end.substring(10, 12);
            }
        	keygens.add(new TelseyKeygen(essid, mac));
        	*/
			//TODO!
			result.add(new PasswordDictionary("TelseyKeygen(eessid, mac)",null,"TelseyKeygen TODO! pending"));
        }

        if (essid.matches("(Thomson|Blink|SpeedTouch|O2Wireless|O2wireless|Orange-|ORANGE-|INFINITUM|"
                + "BigPond|Otenet|Bbox-|DMAX|privat|TN_private_|CYTA|Vodafone-|Optimus|OptimusFibra|MEO-)[0-9a-fA-F]{6}"))
        {
			//TODO!
			result.add(new PasswordDictionary("ThomsonKeygen(eessid, mac)",null,"ThomsonKeygen TODO! pending"));
        }
        
        if (mac.startsWith("F8:D1:11"))
        {
			//TODO!
			result.add(new PasswordDictionary("TplinkKeygen(eessid, mac)",null,"TplinkKeygen TODO! pending"));
        }

        if (essid.length() == 5
        		&& (	   mac.startsWith("00:1F:90") 
        				|| mac.startsWith("A8:39:44")
        				|| mac.startsWith("00:18:01")
        				|| mac.startsWith("00:20:E0")
        				|| mac.startsWith("00:0F:B3")
        				|| mac.startsWith("00:1E:A7")
        				|| mac.startsWith("00:15:05")
        				|| mac.startsWith("00:24:7B")
        				|| mac.startsWith("00:26:62") 
        				|| mac.startsWith("00:26:B8")))
        {
			//TODO!
			result.add(new PasswordDictionary("VerizonKeygen(eessid, mac)",null,"VerizonKeygen TODO! pending"));
        }

        if (essid.matches("wifimedia_R-[0-9a-zA-Z]{4}") && mac.replace(":", "").length() == 12)
        {
			//TODO!
			result.add(new PasswordDictionary("WifimediaRKeygen(eessid, mac)",null,"WifimediaRKeygen TODO! pending"));
        }


        

        if (essid.matches("(WLAN|WiFi|WIFI|YaCom|YACOM)[0-9a-zA-Z]{6}"))
        {
        	result.add(Wifi_password_engine.Wlan6Keygen(essid,bssid));
        }

        if (essid.matches("(WLAN|JAZZTEL)_[0-9a-fA-F]{4}")) 
        {
        	if (mac.startsWith("00:1F:A4") || mac.startsWith("F4:3E:61") || mac.startsWith("40:4A:03"))
        	{
        		result.add(Wifi_password_engine.ZyxelKeygen(essid,bssid));
        	}

        	if (		mac.startsWith("00:1B:20") || mac.startsWith("64:68:0C")
                    ||	mac.startsWith("00:1D:20") || mac.startsWith("00:23:F8")
                    ||	mac.startsWith("38:72:C0") || mac.startsWith("30:39:F2")
                    ||	mac.startsWith("8C:0C:A3") || mac.startsWith("5C:33:8E")
                    ||	mac.startsWith("C8:6C:87") || mac.startsWith("D0:AE:EC")
                    ||	mac.startsWith("00:19:15") || mac.startsWith("00:1A:2B"))
        	{
        		result.add(Wifi_password_engine.ComtrendKeygen(essid,bssid));
        	}
        }

        if (essid.matches("UPC[0-9]{5,8}")) 
        {
			//TODO!
			result.add(new PasswordDictionary("UpcKeygen(eessid, mac)",null,"UpcKeygen TODO! pending"));
        }		
        
        
        
        return result;
	}
	//-----------------------------------------------------------------------------------------------------------------------
}
