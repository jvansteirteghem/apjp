/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTP;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import APJP.APJP;
import APJP.Logger;

public class HTTPProxyServer implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private ServerSocket serverSocket;
	private List<HTTPProxyServerWorker> httpProxyServerWorkers;
	private HTTPServer httpServer;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTP_PROXY_SERVER_LOGGER_ID);
	}
	
	public HTTPProxyServer()
	{
		httpProxyServerWorkers = Collections.synchronizedList(new LinkedList<HTTPProxyServerWorker>());
		httpServer = new HTTPServer();
	}
	
	public String getLocalAddress()
	{
		return APJP.APJP_LOCAL_HTTP_PROXY_SERVER_ADDRESS;
	}
	
	public int getLocalPort()
	{
		return APJP.APJP_LOCAL_HTTP_PROXY_SERVER_PORT;
	}
	
	public synchronized void start() throws HTTPProxyServerException 
	{
		logger.log(2, "HTTP_PROXY_SERVER/START");
		
		try
		{
			startHTTPProxyServer();
			startHTTPProxyServerWorkers();
			startHTTPServer();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/START: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/START", e);
		}
	}
	
	public synchronized void stop() throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER/STOP");
		
		try
		{
			stopHTTPProxyServer();
			stopHTTPProxyServerWorkers();
			stopHTTPServer();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/STOP: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/STOP", e);
		}
	}
	
	protected synchronized void startHTTPProxyServerWorker(HTTPProxyServerWorker httpProxyServerWorker) throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER/START_HTTP_PROXY_SERVER_WORKER");
		
		try
		{
			httpProxyServerWorker.start();
			
			httpProxyServerWorkers.add(httpProxyServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/START_HTTP_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/START_HTTP_PROXY_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopHTTPProxyServerWorker(HTTPProxyServerWorker httpProxyServerWorker) throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER_WORKER");
		
		try
		{
			httpProxyServerWorker.stop();
			
			httpProxyServerWorkers.remove(httpProxyServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void startHTTPServer() throws HTTPProxyServerException 
	{
		logger.log(2, "HTTP_PROXY_SERVER/START_HTTP_SERVER");
		
		try
		{
			httpServer.start();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/START_HTTP_SERVER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/START_HTTP_SERVER", e);
		}
	}
	
	protected synchronized void stopHTTPServer() throws HTTPProxyServerException 
	{
		logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_SERVER");
		
		try
		{
			httpServer.stop();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_SERVER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/STOP_HTTP_SERVER", e);
		}
	}
	
	protected synchronized void startHTTPProxyServer() throws HTTPProxyServerException 
	{
		logger.log(2, "HTTP_PROXY_SERVER/START_HTTP_PROXY_SERVER");
		
		try
		{
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(APJP.APJP_LOCAL_HTTP_PROXY_SERVER_ADDRESS, APJP.APJP_LOCAL_HTTP_PROXY_SERVER_PORT));
			
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/START_HTTP_PROXY_SERVER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/START_HTTP_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void stopHTTPProxyServer() throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER");
		
		try
		{
			thread = null;
			
			try
			{
				Socket outputSocket = new Socket();
				outputSocket.connect(new InetSocketAddress(APJP.APJP_LOCAL_HTTP_PROXY_SERVER_ADDRESS, APJP.APJP_LOCAL_HTTP_PROXY_SERVER_PORT));
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
			logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER", e);
		}
	}
	
	protected synchronized void startHTTPProxyServerWorkers() throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER/START_HTTP_PROXY_SERVER_WORKERS");
	}
	
	protected synchronized void stopHTTPProxyServerWorkers() throws HTTPProxyServerException
	{
		logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER_WORKERS");
		
		try
		{
			for(HTTPProxyServerWorker httpProxyServerWorker2: httpProxyServerWorkers)
			{
				httpProxyServerWorker2.stop();
			}
			
			httpProxyServerWorkers.removeAll(httpProxyServerWorkers);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER_WORKERS: EXCEPTION", e);
			
			throw new HTTPProxyServerException("HTTP_PROXY_SERVER/STOP_HTTP_PROXY_SERVER_WORKERS", e);
		}
	}
	
	protected HTTPServer getHTTPServer()
	{
		logger.log(2, "HTTP_PROXY_SERVER/GET_HTTP_SERVER");
		
		return httpServer;
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
					HTTPProxyServerWorker httpProxyServerWorker = new HTTPProxyServerWorker(this, inputSocket);
					
					startHTTPProxyServerWorker(httpProxyServerWorker);
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
					logger.log(2, "HTTP_PROXY_SERVER: EXCEPTION", e);
				}
			}
		}
	}
}
