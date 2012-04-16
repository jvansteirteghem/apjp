/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTPS;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import APJP.APJP;
import APJP.Logger;
import APJP.HTTP11.HTTPMessageHeader;
import APJP.HTTP11.HTTPRequestMessage;

public class HTTPSProxyServerWorker implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private HTTPSProxyServer httpsProxyServer;
	private HTTPSServer httpsServer;
	private Socket inputSocket;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_LOGGER_ID);
	}
	
	protected HTTPSProxyServerWorker(HTTPSProxyServer httpsProxyServer, Socket inputSocket)
	{	
		this.httpsProxyServer = httpsProxyServer;
		httpsServer = null;
		
		this.inputSocket = inputSocket;
	}
	
	protected HTTPSServer getHTTPSServer()
	{
		return httpsServer;
	}
	
	protected synchronized void start() throws HTTPSProxyServerException 
	{
		logger.log(2, "HTTPS_PROXY_SERVER_WORKER/START");
		
		try
		{
			startHTTPSProxyServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_PROXY_SERVER_WORKER/START: EXCEPTION", e);
			
			throw new HTTPSProxyServerException("HTTPS_PROXY_SERVER_WORKER/START", e);
		}
	}
	
	protected synchronized void stop() throws HTTPSProxyServerException
	{
		logger.log(2, "HTTPS_PROXY_SERVER_WORKER/STOP");
		
		try
		{
			stopHTTPSProxyServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_PROXY_SERVER_WORKER/STOP: EXCEPTION", e);
			
			throw new HTTPSProxyServerException("HTTPS_PROXY_SERVER_WORKER/STOP", e);
		}
	}
	
	protected synchronized void startHTTPSProxyServerWorker() throws HTTPSProxyServerException 
	{
		logger.log(2, "HTTPS_PROXY_SERVER_WORKER/START_HTTPS_PROXY_SERVER_WORKER");
		
		try
		{
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_PROXY_SERVER_WORKER/START_HTTPS_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPSProxyServerException("HTTPS_PROXY_SERVER_WORKER/START_HTTPS_PROXY_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopHTTPSProxyServerWorker() throws HTTPSProxyServerException
	{
		logger.log(2, "HTTPS_PROXY_SERVER_WORKER/STOP_HTTPS_PROXY_SERVER_WORKER");
		
		try
		{
			thread = null;
			
			try
			{
				inputSocket.close();
			}
			catch(Exception e)
			{
				
			}
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_PROXY_SERVER_WORKER/STOP_HTTPS_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPSProxyServerException("HTTPS_PROXY_SERVER_WORKER/STOP_HTTPS_PROXY_SERVER_WORKER", e);
		}
	}
	
	public void run()
	{
		try
		{
			final Socket inputSocket = this.inputSocket;
			final InputStream inputSocketInputStream = inputSocket.getInputStream();
			final OutputStream inputSocketOutputStream = inputSocket.getOutputStream();
			
			HTTPRequestMessage httpRequestMessage1 = new HTTPRequestMessage(inputSocketInputStream);
			
			httpRequestMessage1.read();
			
			HTTPMessageHeader httpRequestMessage1Header1 = httpRequestMessage1.getHTTPMessageHeader("");
			
			if(httpRequestMessage1Header1 != null)
			{
				// CONNECT 127.0.0.1:443 HTTP/1.1
				String httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
				
				String[] httpRequestMessage1Header1Values1 = httpRequestMessage1Header1Value1.split(" ");
				
				// CONNECT
				String httpRequestMessage1Header1Value2 = httpRequestMessage1Header1Values1[0];
				
				if(httpRequestMessage1Header1Value2.equalsIgnoreCase("CONNECT"))
				{
					// 127.0.0.1:443
					String httpRequestMessage1Header1Value3 = httpRequestMessage1Header1Values1[1];
					
					String[] httpRequestMessage1Header1Values3 = httpRequestMessage1Header1Value3.split(":");
					
					// 127.0.0.1
					String httpRequestMessage1Header1Value4 = httpRequestMessage1Header1Values3[0];
					
					int httpRequestMessage1Header1Value5 = 0;
					
					try
					{
						// 443
						httpRequestMessage1Header1Value5 = new Integer(httpRequestMessage1Header1Values3[1]);
					}
					catch(Exception e)
					{
						
					}
					
					httpsServer = httpsProxyServer.getHTTPSServer(httpRequestMessage1Header1Value4, httpRequestMessage1Header1Value5);
					
					final Socket outputSocket = new Socket();
					
					try
					{
						try
						{
							outputSocket.connect(new InetSocketAddress(httpsServer.getLocalAddress(), httpsServer.getLocalPort()));
							
							inputSocketOutputStream.write(("HTTP/1.0 200 OK\r\n").getBytes());
							inputSocketOutputStream.write(("Connection: keep-alive\r\n").getBytes());
							inputSocketOutputStream.write(("\r\n").getBytes());
						}
						catch(Exception e)
						{
							inputSocketOutputStream.write(("HTTP/1.0 500 Internal Server Error\r\n").getBytes());
							inputSocketOutputStream.write(("Connection: close\r\n").getBytes());
							inputSocketOutputStream.write(("\r\n").getBytes());
							
							throw e;
						}
						
						final InputStream outputSocketInputStream = outputSocket.getInputStream();
						final OutputStream outputSocketOutputStream = outputSocket.getOutputStream();
						
						Thread thread1 = new Thread()
						{
							public void run()
							{
								try
								{
									byte[] byteArray1;
									int byteArray1Length;
									
									byteArray1 = new byte[5120];
									byteArray1Length = 0;
									
									while((byteArray1Length = inputSocketInputStream.read(byteArray1)) != -1)
									{
										outputSocketOutputStream.write(byteArray1, 0, byteArray1Length);
									}
								}
								catch(Exception e)
								{
									
								}
							}
						};
						
						thread1.start();
						
						Thread thread2 = new Thread()
						{
							public void run()
							{
								try
								{
									byte[] byteArray1;
									int byteArray1Length;
									
									byteArray1 = new byte[5120];
									byteArray1Length = 0;
									
									while((byteArray1Length = outputSocketInputStream.read(byteArray1)) != -1)
									{
										inputSocketOutputStream.write(byteArray1, 0, byteArray1Length);
									}
									
									inputSocket.shutdownInput();
									inputSocket.shutdownOutput();
									
									outputSocket.shutdownInput();
									outputSocket.shutdownOutput();
								}
								catch(Exception e)
								{
									
								}
							}
						};
						
						thread2.start();
						
						thread1.join();
						thread2.join();
					}
					catch(Exception e)
					{
						throw e;
					}
					finally
					{
						try
						{
							outputSocket.close();
						}
						catch(Exception e)
						{
							
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			if(thread != null)
			{
				logger.log(2, "HTTPS_PROXY_SERVER_WORKER: EXCEPTION", e);
			}
		}
		finally
		{
			if(thread != null)
			{
				try
				{
					httpsProxyServer.stopHTTPSProxyServerWorker(this);
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
}
