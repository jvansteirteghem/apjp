/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTP11;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class HTTPResponseMessage extends HTTPMessage
{
	protected HTTPRequestMessage httpRequestMessage;
	protected InputStream inputStream;
	
	public HTTPResponseMessage(HTTPRequestMessage httpRequestMessage, InputStream inputStream)
	{
		super();
		
		this.httpRequestMessage = httpRequestMessage;
		this.inputStream = inputStream;
	}
	
	public void read() throws HTTPMessageException
	{
		try
		{
			byte[] byteArray1;
			
			byte[] byteArray2;
			int byteArray2Length;
			
			byteArray1 = new byte[1];
			
			byteArray2 = new byte[1024];
			byteArray2Length = 0;
			
			while((inputStream.read(byteArray1)) > 0)
			{
				if(byteArray2Length == byteArray2.length)
				{
					byteArray2 = Arrays.copyOf(byteArray2, byteArray2Length + byteArray2Length);
				}
				
				byteArray2[byteArray2Length] = byteArray1[0];
				byteArray2Length = byteArray2Length + 1;
				
				if(byteArray2Length >= 4)
				{
					if
					(
						byteArray2[byteArray2Length - 4] == '\r' && 
						byteArray2[byteArray2Length - 3] == '\n' && 
						byteArray2[byteArray2Length - 2] == '\r' && 
						byteArray2[byteArray2Length - 1] == '\n'
					)
					{
						break;
					}
				}
			}
			
			String httpResponseMessageHeader1Value1 = new String(byteArray2, 0, byteArray2Length);
			
			String[] httpResponseMessageHeader1Values1 = httpResponseMessageHeader1Value1.split("\r\n");
			
			String httpResponseMessageHeader1Value2 = httpResponseMessageHeader1Values1[0];
			
			addHTTPMessageHeader(new HTTPMessageHeader("", httpResponseMessageHeader1Value2));
			
			for(int i = 1; i < httpResponseMessageHeader1Values1.length; i = i + 1)
			{
				String httpResponseMessageHeader1Value3 = httpResponseMessageHeader1Values1[i];
				
				String[] httpResponseMessageHeader1Values3 = httpResponseMessageHeader1Value3.split(": ");
				
				String httpResponseMessageHeader1Value4 = httpResponseMessageHeader1Values3[0];
				
				String httpResponseMessageHeader1Value5 = "";
				
				if(httpResponseMessageHeader1Values3.length == 2)
				{
					httpResponseMessageHeader1Value5 = httpResponseMessageHeader1Values3[1];
				}
					
				addHTTPMessageHeader(new HTTPMessageHeader(httpResponseMessageHeader1Value4, httpResponseMessageHeader1Value5));
			}
		}
		catch(Exception e)
		{
			throw new HTTPMessageException("HTTP_RESPONSE_MESSAGE/READ", e);
		}
	}
	
	public void read(OutputStream outputStream) throws HTTPMessageException
	{
		try
		{
			HTTPMessageHeader httpRequestMessageHeader1 = httpRequestMessage.getHTTPMessageHeader("");
			
			String httpRequestMessageHeader1Value1 = httpRequestMessageHeader1.getValue();
			
			String[] httpRequestMessageHeader1Values1 = httpRequestMessageHeader1Value1.split(" ");
			
			String httpRequestMessageHeader1Value2 = httpRequestMessageHeader1Values1[0];
			
			// 1.1
			if(httpRequestMessageHeader1Value2.equalsIgnoreCase("OPTIONS"))
			{
				
			}
			// 1.0
			else if(httpRequestMessageHeader1Value2.equalsIgnoreCase("GET"))
			{
				
			}
			// 1.0
			else if(httpRequestMessageHeader1Value2.equalsIgnoreCase("HEAD"))
			{
				return;
			}
			// 1.0
			else if(httpRequestMessageHeader1Value2.equalsIgnoreCase("POST"))
			{
				
			}
			// 1.1
			else if(httpRequestMessageHeader1Value2.equalsIgnoreCase("PUT"))
			{
				
			}
			// 1.1
			else if(httpRequestMessageHeader1Value2.equalsIgnoreCase("DELETE"))
			{
				
			}
			// 1.1
			else if(httpRequestMessageHeader1Value2.equalsIgnoreCase("TRACE"))
			{
				
			}
			// 1.1
			else if(httpRequestMessageHeader1Value2.equalsIgnoreCase("CONNECT"))
			{
				
			}
			
			HTTPMessageHeader httpResponseMessageHeader1 = getHTTPMessageHeader("Content-Length");
			
			int httpResponseMessageHeader1Value1 = 0;
			
			if(httpResponseMessageHeader1 != null)
			{
				try
				{
					httpResponseMessageHeader1Value1 = new Integer(httpResponseMessageHeader1.getValue());
				}
				catch(Exception e)
				{
					
				}
			}
			else
			{
				httpResponseMessageHeader1Value1 = -1;
			}
			
			byte[] byteArray1;
			int byteArray1Length1;
			int byteArray1Length2;
			int byteArray1Length3;
			
			byteArray1 = new byte[5120];
			byteArray1Length1 = 0;
			byteArray1Length2 = 0;
			byteArray1Length3 = httpResponseMessageHeader1Value1;
			
			if(byteArray1Length3 > 0)
			{
				if(byteArray1Length3 >= byteArray1.length)
				{
					byteArray1Length2 = byteArray1.length;
				}
				else
				{
					byteArray1Length2 = byteArray1Length3;
				}
				
				while(byteArray1Length2 > 0 && (byteArray1Length1 = inputStream.read(byteArray1, 0, byteArray1Length2)) > 0)
				{
					outputStream.write(byteArray1, 0, byteArray1Length1);
					
					byteArray1Length3 = byteArray1Length3 - byteArray1Length1;
					
					if(byteArray1Length3 >= byteArray1.length)
					{
						byteArray1Length2 = byteArray1.length;
					}
					else
					{
						byteArray1Length2 = byteArray1Length3;
					}
				}
			}
			else
			{
				if(byteArray1Length3 < 0)
				{
					while((byteArray1Length1 = inputStream.read(byteArray1)) > 0)
					{
						outputStream.write(byteArray1, 0, byteArray1Length1);
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new HTTPMessageException("HTTP_RESPONSE_MESSAGE/READ", e);
		}
	}
}