package APJP.HTTP11;

import APJP.APJP;
import APJP.Logger;

public class HTTPSRequests
{
	private static Logger logger;
	private static HTTPSRequests httpsRequests;
	private int i;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOGGER_ID);
		
		httpsRequests = null;
	}
	
	public static synchronized HTTPSRequests getHTTPSRequests()
	{
		if(httpsRequests == null)
		{
			httpsRequests = new HTTPSRequests();
		}
		
		return httpsRequests;
	}
	
	protected HTTPSRequests()
	{
		
	}
	
	public synchronized HTTPSRequest createHTTPSRequest(HTTPRequestMessage httpRequestMessage)
	{
		HTTPSRequest httpsRequest = null;
		
		for(int j = 0; j < APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL.length; j = j + 1)
		{
			if(APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL[i].equalsIgnoreCase("") == false)
			{
				httpsRequest = new HTTPSRequest(i, httpRequestMessage);
				
				j = APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL.length;
			}
			
			i = (i + 1) % APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL.length;
		}
		
		return httpsRequest;
	}
	
	public void test() throws HTTPSRequestException
	{
		try
		{
			HTTPRequestMessage httpRequestMessage1 = new HTTPRequestMessage(null);
			
			httpRequestMessage1.addHTTPMessageHeader(new HTTPMessageHeader("", "HEAD / HTTP/1.0"));
			httpRequestMessage1.addHTTPMessageHeader(new HTTPMessageHeader("Host", "www.google.com"));
			
			for(int i = 0; i < APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL.length; i = i + 1)
			{
				if(APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL[i].equalsIgnoreCase("") == false)
				{
					HTTPSRequest httpsRequest1 = new HTTPSRequest(i, httpRequestMessage1);
					
					httpsRequest1.open();
					
					try
					{
						httpsRequest1.getHTTPResponseMessage();
					}
					catch(Exception e2)
					{
						throw e2;
					}
					finally
					{
						try
						{
							httpsRequest1.close();
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
			logger.log(2, "HTTPS_REQUESTS/TEST: EXCEPTION", e);
			
			throw new HTTPSRequestException("HTTPS_REQUESTS/TEST", e);
		}
	}
}
