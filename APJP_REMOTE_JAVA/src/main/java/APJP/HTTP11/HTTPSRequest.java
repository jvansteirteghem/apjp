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
import java.net.InetSocketAddress;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class HTTPSRequest
{
	protected HTTPRequestMessage httpRequestMessage;
	protected SSLSocket sslSocket;
	
	public HTTPSRequest(HTTPRequestMessage httpRequestMessage)
	{
		this.httpRequestMessage = httpRequestMessage;
	}
	
	public void open() throws HTTPSRequestException
	{
		try
		{
			HTTPMessageHeader httpRequestMessage1Header1 = httpRequestMessage.getHTTPMessageHeader("Host");
			
			String httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
			
			String[] httpRequestMessage1Header1Values1 = httpRequestMessage1Header1Value1.split(":");
			
			String httpRequestMessage1Header1Value2 = httpRequestMessage1Header1Values1[0];
			
			int httpRequestMessage1Header1Value3 = 443;
			
			if(httpRequestMessage1Header1Values1.length == 2)
			{
				try
				{
					httpRequestMessage1Header1Value3 = new Integer(httpRequestMessage1Header1Values1[1]);
				}
				catch(Exception e)
				{
					
				}
			}
			
			SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			sslSocket = (SSLSocket) sslSocketFactory.createSocket();
			
			sslSocket.connect(new InetSocketAddress(httpRequestMessage1Header1Value2, httpRequestMessage1Header1Value3));
		}
		catch(Exception e)
		{
			throw new HTTPSRequestException("HTTPS_REQUEST/OPEN", e);
		}
	}
	
	public void close() throws HTTPSRequestException
	{
		try
		{
			sslSocket.close();
		}
		catch(Exception e)
		{
			throw new HTTPSRequestException("HTTPS_REQUEST/CLOSE", e);
		}
	}
	
	public HTTPResponseMessage getHTTPResponseMessage() throws HTTPSRequestException
	{
		try
		{
			OutputStream sslSocketOutputStream = sslSocket.getOutputStream();
			
			HTTPMessageHeader[] httpRequestMessage1Headers1 = httpRequestMessage.getHTTPMessageHeaders();
			
			HTTPMessageHeader httpRequestMessage1Header1 = httpRequestMessage1Headers1[0];
			
			String httpRequestMessage1Header1Key1 = httpRequestMessage1Header1.getKey();
			
			String httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
			
			sslSocketOutputStream.write((httpRequestMessage1Header1Value1 + "\r\n").getBytes());
			
			for(int i = 1; i < httpRequestMessage1Headers1.length; i = i + 1)
			{
				httpRequestMessage1Header1 = httpRequestMessage1Headers1[i];
				
				httpRequestMessage1Header1Key1 = httpRequestMessage1Header1.getKey();
				
				httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
				
				sslSocketOutputStream.write((httpRequestMessage1Header1Key1 + ": " + httpRequestMessage1Header1Value1 + "\r\n").getBytes());
			}
			
			sslSocketOutputStream.write(("\r\n").getBytes());
			
			httpRequestMessage.read(sslSocketOutputStream);
			
			InputStream sslSocketInputStream = sslSocket.getInputStream();
			
			HTTPResponseMessage httpResponseMessage1 = new HTTPResponseMessage(httpRequestMessage, sslSocketInputStream);
			
			httpResponseMessage1.read();
			
			return httpResponseMessage1;
		}
		catch(Exception e)
		{
			throw new HTTPSRequestException("HTTPS_REQUEST/GET_HTTP_RESPONSE_MESSAGE", e);
		}
	}
}
