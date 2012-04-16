/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTPS;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import APJP.APJP;
import APJP.Logger;

public class HTTPSServer implements Runnable
{
	private static Logger logger;
	private Thread thread;
	private SSLServerSocket sslServerSocket;
	private List<HTTPSServerWorker> httpsServerWorkers;
	private String remoteAddress;
	private int remotePort;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_ID);
	}
	
	public HTTPSServer(String remoteAddress, int remotePort)
	{
		httpsServerWorkers = Collections.synchronizedList(new LinkedList<HTTPSServerWorker>());
		
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
	}
	
	public String getLocalAddress()
	{
		return APJP.APJP_LOCAL_HTTPS_SERVER_ADDRESS;
	}
	
	public int getLocalPort()
	{
		return sslServerSocket.getLocalPort();
	}
	
	public String getRemoteAddress()
	{
		return remoteAddress;
	}
	
	public int getRemotePort()
	{
		return remotePort;
	}
	
	public synchronized void start() throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER/START");
		
		try
		{
			startHTTPSServer();
			startHTTPSServerWorkers();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER/START: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER/START", e);
		}
	}
	
	public synchronized void stop() throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER/STOP");
		
		try
		{
			stopHTTPSServer();
			stopHTTPSServerWorkers();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER/STOP: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER/STOP", e);
		}
	}
	
	protected synchronized void startHTTPSServerWorker(HTTPSServerWorker httpsServerWorker) throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER/START_HTTPS_SERVER_WORKER");
		
		try
		{
			httpsServerWorker.start();
			
			httpsServerWorkers.add(httpsServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER/START_HTTPS_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER/START_HTTPS_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void stopHTTPSServerWorker(HTTPSServerWorker httpsServerWorker) throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER/STOP_HTTPS_SERVER_WORKER");
		
		try
		{
			httpsServerWorker.stop();
			
			httpsServerWorkers.remove(httpsServerWorker);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER/STOP_HTTPS_SERVER_WORKER: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER/STOP_HTTPS_SERVER_WORKER", e);
		}
	}
	
	protected synchronized void startHTTPSServer() throws HTTPSServerException 
	{
		logger.log(2, "HTTPS_SERVER/START_HTTPS_SERVER");
		
		try
		{
			sslServerSocket = HTTPS.createSSLServerSocket(remoteAddress, remotePort);
			
			int i = 0;
			while(i <= 100)
			{
				try
				{
					sslServerSocket.bind(new InetSocketAddress(APJP.APJP_LOCAL_HTTPS_SERVER_ADDRESS, APJP.APJP_LOCAL_HTTPS_SERVER_PORT + i));
					break;
				}
				catch(Exception e)
				{
					if(i == 100)
					{
						throw e;
					}
				}
				
				i = i + 1;
			}
			
			thread = new Thread(this);
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER/START_HTTPS_SERVER: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER/START_HTTPS_SERVER", e);
		}
	}
	
	protected synchronized void stopHTTPSServer() throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER/STOP_HTTPS_SERVER");
		
		try
		{
			thread = null;
			
			try
			{
				SSLSocket outputSSLSocket = HTTPS.createSSLSocket();
				outputSSLSocket.connect(new InetSocketAddress(APJP.APJP_LOCAL_HTTPS_SERVER_ADDRESS, sslServerSocket.getLocalPort()));
				outputSSLSocket.close();
			}
			catch(Exception e)
			{
				
			}
			
			try
			{
				sslServerSocket.close();
			}
			catch(Exception e)
			{
				
			}
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER/STOP_HTTPS_SERVER: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER/STOP_HTTPS_SERVER", e);
		}
	}
	
	protected synchronized void startHTTPSServerWorkers() throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER/START_HTTPS_SERVER_WORKERS");
	}
	
	protected synchronized void stopHTTPSServerWorkers() throws HTTPSServerException
	{
		logger.log(2, "HTTPS_SERVER/STOP_HTTPS_SERVER_WORKERS");
		
		try
		{
			for(HTTPSServerWorker httpsServerWorker2: httpsServerWorkers)
			{
				httpsServerWorker2.stop();
			}
			
			httpsServerWorkers.removeAll(httpsServerWorkers);
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS_SERVER/STOP_HTTPS_SERVER_WORKERS: EXCEPTION", e);
			
			throw new HTTPSServerException("HTTPS_SERVER/STOP_HTTPS_SERVER_WORKERS", e);
		}
	}
	
	public void run()
	{
		while(thread != null)
		{
			try
			{
				SSLSocket inputSSLSocket = (SSLSocket) sslServerSocket.accept();
				
				if(thread != null)
				{
					HTTPSServerWorker httpsServerWorker = new HTTPSServerWorker(this, inputSSLSocket);
					
					startHTTPSServerWorker(httpsServerWorker);
				}
				else
				{
					inputSSLSocket.close();
				}
			}
			catch(Exception e)
			{
				if(thread != null)
				{
					logger.log(2, "HTTPS_SERVER: EXCEPTION", e);
				}
			}
		}
	}
}
