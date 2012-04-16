/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID.HTTPS;

import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;

import APJP.ANDROID.APJP;
import APJP.ANDROID.Logger;
import APJP.ANDROID.HTTP11.HTTPMessageHeader;
import APJP.ANDROID.HTTP11.HTTPRequestMessage;
import APJP.ANDROID.HTTP11.HTTPResponseMessage;
import APJP.ANDROID.HTTP11.HTTPSRequest;
import APJP.ANDROID.HTTP11.HTTPSRequests;

public class HTTPSServerWorker implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private HTTPSServer httpsServer;
	private SSLSocket inputSSLSocket;
	private boolean keepAlive;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_ID);
	}
	
	protected HTTPSServerWorker(HTTPSServer httpsServer, SSLSocket inputSSLSocket)
	{
		this.httpsServer = httpsServer;
		
		this.inputSSLSocket = inputSSLSocket;
	}
	
	protected synchronized void start() throws HTTPSServerException 
	{
		logger.log(2, "HTTPS_SERVER_WORKER/START");
		
		try
		{
			startHTTPSServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER_WORKER/START: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER_WORKER/START", e);
		}
	}
	
	protected synchronized void stop() throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER_WORKER/STOP");
		
		try
		{
			stopHTTPSServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER_WORKER/STOP: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER_WORKER/STOP", e);
		}
	}
	
	protected synchronized void startHTTPSServerWorker() throws HTTPSServerException 
	{
		logger.log(2, "HTTPS_SERVER_WORKER/START_HTTPS_SERVER_WORKER");
		
		try
		{
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER_WORKER/START_HTTPS_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER_WORKER/START_HTTPS_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopHTTPSServerWorker() throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER_WORKER/STOP_HTTPS_SERVER_WORKER");
		
		try
		{
			thread = null;
			
			try
			{
				inputSSLSocket.close();
			}
			catch(Exception e)
			{
				
			}
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER_WORKER/STOP_HTTPS_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER_WORKER/STOP_HTTPS_SERVER_WORKER", e);
		}
	}
	
	public void run()
	{
		try
		{
			do
			{
				keepAlive = true;
				
				process();
			}
			while(keepAlive);
		}
		catch(Exception e)
		{
			if(thread != null)
			{
				logger.log(2, "HTTPS_SERVER_WORKER: EXCEPTION", e);
			}
		}
		finally
		{
			if(thread != null)
			{
				try
				{
					httpsServer.stopHTTPSServerWorker(this);
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
	
	protected void processHTTPRequestMessage(HTTPRequestMessage httpRequestMessage) throws Exception
	{
		HTTPMessageHeader httpRequestMessage1Header01 = httpRequestMessage.getHTTPMessageHeader("Proxy-Connection");
		
		if(httpRequestMessage1Header01 != null)
		{
			String httpRequestMessage1Header1Value01 = httpRequestMessage1Header01.getValue();
			
			if(httpRequestMessage1Header1Value01.equalsIgnoreCase("close"))
			{
				keepAlive = false;
			}
		}
		
		HTTPMessageHeader httpRequestMessage1Header02 = httpRequestMessage.getHTTPMessageHeader("Connection");
		
		if(httpRequestMessage1Header02 != null)
		{
			String httpRequestMessage1Header1Value02 = httpRequestMessage1Header02.getValue();
			
			if(httpRequestMessage1Header1Value02.equalsIgnoreCase("close"))
			{
				keepAlive = false;
			}
		}
	}
	
	protected void processHTTPResponseMessage(HTTPResponseMessage httpResponseMessage) throws Exception
	{
		HTTPMessageHeader httpResponseMessageHeader1 = httpResponseMessage.getHTTPMessageHeader("Content-Length");
		
		if(httpResponseMessageHeader1 == null)
		{
			keepAlive = false;
		}
		
		HTTPMessageHeader httpResponseMessageHeader2 = httpResponseMessage.getHTTPMessageHeader("Proxy-Connection");
		
		if(httpResponseMessageHeader2 != null)
		{
			httpResponseMessage.removeHTTPMessageHeader(httpResponseMessageHeader2);
		}
		
		if(keepAlive == true)
		{
			httpResponseMessage.addHTTPMessageHeader(new HTTPMessageHeader("Proxy-Connection", "Keep-Alive"));
		}
		else
		{
			httpResponseMessage.addHTTPMessageHeader(new HTTPMessageHeader("Proxy-Connection", "close"));
		}
		
		HTTPMessageHeader httpResponseMessageHeader3 = httpResponseMessage.getHTTPMessageHeader("Connection");
		
		if(httpResponseMessageHeader3 != null)
		{
			httpResponseMessage.removeHTTPMessageHeader(httpResponseMessageHeader3);
		}
		
		if(keepAlive == true)
		{
			httpResponseMessage.addHTTPMessageHeader(new HTTPMessageHeader("Connection", "Keep-Alive"));
		}
		else
		{
			httpResponseMessage.addHTTPMessageHeader(new HTTPMessageHeader("Connection", "close"));
		}
	}
	
	public void process() throws Exception
	{
		InputStream inputSSLSocketInputStream = inputSSLSocket.getInputStream();
		OutputStream inputSSLSocketOutputStream = inputSSLSocket.getOutputStream();
		
		HTTPSRequests httpsRequests = HTTPSRequests.getHTTPSRequests();
		
		HTTPRequestMessage httpRequestMessage1 = new HTTPRequestMessage(inputSSLSocketInputStream);
		
		httpRequestMessage1.read();
		
		HTTPMessageHeader httpRequestMessage1Header1 = httpRequestMessage1.getHTTPMessageHeader("");
		
		if(httpRequestMessage1Header1 == null)
		{
			keepAlive = false;
			
			return;
		}
		
		String httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
		
		if(httpRequestMessage1Header1Value1.equalsIgnoreCase(""))
		{
			keepAlive = false;
			
			return;
		}
		
		processHTTPRequestMessage(httpRequestMessage1);
		
		HTTPResponseMessage httpResponseMessage1 = null;
		
		try
		{
			HTTPSRequest httpsRequest1 = httpsRequests.createHTTPSRequest(httpRequestMessage1);
			
			httpsRequest1.open();
			
			try
			{
				httpResponseMessage1 = httpsRequest1.getHTTPResponseMessage();
				
				processHTTPResponseMessage(httpResponseMessage1);
				
				HTTPMessageHeader[] httpResponseMessage1Headers1 = httpResponseMessage1.getHTTPMessageHeaders();
				
				HTTPMessageHeader httpResponseMessage1Header1 = httpResponseMessage1Headers1[0];
				
				String httpResponseMessage1Header1Key1 = httpResponseMessage1Header1.getKey();
				
				String httpResponseMessage1Header1Value1 = httpResponseMessage1Header1.getValue();
				
				inputSSLSocketOutputStream.write((httpResponseMessage1Header1Value1 + "\r\n").getBytes());
				
				for(int i = 1; i < httpResponseMessage1Headers1.length; i = i + 1)
				{
					httpResponseMessage1Header1 = httpResponseMessage1Headers1[i];
					
					httpResponseMessage1Header1Key1 = httpResponseMessage1Header1.getKey();
					
					httpResponseMessage1Header1Value1 = httpResponseMessage1Header1.getValue();
					
					inputSSLSocketOutputStream.write((httpResponseMessage1Header1Key1 + ": " + httpResponseMessage1Header1Value1 + "\r\n").getBytes());
				}
				
				inputSSLSocketOutputStream.write(("\r\n").getBytes());
				
				httpResponseMessage1.read(inputSSLSocketOutputStream);
			}
			catch(Exception e)
			{
				throw e;
			}
			finally
			{
				try
				{
					httpsRequest1.close();
				}
				catch(Exception e)
				{
					
				}
			}
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER_WORKER/PROCESS: EXCEPTION", e);
			
			if(httpResponseMessage1 == null)
			{
				String[] httpRequestMessage1Header1Values1 = httpRequestMessage1Header1Value1.split(" ");
				
				String httpRequestMessage1Header1Value2 = httpRequestMessage1Header1Values1[0];
				
				if(!httpRequestMessage1Header1Value2.equalsIgnoreCase("GET"))
				{
					throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS: REQUEST/METHOD != \"GET\", REQUEST/METHOD == \"" + httpRequestMessage1Header1Value2 + "\"");
				}
				
				HTTPMessageHeader httpRequestMessage1Header2 = httpRequestMessage1.getHTTPMessageHeader("If-Range");
				
				if(httpRequestMessage1Header2 != null)
				{
					String httpRequestMessage1Header2Value1 = httpRequestMessage1Header2.getValue();
					
					throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS: REQUEST/IF_RANGE != \"\", REQUEST/IF_RANGE == \"" + httpRequestMessage1Header2Value1 + "\"");
				}
				
				HTTPMessageHeader httpRequestMessage1Header3 = httpRequestMessage1.getHTTPMessageHeader("Range");
				
				if(httpRequestMessage1Header3 != null)
				{
					String httpRequestMessage1Header3Value1 = httpRequestMessage1Header3.getValue();
					
					throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS: REQUEST/RANGE != \"\", REQUEST/RANGE == \"" + httpRequestMessage1Header3Value1 + "\"");
				}
				
				httpRequestMessage1Header1Values1[0] = "HEAD";
				
				httpRequestMessage1Header1Value1 = httpRequestMessage1Header1Values1[0];
				
				for(int i = 1; i < httpRequestMessage1Header1Values1.length; i = i + 1)
				{
					httpRequestMessage1Header1Value1 = httpRequestMessage1Header1Value1 + " " + httpRequestMessage1Header1Values1[i];
				}
				
				httpRequestMessage1Header1.setValue(httpRequestMessage1Header1Value1);
				
				HTTPSRequest httpsRequest1 = httpsRequests.createHTTPSRequest(httpRequestMessage1);
				
				httpsRequest1.open();
				
				try
				{
					httpResponseMessage1 = httpsRequest1.getHTTPResponseMessage();
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
				
				HTTPMessageHeader httpResponseMessage1Header1 = httpResponseMessage1.getHTTPMessageHeader("Accept-Ranges");
				
				if(httpResponseMessage1Header1 == null)
				{
					throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS: RESPONSE/ACCEPT_RANGES != \"bytes\", RESPONSE/ACCEPT_RANGES == \"\"");
				}
				
				String httpResponseMessage1Header1Value1 = httpResponseMessage1Header1.getValue();
				
				if(!httpResponseMessage1Header1Value1.equalsIgnoreCase("bytes"))
				{
					throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS: RESPONSE/ACCEPT_RANGES != \"bytes\", RESPONSE/ACCEPT_RANGES == \"" + httpResponseMessage1Header1Value1 + "\"");
				}
				
				processHTTPResponseMessage(httpResponseMessage1);
				
				HTTPMessageHeader[] httpResponseMessage1Headers1 = httpResponseMessage1.getHTTPMessageHeaders();
				
				HTTPMessageHeader httpResponseMessage1Header2 = httpResponseMessage1Headers1[0];
				
				String httpResponseMessage1Header2Key1 = httpResponseMessage1Header2.getKey();
				
				String httpResponseMessage1Header2Value1 = httpResponseMessage1Header2.getValue();
				
				inputSSLSocketOutputStream.write((httpResponseMessage1Header2Value1 + "\r\n").getBytes());
				
				for(int i = 1; i < httpResponseMessage1Headers1.length; i = i + 1)
				{
					httpResponseMessage1Header2 = httpResponseMessage1Headers1[i];
					
					httpResponseMessage1Header2Key1 = httpResponseMessage1Header2.getKey();
					
					httpResponseMessage1Header2Value1 = httpResponseMessage1Header2.getValue();
					
					inputSSLSocketOutputStream.write((httpResponseMessage1Header2Key1 + ": " + httpResponseMessage1Header2Value1 + "\r\n").getBytes());
				}
				
				inputSSLSocketOutputStream.write(("\r\n").getBytes());
				
				HTTPMessageHeader httpResponseMessage1Header3 = httpResponseMessage1.getHTTPMessageHeader("Content-Length");
				
				int httpResponseMessage1Header3Value1 = 0;
				
				if(httpResponseMessage1Header3 != null)
				{
					try
					{
						httpResponseMessage1Header3Value1 = new Integer(httpResponseMessage1Header3.getValue());
					}
					catch(Exception e2)
					{
						
					}
				}
				
				httpRequestMessage1Header1Values1[0] = "GET";
				
				httpRequestMessage1Header1Value1 = httpRequestMessage1Header1Values1[0];
				
				for(int i = 1; i < httpRequestMessage1Header1Values1.length; i = i + 1)
				{
					httpRequestMessage1Header1Value1 = httpRequestMessage1Header1Value1 + " " + httpRequestMessage1Header1Values1[i];
				}
				
				httpRequestMessage1Header1.setValue(httpRequestMessage1Header1Value1);
				
				long i = 0;
				long j = 1048576;
				
				if(j > httpResponseMessage1Header3Value1 - 1)
				{
					j = httpResponseMessage1Header3Value1 - 1;
				}
				
				while(i < httpResponseMessage1Header3Value1 - 1)
				{
					HTTPMessageHeader httpRequestMessage1Header4 = httpRequestMessage1.getHTTPMessageHeader("Range");
					
					if(httpRequestMessage1Header4 != null)
					{
						httpRequestMessage1.removeHTTPMessageHeader(httpRequestMessage1Header4);
					}
					
					httpRequestMessage1.addHTTPMessageHeader(new HTTPMessageHeader("Range", "bytes=" + i + "-" + j));
					
					HTTPResponseMessage httpResponseMessage2 = null;
					
					int k = 1;
					
					while(k <= 5)
					{	
						try
						{
							HTTPSRequest httpsRequest2 = httpsRequests.createHTTPSRequest(httpRequestMessage1);
							
							httpsRequest2.open();
							
							try
							{
								httpResponseMessage2 = httpsRequest2.getHTTPResponseMessage();
								
								HTTPMessageHeader httpResponseMessage2Header1 = httpResponseMessage2.getHTTPMessageHeader("Content-Range");
								
								if(httpResponseMessage2Header1 == null)
								{
									throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS: RESPONSE/CONTENT_RANGE != \"bytes " + i + "-" + j + "/" + httpResponseMessage1Header3Value1 + "\", RESPONSE/CONTENT_RANGE == \"\"");
								}
								
								String httpResponseMessage2Header1Value1 = httpResponseMessage2Header1.getValue();
								
								if(!httpResponseMessage2Header1Value1.equalsIgnoreCase("bytes " + i + "-" + j + "/" + httpResponseMessage1Header3Value1))
								{
									throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS: RESPONSE/CONTENT_RANGE != \"bytes " + i + "-" + j + "/" + httpResponseMessage1Header3Value1 + "\", RESPONSE/CONTENT_RANGE == \"" + httpResponseMessage2Header1Value1 + "\"");
								}
								
								httpResponseMessage2.read(inputSSLSocketOutputStream);
							}
							catch(Exception e2)
							{	
								throw e;
							}
							finally
							{
								try
								{
									httpsRequest2.close();
								}
								catch(Exception e2)
								{
									
								}
							}
							
							k = 5;
						}
						catch(Exception e2)
						{	
							logger.log(2, "HTTPS_SERVER_WORKER/PROCESS: EXCEPTION", e2);
							
							if(k == 5)
							{
								throw new HTTPSServerException("HTTPS_SERVER_WORKER/PROCESS");
							}
						}
						
						k = k + 1;
					}
					
					i = j + 1;
					j = j + 1 + 1048576;
					
					if(j > httpResponseMessage1Header3Value1 - 1)
					{
						j = httpResponseMessage1Header3Value1 - 1;
					}
				}
			}
		}
	}
}
