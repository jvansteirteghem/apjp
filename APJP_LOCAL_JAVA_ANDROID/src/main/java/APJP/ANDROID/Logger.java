/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID;

import java.util.HashMap;
import java.util.Map;

public class Logger
{
	protected static Map<String, Logger> loggers = new HashMap<String, Logger>();
	protected String id;
	protected int level;
	
	public static synchronized Logger getLogger(String id)
	{
		Logger logger = loggers.get(id);
		
		if(logger == null)
		{
			if(id.equalsIgnoreCase(""))
			{
				logger = new Logger("");
				logger.setLevel(3);
			}
			else if(id.equalsIgnoreCase(APJP.APJP_LOGGER_ID))
			{
				logger = new Logger(APJP.APJP_LOGGER_ID);
				logger.setLevel(APJP.APJP_LOGGER_LEVEL);
			}
			else if(id.equalsIgnoreCase(APJP.APJP_LOCAL_PROXY_SERVER_LOGGER_ID))
			{
				logger = new Logger(APJP.APJP_LOCAL_PROXY_SERVER_LOGGER_ID);
				logger.setLevel(APJP.APJP_LOCAL_PROXY_SERVER_LOGGER_LEVEL);
			}
			else if(id.equalsIgnoreCase(APJP.APJP_LOCAL_HTTP_PROXY_SERVER_LOGGER_ID))
			{
				logger = new Logger(APJP.APJP_LOCAL_HTTP_PROXY_SERVER_LOGGER_ID);
				logger.setLevel(APJP.APJP_LOCAL_HTTP_PROXY_SERVER_LOGGER_LEVEL);
			}
			else if(id.equalsIgnoreCase(APJP.APJP_LOCAL_HTTP_SERVER_LOGGER_ID))
			{
				logger = new Logger(APJP.APJP_LOCAL_HTTP_SERVER_LOGGER_ID);
				logger.setLevel(APJP.APJP_LOCAL_HTTP_SERVER_LOGGER_LEVEL);
			}
			else if(id.equalsIgnoreCase(APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_LOGGER_ID))
			{
				logger = new Logger(APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_LOGGER_ID);
				logger.setLevel(APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_LOGGER_LEVEL);
			}
			else if(id.equalsIgnoreCase(APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_ID))
			{
				logger = new Logger(APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_ID);
				logger.setLevel(APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_LEVEL);
			}
			else
			{
				return logger;
			}
			
			loggers.put(id, logger);
		}
		
		return logger;
	}
	
	protected Logger(String id)
	{
		this.id = id;
	}
	
	public synchronized String getId()
	{
		return id;
	}
	
	public synchronized int getLevel()
	{
		return level;
	}
	
	public synchronized void setLevel(int level)
	{
		this.level = level;
	}
	
	public synchronized void log(int level, String message)
	{
		// 0 = OFF, 1 = INFO, 2 = MORE INFO, 3 = MOST INFO
		if(level <= this.level)
		{
			System.out.println(this.id + ": " + message);
		}
	}
	
	public synchronized void log(int level, String message, Throwable t)
	{
		if(level <= this.level)
		{
			System.out.println(this.id + ": " + message);
			
			t.printStackTrace(System.out);
		}
	}
}
