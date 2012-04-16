/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID.BASE64;

import APJP.ANDROID.JAVA5.Arrays;

public class BASE64
{
	private static final char[] BASE64_TABLE1 =
	{
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
		'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
		'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
		'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', '+', '/'
	};
	private static final byte[] BASE64_TABLE2;
	private static final int EQ = 61;
	
	static
	{
		BASE64_TABLE2 = new byte[256];
		for(int i = 0; i < 255; i = i + 1)
		{
			BASE64_TABLE2[i] = -1;
		}
		for(int i = 0; i < BASE64_TABLE1.length; i = i + 1)
		{
			BASE64_TABLE2[BASE64_TABLE1[i]] = (byte)i;
		}
	}
	
	protected static int encode0(byte[] inputByteArray, int inputByteArrayLength, byte[] outputByteArray, int outputByteArrayLength)
	{
		if(inputByteArrayLength == 1)
		{
			byte b = inputByteArray[0];
			int i = 0;
			
			outputByteArray[outputByteArrayLength + 0] = (byte) BASE64_TABLE1[b>>>2 & 0x3f];
			outputByteArray[outputByteArrayLength + 1] = (byte) BASE64_TABLE1[(b<<4 & 0x30) + (i>>>4 & 0xf)];
			outputByteArray[outputByteArrayLength + 2] = EQ;
			outputByteArray[outputByteArrayLength + 3] = EQ;
		}
		else if(inputByteArrayLength == 2)
		{
			byte b1 = inputByteArray[0];
			byte b2 = inputByteArray[1];
			int i = 0;
			
			outputByteArray[outputByteArrayLength + 0] = (byte) BASE64_TABLE1[b1>>>2 & 0x3f];
			outputByteArray[outputByteArrayLength + 1] = (byte) BASE64_TABLE1[(b1<<4 & 0x30) + (b2>>>4 & 0xf)];
			outputByteArray[outputByteArrayLength + 2] = (byte) BASE64_TABLE1[(b2<<2 & 0x3c) + (i>>>6 & 0x3)];
			outputByteArray[outputByteArrayLength + 3] = EQ;
		}
		else
		{
			byte b1 = inputByteArray[0];
			byte b2 = inputByteArray[1];
			byte b3 = inputByteArray[2];
			
			outputByteArray[outputByteArrayLength + 0] = (byte) BASE64_TABLE1[b1>>>2 & 0x3f];
			outputByteArray[outputByteArrayLength + 1] = (byte) BASE64_TABLE1[(b1<<4 & 0x30) + (b2>>>4 & 0xf)];
			outputByteArray[outputByteArrayLength + 2] = (byte) BASE64_TABLE1[(b2<<2 & 0x3c) + (b3>>>6 & 0x3)];
			outputByteArray[outputByteArrayLength + 3] = (byte) BASE64_TABLE1[b3 & 0x3f];
		}
		
		return outputByteArrayLength + 4;
	}
	
	public static byte[] encode(byte[] byteArray, int byteArrayOffset, int byteArrayLength)
	{
		byte[] inputByteArray = new byte[3];
		int inputByteArrayLength = 0;
		byte[] outputByteArray = new byte[byteArrayLength / 3 * 4 + 4];
		int outputByteArrayLength = 0;
		
		for(int i = byteArrayOffset;  i < byteArrayOffset + byteArrayLength;  i = i + 1)
		{
			inputByteArray[inputByteArrayLength] = byteArray[i];
			inputByteArrayLength = inputByteArrayLength + 1;
			
			if(inputByteArrayLength == inputByteArray.length)
			{
				outputByteArrayLength = encode0(inputByteArray, inputByteArrayLength, outputByteArray, outputByteArrayLength);
				inputByteArrayLength = 0;
			}
			
		}
		
		if(inputByteArrayLength != 0)
		{
			outputByteArrayLength = encode0(inputByteArray, inputByteArrayLength, outputByteArray, outputByteArrayLength);
			inputByteArrayLength = 0;
		}
		
		if(outputByteArrayLength == outputByteArray.length)
		{
			return outputByteArray;
		}
		else
		{
			return Arrays.copyOf(outputByteArray, outputByteArrayLength);
		}
	}
	
	public static byte[] encode(byte[] byteArray)
	{
		return encode(byteArray, 0, byteArray.length);
	}
	
	protected static int decode0(byte[] inputByteArray, int inputByteArrayLength, byte[] outputByteArray, int outputByteArrayLength)
	{ 
		byte b0 = BASE64_TABLE2[inputByteArray[0] & 0xff];
		byte b2 = BASE64_TABLE2[inputByteArray[1] & 0xff];
		
		outputByteArray[outputByteArrayLength] = (byte)(b0<<2 & 0xfc | b2>>>4 & 0x3);
		outputByteArrayLength = outputByteArrayLength + 1;
		
		if(inputByteArray[2] != EQ)
		{
			b0 = b2;
			b2 = BASE64_TABLE2[inputByteArray[2] & 0xff];
			
			outputByteArray[outputByteArrayLength] = (byte)(b0<<4 & 0xf0 | b2>>>2 & 0xf);
			outputByteArrayLength = outputByteArrayLength + 1;
			
			if(inputByteArray[3] != EQ)
			{
				b0 = b2;
				b2 = BASE64_TABLE2[inputByteArray[3] & 0xff];
				
				outputByteArray[outputByteArrayLength] = (byte)(b0<<6 & 0xc0 | b2 & 0x3f);
				outputByteArrayLength = outputByteArrayLength + 1;
			}
		}
		
		return outputByteArrayLength;
	}
	
	public static byte[] decode(byte[] byteArray, int byteArrayOffset, int byteArrayLength)
	{
		byte[] inputByteArray = new byte[4];
		int inputByteArrayLength = 0;
		byte[] outputByteArray = new byte[byteArrayLength / 4 * 3 + 3];
		int outputByteArrayLength = 0;
		
		for(int i = byteArrayOffset;  i < byteArrayOffset + byteArrayLength;  i = i + 1)
		{
			inputByteArray[inputByteArrayLength] = byteArray[i];
			inputByteArrayLength = inputByteArrayLength + 1;
			
			if(inputByteArrayLength == inputByteArray.length)
			{
				outputByteArrayLength = decode0(inputByteArray, inputByteArrayLength, outputByteArray, outputByteArrayLength);
				inputByteArrayLength = 0;
			}        
		}
		
		if(outputByteArrayLength == outputByteArray.length)
		{
			return outputByteArray;
		}
		else
		{
			return Arrays.copyOf(outputByteArray, outputByteArrayLength);
		}
	}
	
	public static byte[] decode(byte[] byteArray)
	{
		return decode(byteArray, 0, byteArray.length);
	}
}