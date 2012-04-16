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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HTTPSRequest
{
	protected HTTPRequestMessage httpRequestMessage;
	protected URL httpURL;
	protected HttpURLConnection httpURLConnection;
	
	public HTTPSRequest(HTTPRequestMessage httpRequestMessage)
	{
		this.httpRequestMessage = httpRequestMessage;
	}
	
	public void open() throws HTTPSRequestException
	{
		try
		{
			HTTPMessageHeader httpRequestMessage1Header1 = httpRequestMessage.getHTTPMessageHeader("");
			
			String httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
			
			String[] httpRequestMessage1Header1Values1 = httpRequestMessage1Header1Value1.split(" ");
			
			String httpRequestMessage1Header1Value2 = httpRequestMessage1Header1Values1[0];
			
			String httpRequestMessage1Header1Value3 = httpRequestMessage1Header1Values1[1];
			
			HTTPMessageHeader httpRequestMessage1Header2 = httpRequestMessage.getHTTPMessageHeader("Host");
			
			String httpRequestMessage1Header2Value1 = httpRequestMessage1Header2.getValue();
			
			httpURL = new URL("https://" + httpRequestMessage1Header2Value1 + httpRequestMessage1Header1Value3);
			
			httpURLConnection = (HttpURLConnection) httpURL.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(55000);
			httpURLConnection.setInstanceFollowRedirects(false);
			
			if
			(
				httpRequestMessage1Header1Value2.equalsIgnoreCase("POST") ||
				httpRequestMessage1Header1Value2.equalsIgnoreCase("PUT")
			)
	        {
				httpURLConnection.setDoOutput(true);
	        }
			
			httpURLConnection.setDoInput(true);
			
			httpURLConnection.setRequestMethod(httpRequestMessage1Header1Value2);
			
			HTTPMessageHeader[] httpRequestMessage1Headers3 = httpRequestMessage.getHTTPMessageHeaders();
			for(HTTPMessageHeader httpRequestMessage1Header3: httpRequestMessage1Headers3)
			{
				String httpRequestMessage1Header3Key1 = httpRequestMessage1Header3.getKey();
				String httpRequestMessage1Header3Value1 = httpRequestMessage1Header3.getValue();
				
				httpURLConnection.setRequestProperty(httpRequestMessage1Header3Key1, httpRequestMessage1Header3Value1);
			}
			
			if(httpURLConnection.getDoOutput())
	        {
				OutputStream httpRequestOutputStream = httpURLConnection.getOutputStream();
				
				httpRequestMessage.read(httpRequestOutputStream);
	        }
			
			httpURLConnection.connect();
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
			httpURLConnection.disconnect();
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
			InputStream httpResponseInputStream = httpURLConnection.getInputStream();
			
			HTTPResponseMessage httpResponseMessage1 = new HTTPResponseMessage(httpRequestMessage, httpResponseInputStream);
			
			httpResponseMessage1.addHTTPMessageHeader(new HTTPMessageHeader("", "HTTP/1.1 " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage()));
			
			Map<String, List<String>> httpResponseHeaderMap = httpURLConnection.getHeaderFields();
			Set<Entry<String, List<String>>> httpResponseHeaderEntrySet = httpResponseHeaderMap.entrySet();
			for(Entry<String, List<String>> httpResponseHeaderEntry: httpResponseHeaderEntrySet)
			{
				String httpResponseHeaderEntryKey = httpResponseHeaderEntry.getKey();
				List<String> httpResponseHeaderEntryValueList = httpResponseHeaderEntry.getValue();
				
				if(httpResponseHeaderEntryKey.equalsIgnoreCase("Set-Cookie"))
				{
					String httpResponseHeaderEntryValue1 = "";
					
					for(String httpResponseHeaderEntryValue2: httpResponseHeaderEntryValueList)
					{
						if(httpResponseHeaderEntryValue1.equalsIgnoreCase(""))
						{
							httpResponseHeaderEntryValue1 = httpResponseHeaderEntryValue2;
						}
						else
						{
							String[] httpResponseHeaderEntryValues2 = httpResponseHeaderEntryValue2.split(";");
							
							String httpResponseHeaderEntryValue3 = httpResponseHeaderEntryValues2[0];
							
							String[] httpResponseHeaderEntryValues3 = httpResponseHeaderEntryValue3.split(" ");
							
							if(httpResponseHeaderEntryValues3.length == 3)
							{
								httpResponseHeaderEntryValue1 = httpResponseHeaderEntryValue1 + ", " + httpResponseHeaderEntryValue2;
							}
							else
							{
								httpResponseMessage1.addHTTPMessageHeader(new HTTPMessageHeader(httpResponseHeaderEntryKey, httpResponseHeaderEntryValue1));
								
								httpResponseHeaderEntryValue1 = httpResponseHeaderEntryValue2;
							}
						}
					}
					
					httpResponseMessage1.addHTTPMessageHeader(new HTTPMessageHeader(httpResponseHeaderEntryKey, httpResponseHeaderEntryValue1));
					
				}
				else
				{
					String httpResponseHeaderEntryValue1 = "";
					
					for(String httpResponseHeaderEntryValue2: httpResponseHeaderEntryValueList)
					{
						if(httpResponseHeaderEntryValue1.equalsIgnoreCase(""))
						{
							httpResponseHeaderEntryValue1 = httpResponseHeaderEntryValue2;
						}
						else
						{
							httpResponseHeaderEntryValue1 = httpResponseHeaderEntryValue1 + ", " + httpResponseHeaderEntryValue2;
						}
					}
					
					httpResponseMessage1.addHTTPMessageHeader(new HTTPMessageHeader(httpResponseHeaderEntryKey, httpResponseHeaderEntryValue1));
				}
			}
			
			return httpResponseMessage1;
		}
		catch(Exception e)
		{
			throw new HTTPSRequestException("HTTPS_REQUEST/GET_HTTP_RESPONSE_MESSAGE", e);
		}
	}
}
