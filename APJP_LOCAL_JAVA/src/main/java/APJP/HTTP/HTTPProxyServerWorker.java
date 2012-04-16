/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import APJP.APJP;
import APJP.Logger;

public class HTTPProxyServerWorker implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private HTTPProxyServer httpProxyServer;
	private HTTPServer httpServer;
	private Socket inputSocket;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTP_PROXY_SERVER_LOGGER_ID);
	}
	
	protected HTTPProxyServerWorker(HTTPProxyServer httpProxyServer, Socket inputSocket)
	{
		this.httpProxyServer = httpProxyServer;
		httpServer = httpProxyServer.getHTTPServer();
		
		this.inputSocket = inputSocket;
	}
	
	protected synchronized void start() throws HTTPProxyServerException 
	{
		logger.log(2, "HTTP_PROXY_SERVER_WORKER/START");
		
		try
		{
			startHTTPProxyServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER_WORKER/START: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER_WORKER/START", e);
		}
	}
	
	protected synchronized void stop() throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER_WORKER/STOP");
		
		try
		{
			stopHTTPProxyServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER_WORKER/STOP: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER_WORKER/STOP", e);
		}
	}
	
	protected synchronized void startHTTPProxyServerWorker() throws HTTPProxyServerException 
	{
		logger.log(2, "HTTP_PROXY_SERVER_WORKER/START_HTTP_PROXY_SERVER_WORKER");
		
		try
		{
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER_WORKER/START_HTTP_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER_WORKER/START_HTTP_PROXY_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopHTTPProxyServerWorker() throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER_WORKER/STOP_HTTP_PROXY_SERVER_WORKER");
		
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
			logger.log(2, "HTTP_PROXY_SERVER_WORKER/STOP_HTTP_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER_WORKER/STOP_HTTP_PROXY_SERVER_WORKER", e);
		}
	}
	
	public void run()
	{
		try
		{
			final Socket inputSocket = this.inputSocket;
			final InputStream inputSocketInputStream = inputSocket.getInputStream();
			final OutputStream inputSocketOutputStream = inputSocket.getOutputStream();
			
			final Socket outputSocket = new Socket();
			
			try
			{
				outputSocket.connect(new InetSocketAddress(httpServer.getLocalAddress(), httpServer.getLocalPort()));
				
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
		catch(Exception e)
		{
			if(thread != null)
			{
				logger.log(2, "HTTP_PROXY_SERVER_WORKER: EXCEPTION", e);
			}
		}
		finally
		{
			if(thread != null)
			{
				try
				{
					httpProxyServer.stopHTTPProxyServerWorker(this);
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
}
