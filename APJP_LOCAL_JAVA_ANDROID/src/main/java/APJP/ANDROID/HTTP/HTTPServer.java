/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID.HTTP;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import APJP.ANDROID.APJP;
import APJP.ANDROID.Logger;

public class HTTPServer implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private ServerSocket serverSocket;
	private List<HTTPServerWorker> httpServerWorkers;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTP_SERVER_LOGGER_ID);
	}
	
	public HTTPServer()
	{
		httpServerWorkers = Collections.synchronizedList(new LinkedList<HTTPServerWorker>());
	}
	
	public String getLocalAddress()
	{
		return APJP.APJP_LOCAL_HTTP_SERVER_ADDRESS;
	}
	
	public int getLocalPort()
	{
		return serverSocket.getLocalPort();
	}
	
	public synchronized void start() throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/START");
		
		try
		{
			startHTTPServer();
			startHTTPServerWorkers();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_SERVER/START: EXCEPTION", e);
			
			throw new HTTPServerException("HTTP_SERVER/START", e);
		}
	}
	
	public synchronized void stop() throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/STOP");
		
		try
		{
			stopHTTPServer();
			stopHTTPServerWorkers();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_SERVER/STOP: EXCEPTION", e);
			
			throw new HTTPServerException("HTTP_SERVER/STOP", e);
		}
	}
	
	protected synchronized void startHTTPServerWorker(HTTPServerWorker httpServerWorker) throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/START_HTTP_SERVER_WORKER");
		
		try
		{
			httpServerWorker.start();
			
			httpServerWorkers.add(httpServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_SERVER/START_HTTP_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPServerException("HTTP_SERVER/START_HTTP_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopHTTPServerWorker(HTTPServerWorker httpServerWorker) throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/STOP_HTTP_SERVER_WORKER");
		
		try
		{
			httpServerWorker.stop();
			
			httpServerWorkers.remove(httpServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_SERVER/STOP_HTTP_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPServerException("HTTP_SERVER/STOP_HTTP_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void startHTTPServer() throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/START_HTTP_SERVER");
		
		try
		{
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(APJP.APJP_LOCAL_HTTP_SERVER_ADDRESS, APJP.APJP_LOCAL_HTTP_SERVER_PORT));
			
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_SERVER/START_HTTP_SERVER: EXCEPTION", e);
			
			throw new HTTPServerException("HTTP_SERVER/START_HTTP_SERVER", e);
		}
	}
	
	protected synchronized void stopHTTPServer() throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/STOP_HTTP_SERVER");
		
		try
		{
			thread = null;
			
			try
			{
				Socket outputSocket = new Socket();
				outputSocket.connect(new InetSocketAddress(APJP.APJP_LOCAL_HTTP_SERVER_ADDRESS, serverSocket.getLocalPort()));
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
			logger.log(2, "HTTP_SERVER/STOP_HTTP_SERVER: EXCEPTION", e);
			
			throw new HTTPServerException("HTTP_SERVER/STOP_HTTP_SERVER", e);
		}
	}
	
	protected synchronized void startHTTPServerWorkers() throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/START_HTTP_SERVER_WORKERS");
	}
	
	protected synchronized void stopHTTPServerWorkers() throws HTTPServerException
	{
		logger.log(2, "HTTP_SERVER/STOP_HTTP_SERVER_WORKERS");
		
		try
		{
			for(HTTPServerWorker httpServerWorker2: httpServerWorkers)
			{
				httpServerWorker2.stop();
			}
			
			httpServerWorkers.removeAll(httpServerWorkers);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTP_SERVER/STOP_HTTP_SERVER_WORKERS: EXCEPTION", e);
			
			throw new HTTPServerException("HTTP_SERVER/STOP_HTTP_SERVER_WORKERS", e);
		}
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
					HTTPServerWorker httpServerWorker = new HTTPServerWorker(this, inputSocket);
					
					startHTTPServerWorker(httpServerWorker);
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
					logger.log(2, "HTTP_SERVER: EXCEPTION", e);
				}
			}
		}
	}
}
