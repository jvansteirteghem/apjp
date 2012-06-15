/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import APJP.HTTP.HTTPProxyServer;
import APJP.HTTPS.HTTPSProxyServer;

public class ProxyServerWorker implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private ProxyServer proxyServer;
	private HTTPProxyServer httpProxyServer;
	private HTTPSProxyServer httpsProxyServer;
	private Socket inputSocket;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_PROXY_SERVER_LOGGER_ID);
	}
	
	protected ProxyServerWorker(ProxyServer proxyServer, Socket inputSocket)
	{
		this.proxyServer = proxyServer;
		httpProxyServer = proxyServer.getHTTPProxyServer();
		httpsProxyServer = proxyServer.getHTTPSProxyServer();
		
		this.inputSocket = inputSocket;
	}
	
	protected synchronized void start() throws ProxyServerException 
	{
		logger.log(2, "PROXY_SERVER_WORKER/START");
		
		try
		{
			startProxyServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER_WORKER/START/EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER_WORKER/START", e);
		}
	}
	
	protected synchronized void stop() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER_WORKER/STOP");
		
		try
		{
			stopProxyServerWorker();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER_WORKER/STOP: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER_WORKER/STOP", e);
		}
	}
	
	protected synchronized void startProxyServerWorker() throws ProxyServerException 
	{
		logger.log(2, "PROXY_SERVER_WORKER/START_PROXY_SERVER_WORKER");
		
		try
		{
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER_WORKER/START_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER_WORKER/START_PROXY_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopProxyServerWorker() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER_WORKER/STOP_PROXY_SERVER_WORKER");
		
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
			logger.log(2, "PROXY_SERVER_WORKER/STOP_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER_WORKER/STOP_PROXY_SERVER_WORKER", e);
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
				byte[] byteArray1;
				
				byteArray1 = new byte[7];
				
				for(int i = 0; i < byteArray1.length; i = i + 1)
				{
					inputSocketInputStream.read(byteArray1, i, 1);
				}
				
				if
				(
					byteArray1[0] == 'C' && 
					byteArray1[1] == 'O' && 
					byteArray1[2] == 'N' && 
					byteArray1[3] == 'N' &&
					byteArray1[4] == 'E' &&
					byteArray1[5] == 'C' &&
					byteArray1[6] == 'T'
				)
				{
					outputSocket.connect(new InetSocketAddress(httpsProxyServer.getLocalAddress(), httpsProxyServer.getLocalPort()));
				}
				else
				{
					outputSocket.connect(new InetSocketAddress(httpProxyServer.getLocalAddress(), httpProxyServer.getLocalPort()));
				}
				
				final InputStream outputSocketInputStream = outputSocket.getInputStream();
				final OutputStream outputSocketOutputStream = outputSocket.getOutputStream();
				
				outputSocketOutputStream.write(byteArray1);
				
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
						finally
						{
							try
							{
								outputSocket.shutdownInput();
								outputSocket.shutdownOutput();
							}
							catch(Exception e)
							{
								
							}
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
						}
						catch(Exception e)
						{
							
						}
						finally
						{
							try
							{
								inputSocket.shutdownInput();
								inputSocket.shutdownOutput();
							}
							catch(Exception e)
							{
								
							}
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
				logger.log(2, "PROXY_SERVER_WORKER: EXCEPTION", e);
			}
		}
		finally
		{
			if(thread != null)
			{
				try
				{
					proxyServer.stopProxyServerWorker(this);
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
}
