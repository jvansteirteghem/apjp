/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID.HTTPS;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import APJP.ANDROID.APJP;
import APJP.ANDROID.Logger;
import android.content.res.AssetManager;

public class HTTPS
{
	private static Logger logger;
	private static KeyStore defaultKeyStore;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_ID);
		
		defaultKeyStore = null;
	}
	
	private static KeyStore getDefaultKeyStore() throws HTTPSException
	{
		try
		{
			if(defaultKeyStore == null)
			{
				AssetManager assetManager = APJP.APJP_APPLICATION.getAssets();
				
				defaultKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				defaultKeyStore.load(assetManager.open("APJP.bks"), "APJP".toCharArray());
			}
			
			return defaultKeyStore;
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS/GET_DEFAULT_KEY_STORE: EXCEPTION", e);
			
			throw new HTTPSException("HTTPS/GET_DEFAULT_KEY_STORE", e);
		}
	}
	
	public static synchronized SSLSocket createSSLSocket() throws HTTPSException
	{
		try
		{
			KeyStore defaultKeyStore = getDefaultKeyStore();
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(defaultKeyStore, "APJP".toCharArray());
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(defaultKeyStore);
			
			sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(), null);
			
			SSLSocketFactory sslSocketFactory = (SSLSocketFactory) sslContext.getSocketFactory();
			
			return (SSLSocket) sslSocketFactory.createSocket();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS/CREATE_SSL_SOCKET: EXCEPTION", e);
			
			throw new HTTPSException("HTTPS/CREATE_SSL_SOCKET", e);
		}
	}
	
	public static synchronized SSLServerSocket createSSLServerSocket() throws HTTPSException
	{
		try
		{
			KeyStore defaultKeyStore = getDefaultKeyStore();
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(defaultKeyStore, "APJP".toCharArray());
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(defaultKeyStore);
			
			sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(), null);
			
			SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) sslContext.getServerSocketFactory();
			
			return (SSLServerSocket) sslServerSocketFactory.createServerSocket();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS/CREATE_SSL_SERVER_SOCKET: EXCEPTION", e);
			
			throw new HTTPSException("HTTPS/CREATE_SSL_SERVER_SOCKET", e);
		}
	}
}
