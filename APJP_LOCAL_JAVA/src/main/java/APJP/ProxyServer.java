/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import APJP.HTTP.HTTPProxyServer;
import APJP.HTTPS.HTTPSProxyServer;

public class ProxyServer implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private ServerSocket serverSocket;
	private List<ProxyServerWorker> proxyServerWorkers;
	private HTTPProxyServer httpProxyServer;
	private HTTPSProxyServer httpsProxyServer;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_PROXY_SERVER_LOGGER_ID);
	}
	
	public ProxyServer()
	{
		proxyServerWorkers = Collections.synchronizedList(new LinkedList<ProxyServerWorker>());
		httpProxyServer = new HTTPProxyServer();
		httpsProxyServer = new HTTPSProxyServer();
	}
	
	public String getLocalAddress()
	{
		return APJP.APJP_LOCAL_PROXY_SERVER_ADDRESS;
	}
	
	public int getLocalPort()
	{
		return APJP.APJP_LOCAL_PROXY_SERVER_PORT;
	}
	
	public synchronized void start() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/START");
		
		try
		{
			startProxyServer();
			startProxyServerWorkers();
			startHTTPProxyServer();
			startHTTPSProxyServer();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/START: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/START", e);
		}
	}
	
	public synchronized void stop() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/STOP");
		
		try
		{
			stopProxyServer();
			stopProxyServerWorkers();
			stopHTTPProxyServer();
			stopHTTPSProxyServer();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/STOP: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/STOP", e);
		}
	}
	
	protected synchronized void startProxyServerWorker(ProxyServerWorker proxyServerWorker) throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/START_PROXY_SERVER_WORKER");
		
		try
		{
			proxyServerWorker.start();
			
			proxyServerWorkers.add(proxyServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/START_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/START_PROXY_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopProxyServerWorker(ProxyServerWorker proxyServerWorker) throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/STOP_PROXY_SERVER_WORKER");
		
		try
		{
			proxyServerWorker.stop();
			
			proxyServerWorkers.remove(proxyServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/STOP_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/STOP_PROXY_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void startHTTPProxyServer() throws ProxyServerException 
	{
		logger.log(2, "PROXY_SERVER/START_HTTP_PROXY_SERVER");
		
		try
		{
			httpProxyServer.start();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/START_HTTP_PROXY_SERVER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/START_HTTP_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void stopHTTPProxyServer() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/STOP_HTTP_PROXY_SERVER");
		
		try
		{
			httpProxyServer.stop();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/STOP_HTTP_PROXY_SERVER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/STOP_HTTP_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void startHTTPSProxyServer() throws ProxyServerException 
	{
		logger.log(2, "PROXY_SERVER/START_HTTPS_PROXY_SERVER");
		
		try
		{
			httpsProxyServer.start();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/START_HTTPS_PROXY_SERVER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/START_HTTPS_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void stopHTTPSProxyServer() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/STOP_HTTPS_PROXY_SERVER");
		
		try
		{
			httpsProxyServer.stop();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/STOP_HTTPS_PROXY_SERVER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/STOP_HTTPS_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void startProxyServer() throws ProxyServerException 
	{
		logger.log(2, "PROXY_SERVER/START_PROXY_SERVER");
		
		try
		{
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(APJP.APJP_LOCAL_PROXY_SERVER_ADDRESS, APJP.APJP_LOCAL_PROXY_SERVER_PORT));
			
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/START_PROXY_SERVER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/START_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void stopProxyServer() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/STOP_PROXY_SERVER");
		
		try
		{
			thread = null;
			
			try
			{
				Socket outputSocket = new Socket();
				outputSocket.connect(new InetSocketAddress(APJP.APJP_LOCAL_PROXY_SERVER_ADDRESS, APJP.APJP_LOCAL_PROXY_SERVER_PORT));
				outputSocket.close();
			}
			catch(Exception e)
			{
				
			}
			
			try
			{
				serverSocket.close();
			}
			catch(Exception e)
			{
				
			}
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/STOP_PROXY_SERVER: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/STOP_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void startProxyServerWorkers() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/START_PROXY_SERVER_WORKERS");
	}
	
	protected synchronized void stopProxyServerWorkers() throws ProxyServerException
	{
		logger.log(2, "PROXY_SERVER/STOP_PROXY_SERVER_WORKERS");
		
		try
		{
			for(ProxyServerWorker proxyServerWorker2: proxyServerWorkers)
			{
				proxyServerWorker2.stop();
			}
			
			proxyServerWorkers.removeAll(proxyServerWorkers);
		}
		catch(Exception e)
		{
			logger.log(2, "PROXY_SERVER/STOP_PROXY_SERVER_WORKERS: EXCEPTION", e);
			
			throw new ProxyServerException("PROXY_SERVER/STOP_PROXY_SERVER_WORKERS", e);
		}
	}
	
	protected HTTPProxyServer getHTTPProxyServer()
	{
		logger.log(2, "PROXY_SERVER/GET_HTTP_PROXY_SERVER");
		
		return httpProxyServer;
	}
	
	protected HTTPSProxyServer getHTTPSProxyServer()
	{
		logger.log(2, "PROXY_SERVER/GET_HTTPS_PROXY_SERVER");
		
		return httpsProxyServer;
	}
	
	public void run()
	{
		while(thread != null)
		{
			try
			{
				Socket inputSocket = serverSocket.accept();
				
				if(thread != null)
				{
					ProxyServerWorker proxyServerWorker = new ProxyServerWorker(this, inputSocket);
					
					startProxyServerWorker(proxyServerWorker);
				}
				else
				{
					inputSocket.close();
				}
			}
			catch(Exception e)
			{
				if(thread != null)
				{
					logger.log(2, "PROXY_SERVER: EXCEPTION", e);
				}
			}
		}
	}
}
