/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import APJP.ANDROID.HTTP11.HTTPRequests;
import APJP.ANDROID.HTTP11.HTTPSRequests;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class Main extends Service
{
	private Logger logger;
	private ProxyServer proxyServer;
	
	public Main()
	{
		logger = Logger.getLogger("");
	}
	
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	public void onCreate()
	{
		try
		{
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			APJP.APJP_KEY = sharedPreferences.getString("APJP_KEY", "");
			
			APJP.APJP_LOGGER_ID = "APJP";
			APJP.APJP_LOGGER_LEVEL = 1;
			
			APJP.APJP_LOCAL_PROXY_SERVER_ADDRESS = sharedPreferences.getString("APJP_LOCAL_PROXY_SERVER_ADDRESS", "");
			try
			{
				APJP.APJP_LOCAL_PROXY_SERVER_PORT = new Integer(sharedPreferences.getString("APJP_LOCAL_PROXY_SERVER_PORT", ""));
			}
			catch(Exception e)
			{
				APJP.APJP_LOCAL_PROXY_SERVER_PORT = 0;
			}
			APJP.APJP_LOCAL_PROXY_SERVER_LOGGER_ID = "APJP_LOCAL_PROXY_SERVER";
			APJP.APJP_LOCAL_PROXY_SERVER_LOGGER_LEVEL = 1;
			
			APJP.APJP_LOCAL_HTTP_PROXY_SERVER_ADDRESS = sharedPreferences.getString("APJP_LOCAL_HTTP_PROXY_SERVER_ADDRESS", "");
			try
			{
				APJP.APJP_LOCAL_HTTP_PROXY_SERVER_PORT = new Integer(sharedPreferences.getString("APJP_LOCAL_HTTP_PROXY_SERVER_PORT", ""));
			}
			catch(Exception e)
			{
				APJP.APJP_LOCAL_HTTP_PROXY_SERVER_PORT = 0;
			}
			APJP.APJP_LOCAL_HTTP_PROXY_SERVER_LOGGER_ID = "APJP_LOCAL_HTTP_PROXY_SERVER";
			APJP.APJP_LOCAL_HTTP_PROXY_SERVER_LOGGER_LEVEL = 1;
			
			APJP.APJP_LOCAL_HTTP_SERVER_ADDRESS = sharedPreferences.getString("APJP_LOCAL_HTTP_SERVER_ADDRESS", "");
			try
			{
				APJP.APJP_LOCAL_HTTP_SERVER_PORT = new Integer(sharedPreferences.getString("APJP_LOCAL_HTTP_SERVER_PORT", ""));
			}
			catch(Exception e)
			{
				APJP.APJP_LOCAL_HTTP_SERVER_PORT = 0;
			}
			APJP.APJP_LOCAL_HTTP_SERVER_LOGGER_ID = "APJP_LOCAL_HTTP_SERVER";
			APJP.APJP_LOCAL_HTTP_SERVER_LOGGER_LEVEL = 1;
			
			APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL = new String[10];
			APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_KEY = new String[10][5];
			APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_VALUE = new String[10][5];
			for(int i = 0; i < APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_KEY.length; i = i + 1)
			{
				APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL[i] = sharedPreferences.getString("APJP_REMOTE_HTTP_SERVER_" + (i + 1) + "_REQUEST_URL", "");
				
				for(int j = 0; j < APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_KEY[i].length; j = j + 1)
				{
					APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_KEY[i][j] = sharedPreferences.getString("APJP_REMOTE_HTTP_SERVER_" + (i + 1) + "_REQUEST_PROPERTY_" + (j + 1) + "_KEY", "");
					APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_VALUE[i][j] = sharedPreferences.getString("APJP_REMOTE_HTTP_SERVER_" + (i + 1) + "_REQUEST_PROPERTY_" + (j + 1) + "_VALUE", "");
				}
			}
			
			APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_ADDRESS = sharedPreferences.getString("APJP_LOCAL_HTTPS_PROXY_SERVER_ADDRESS", "");
			try
			{
				APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_PORT = new Integer(sharedPreferences.getString("APJP_LOCAL_HTTPS_PROXY_SERVER_PORT", ""));
			}
			catch(Exception e)
			{
				APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_PORT = 0;
			}
			APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_LOGGER_ID = "APJP_LOCAL_HTTPS_PROXY_SERVER";
			APJP.APJP_LOCAL_HTTPS_PROXY_SERVER_LOGGER_LEVEL = 1;
			
			APJP.APJP_LOCAL_HTTPS_SERVER_ADDRESS = sharedPreferences.getString("APJP_LOCAL_HTTPS_SERVER_ADDRESS", "");
			try
			{
				APJP.APJP_LOCAL_HTTPS_SERVER_PORT = new Integer(sharedPreferences.getString("APJP_LOCAL_HTTPS_SERVER_PORT", ""));
			}
			catch(Exception e)
			{
				APJP.APJP_LOCAL_HTTPS_SERVER_PORT = 0;
			}
			APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_ID = "APJP_LOCAL_HTTPS_SERVER";
			APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_LEVEL = 1;
			
			APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL = new String[10];
			APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_PROPERTY_KEY = new String[10][5];
			APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_PROPERTY_VALUE = new String[10][5];
			for(int i = 0; i < APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_PROPERTY_KEY.length; i = i + 1)
			{
				APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_URL[i] = sharedPreferences.getString("APJP_REMOTE_HTTPS_SERVER_" + (i + 1) + "_REQUEST_URL", "");
				
				for(int j = 0; j < APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_PROPERTY_KEY[i].length; j = j + 1)
				{
					APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_PROPERTY_KEY[i][j] = sharedPreferences.getString("APJP_REMOTE_HTTPS_SERVER_" + (i + 1) + "_REQUEST_PROPERTY_" + (j + 1) + "_KEY", "");
					APJP.APJP_REMOTE_HTTPS_SERVER_REQUEST_PROPERTY_VALUE[i][j] = sharedPreferences.getString("APJP_REMOTE_HTTPS_SERVER_" + (i + 1) + "_REQUEST_PROPERTY_" + (j + 1) + "_VALUE", "");
				}
			}
			
			APJP.APJP_HTTP_PROXY_SERVER_ADDRESS = sharedPreferences.getString("APJP_HTTP_PROXY_SERVER_ADDRESS", "");
			try
			{
				APJP.APJP_HTTP_PROXY_SERVER_PORT = new Integer(sharedPreferences.getString("APJP_HTTP_PROXY_SERVER_PORT", ""));
			}
			catch(Exception e)
			{
				APJP.APJP_HTTP_PROXY_SERVER_PORT = 0;
			}
			APJP.APJP_HTTP_PROXY_SERVER_USERNAME = sharedPreferences.getString("APJP_HTTP_PROXY_SERVER_USERNAME", "");
			APJP.APJP_HTTP_PROXY_SERVER_PASSWORD = sharedPreferences.getString("APJP_HTTP_PROXY_SERVER_PASSWORD", "");
			
			APJP.APJP_HTTPS_PROXY_SERVER_ADDRESS = sharedPreferences.getString("APJP_HTTPS_PROXY_SERVER_ADDRESS", "");
			try
			{
				APJP.APJP_HTTPS_PROXY_SERVER_PORT = new Integer(sharedPreferences.getString("APJP_HTTPS_PROXY_SERVER_PORT", ""));
			}
			catch(Exception e)
			{
				APJP.APJP_HTTPS_PROXY_SERVER_PORT = 0;
			}
			APJP.APJP_HTTPS_PROXY_SERVER_USERNAME = sharedPreferences.getString("APJP_HTTPS_PROXY_SERVER_USERNAME", "");
			APJP.APJP_HTTPS_PROXY_SERVER_PASSWORD = sharedPreferences.getString("APJP_HTTPS_PROXY_SERVER_PASSWORD", "");
			
			APJP.APJP_APPLICATION = getApplication();
			
			Authenticator.setDefault
			(
				new Authenticator()
				{
					protected PasswordAuthentication getPasswordAuthentication()
					{
						PasswordAuthentication passwordAuthentication = null;
						
						if(this.getRequestorType() == Authenticator.RequestorType.PROXY)
						{
							if(this.getRequestingURL().getProtocol().equalsIgnoreCase("HTTP") == true)
							{
								passwordAuthentication = new PasswordAuthentication(APJP.APJP_HTTP_PROXY_SERVER_USERNAME, APJP.APJP_HTTP_PROXY_SERVER_PASSWORD.toCharArray());
							}
							else
							{
								if(this.getRequestingURL().getProtocol().equalsIgnoreCase("HTTPS") == true)
								{
									passwordAuthentication = new PasswordAuthentication(APJP.APJP_HTTPS_PROXY_SERVER_USERNAME, APJP.APJP_HTTPS_PROXY_SERVER_PASSWORD.toCharArray());
								}
							}
						}
						
						return passwordAuthentication;
					}
				}
			);
			
			logger = Logger.getLogger(APJP.APJP_LOGGER_ID);
			
			proxyServer = new ProxyServer();
		}
		catch(Exception e)
		{
			logger.log(1, "EXCEPTION", e);
		}
	}

	public void onStart(Intent intent, int startid)
	{
		try
		{
			logger.log(1, "START_PROXY_SERVER");
			
			try
			{
				proxyServer.start();
				
				logger.log(1, "START_PROXY_SERVER: OK");
			}
			catch(Exception e)
			{
				logger.log(1, "START_PROXY_SERVER: EXCEPTION", e);
				
				logger.log(1, "START_PROXY_SERVER: NOT OK");
			}
			
			Thread thread = new Thread
			(
				new Runnable()
				{
					public void run()
					{
						HTTPRequests httpRequests = HTTPRequests.getHTTPRequests();
						
						logger.log(1, "TEST_HTTP_REQUESTS");
						
						try
						{
							httpRequests.test();
							
							logger.log(1, "TEST_HTTP_REQUESTS: OK");
						}
						catch(Exception e)
						{
							logger.log(1, "TEST_HTTP_REQUESTS: EXCEPTION", e);
							
							logger.log(1, "TEST_HTTP_REQUESTS: NOT OK");
						}
						
						HTTPSRequests httpsRequests = HTTPSRequests.getHTTPSRequests();
						
						logger.log(1, "TEST_HTTPS_REQUESTS");
						
						try
						{
							httpsRequests.test();
							
							logger.log(1, "TEST_HTTPS_REQUESTS: OK");
						}
						catch(Exception e)
						{
							logger.log(1, "TEST_HTTPS_REQUESTS: EXCEPTION", e);
							
							logger.log(1, "TEST_HTTPS_REQUESTS: NOT OK");
						}
					}
				}
			);
			
			thread.start();
		}
		catch(Exception e)
		{
			logger.log(1, "EXCEPTION", e);
		}
	}

	public void onDestroy()
	{
		try
		{
			logger.log(1, "STOP_PROXY_SERVER");
			
			try
			{
				proxyServer.stop();
				
				logger.log(1, "STOP_PROXY_SERVER: OK");
			}
			catch(Exception e)
			{
				logger.log(1, "STOP_PROXY_SERVER: EXCEPTION", e);
				
				logger.log(1, "STOP_PROXY_SERVER: NOT OK");
			}
		}
		catch(Exception e)
		{
			logger.log(1, "EXCEPTION", e);
		}
	}
}
