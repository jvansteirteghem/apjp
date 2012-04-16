/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID.HTTP11;

import APJP.ANDROID.APJP;
import APJP.ANDROID.Logger;

public class HTTPRequests
{
	private static Logger logger;
	private static HTTPRequests httpRequests;
	private int i;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOGGER_ID);
		
		httpRequests = null;
	}
	
	public static synchronized HTTPRequests getHTTPRequests()
	{
		if(httpRequests == null)
		{
			httpRequests = new HTTPRequests();
		}
		
		return httpRequests;
	}
	
	protected HTTPRequests()
	{
		
	}
	
	public synchronized HTTPRequest createHTTPRequest(HTTPRequestMessage httpRequestMessage)
	{
		HTTPRequest httpRequest = null;
		
		for(int j = 0; j < APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL.length; j = j + 1)
		{
			if(APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL[i].equalsIgnoreCase("") == false)
			{
				httpRequest = new HTTPRequest(i, httpRequestMessage);
				
				j = APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL.length;
			}
			
			i = (i + 1) % APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL.length;
		}
		
		return httpRequest;
	}
	
	public void test() throws HTTPRequestException
	{
		try
		{
			HTTPRequestMessage httpRequestMessage1 = new HTTPRequestMessage(null);
			
			httpRequestMessage1.addHTTPMessageHeader(new HTTPMessageHeader("", "HEAD / HTTP/1.0"));
			httpRequestMessage1.addHTTPMessageHeader(new HTTPMessageHeader("Host", "www.google.com"));
			
			for(int i = 0; i < APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL.length; i = i + 1)
			{
				if(APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL[i].equalsIgnoreCase("") == false)
				{
					HTTPRequest httpRequest1 = new HTTPRequest(i, httpRequestMessage1);
					
					httpRequest1.open();
					
					try
					{
						httpRequest1.getHTTPResponseMessage();
					}
					catch(Exception e2)
					{
						throw e2;
					}
					finally
					{
						try
						{
							httpRequest1.close();
						}
						catch(Exception e2)
						{
							
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_REQUESTS/TEST: EXCEPTION", e);
			
			throw new HTTPRequestException("HTTP_REQUESTS/TEST", e);
		}
	}
}
