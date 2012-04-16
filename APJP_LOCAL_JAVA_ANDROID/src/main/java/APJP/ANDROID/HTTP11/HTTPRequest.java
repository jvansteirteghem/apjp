/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID.HTTP11;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import APJP.ANDROID.APJP;
import APJP.ANDROID.Logger;
import APJP.ANDROID.BASE64.BASE64;

public class HTTPRequest
{
	protected static Logger logger;
	protected int i;
	protected HTTPRequestMessage httpRequestMessage;
	protected URL url;
	protected URLConnection urlConnection;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOGGER_ID);
	}
	
	protected HTTPRequest(int i, HTTPRequestMessage httpRequestMessage)
	{
		this.i = i;
		this.httpRequestMessage = httpRequestMessage;
	}
	
	public void open() throws HTTPRequestException
	{
		try
		{
			url = new URL(APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL[i]);
			
			Proxy proxy = Proxy.NO_PROXY;
			
			if(url.getProtocol().equalsIgnoreCase("HTTP") == true)
			{
				if(APJP.APJP_HTTP_PROXY_SERVER_ADDRESS.equalsIgnoreCase("") == false)
				{
					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(APJP.APJP_HTTP_PROXY_SERVER_ADDRESS, APJP.APJP_HTTP_PROXY_SERVER_PORT));
				}
			}
			else
			{
				if(url.getProtocol().equalsIgnoreCase("HTTPS") == true)
				{
					if(APJP.APJP_HTTPS_PROXY_SERVER_ADDRESS.equalsIgnoreCase("") == false)
					{
						proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(APJP.APJP_HTTPS_PROXY_SERVER_ADDRESS, APJP.APJP_HTTPS_PROXY_SERVER_PORT));
					}
				}
			}
			
			urlConnection = url.openConnection(proxy);
			
			if(urlConnection instanceof HttpsURLConnection)
			{
				((HttpsURLConnection) urlConnection).setHostnameVerifier
				(
					new HostnameVerifier()
					{
						public boolean verify(String hostname, SSLSession sslSession)
						{
							String value1 = APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_URL[i];
							
							String[] values1 = value1.split("/", -1);
							
							String value2 = values1[2];
							
							String[] values2 = value2.split(":");
							
							String value3 = values2[0];
							
							if(value3.equalsIgnoreCase(hostname))
							{
								return true;
							}
							else
							{
								return false;
							}
						}
					}
				);
			}
			
			if(url.getProtocol().equalsIgnoreCase("HTTP") == true)
			{
				if(APJP.APJP_HTTP_PROXY_SERVER_ADDRESS.equalsIgnoreCase("") == false && APJP.APJP_HTTP_PROXY_SERVER_USERNAME.equalsIgnoreCase("") == false)
				{
					urlConnection.setRequestProperty("Proxy-Authorization", "Basic " + new String(BASE64.encode((APJP.APJP_HTTP_PROXY_SERVER_USERNAME + ":" + APJP.APJP_HTTP_PROXY_SERVER_PASSWORD).getBytes())));
				}
			}
			else
			{
				if(url.getProtocol().equalsIgnoreCase("HTTPS") == true)
				{
					if(APJP.APJP_HTTPS_PROXY_SERVER_ADDRESS.equalsIgnoreCase("") == false && APJP.APJP_HTTPS_PROXY_SERVER_USERNAME.equalsIgnoreCase("") == false)
					{
						urlConnection.setRequestProperty("Proxy-Authorization", "Basic " + new String(BASE64.encode((APJP.APJP_HTTPS_PROXY_SERVER_USERNAME + ":" + APJP.APJP_HTTPS_PROXY_SERVER_PASSWORD).getBytes())));
					}
				}
			}
			
			for(int j = 0; j < APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_KEY[i].length; j = j + 1)
			{
				if(APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_KEY[i][j].equalsIgnoreCase("") == false)
				{
					urlConnection.setRequestProperty(APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_KEY[i][j], APJP.APJP_REMOTE_HTTP_SERVER_REQUEST_PROPERTY_VALUE[i][j]);
				}
			}
			
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			
			urlConnection.connect();
		}
		catch(Exception e)
		{
			throw new HTTPRequestException("HTTP_REQUEST/OPEN", e);
		}
	}
	
	public void close() throws HTTPRequestException
	{
		
	}
	
	public HTTPResponseMessage getHTTPResponseMessage() throws HTTPRequestException
	{
		try
		{
			HTTPMessageHeader[] httpRequestMessage1Headers1 = httpRequestMessage.getHTTPMessageHeaders();
			
			HTTPMessageHeader httpRequestMessage1Header1 = httpRequestMessage1Headers1[0];
			
			String httpRequestMessage1Header1Key1 = httpRequestMessage1Header1.getKey();
			
			String httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
			
			logger.log(2, "HTTP_REQUEST/GET_HTTP_RESPONSE_MESSAGE: REQUEST: " + httpRequestMessage1Header1Value1);
			
			String[] httpRequestMessage1Header1Values1 = httpRequestMessage1Header1Value1.split(" ");
			
			String httpRequestMessage1Header1Value2 = httpRequestMessage1Header1Values1[1];
			
			String[] httpRequestMessage1Header1Values2 = httpRequestMessage1Header1Value2.split("/", -1);
			
			String httpRequestMessage1Header1Value3 = httpRequestMessage1Header1Values2[0];
			
			if(httpRequestMessage1Header1Value3.equalsIgnoreCase("http:"))
			{
				httpRequestMessage1Header1Value2 = "";
				
				for(int j = 3; j < httpRequestMessage1Header1Values2.length; j = j + 1)
				{
					httpRequestMessage1Header1Value2 = httpRequestMessage1Header1Value2 + "/" + httpRequestMessage1Header1Values2[j];
				}
				
				httpRequestMessage1Header1Values1[1] = httpRequestMessage1Header1Value2;
			}
			
			httpRequestMessage1Header1Values1[2] = "HTTP/1.0";
			
			httpRequestMessage1Header1Value1 = httpRequestMessage1Header1Values1[0];
			
			for(int j = 1; j < httpRequestMessage1Header1Values1.length; j = j + 1)
			{
				httpRequestMessage1Header1Value1 = httpRequestMessage1Header1Value1 + " " + httpRequestMessage1Header1Values1[j];
			}
			
			httpRequestMessage1Header1.setValue(httpRequestMessage1Header1Value1);
			
			for(int j = 1; j < httpRequestMessage1Headers1.length; j = j + 1)
			{
				httpRequestMessage1Header1 = httpRequestMessage1Headers1[j];
				
				httpRequestMessage1Header1Key1 = httpRequestMessage1Header1.getKey();
				
				httpRequestMessage1Header1Value1 = httpRequestMessage1Header1.getValue();
				
				// 1.1
				if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Accept"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Accept-Charset"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Accept-Encoding"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Accept-Language"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Allow"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Authorization"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Cache-Control"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Connection"))
				{
					httpRequestMessage.removeHTTPMessageHeader(httpRequestMessage1Header1);
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Content-Encoding"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Content-Language"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Content-Length"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Content-Location"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Content-MD5"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Content-Range"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Content-Type"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Date"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Expect"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Expires"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("From"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Host"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("If-Match"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("If-Modified-Since"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("If-None-Match"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("If-Range"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("If-Unmodified-Since"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Keep-Alive"))
				{
					httpRequestMessage.removeHTTPMessageHeader(httpRequestMessage1Header1);
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Last-Modified"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Max-Forwards"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Pragma"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Proxy-Authorization"))
				{
					httpRequestMessage.removeHTTPMessageHeader(httpRequestMessage1Header1);
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Proxy-Connection"))
				{
					httpRequestMessage.removeHTTPMessageHeader(httpRequestMessage1Header1);
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Range"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Referer"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("TE"))
				{
					httpRequestMessage.removeHTTPMessageHeader(httpRequestMessage1Header1);
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Trailer"))
				{
					httpRequestMessage.removeHTTPMessageHeader(httpRequestMessage1Header1);
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Transfer-Encoding"))
				{
					httpRequestMessage.removeHTTPMessageHeader(httpRequestMessage1Header1);
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Upgrade"))
				{
					
				}
				// 1.0
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("User-Agent"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Via"))
				{
					
				}
				// 1.1
				else if(httpRequestMessage1Header1Key1.equalsIgnoreCase("Warning"))
				{
					
				}
				
				logger.log(3, "HTTP_REQUEST/GET_HTTP_RESPONSE_MESSAGE: REQUEST: " + httpRequestMessage1Header1Key1 + ": " + httpRequestMessage1Header1Value1);
			}
			
			httpRequestMessage.addHTTPMessageHeader(new HTTPMessageHeader("Connection", "close"));
			
			SecretKeySpec secretKeySpec = new SecretKeySpec(APJP.APJP_KEY.getBytes(), "ARCFOUR");
			
			Cipher outputCipher = Cipher.getInstance("ARCFOUR");
			outputCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			
			CipherOutputStream cipherOutputStream = new CipherOutputStream(urlConnection.getOutputStream(), outputCipher);
			
			HTTPMessageHeader[] httpRequestMessage1Headers2 = httpRequestMessage.getHTTPMessageHeaders();
			
			HTTPMessageHeader httpRequestMessage1Header2 = httpRequestMessage1Headers2[0];
			
			String httpRequestMessage1Header2Key1 = httpRequestMessage1Header2.getKey();
			
			String httpRequestMessage1Header2Value1 = httpRequestMessage1Header2.getValue();
			
			cipherOutputStream.write((httpRequestMessage1Header2Value1 + "\r\n").getBytes());
			
			for(int j = 1; j < httpRequestMessage1Headers2.length; j = j + 1)
			{
				httpRequestMessage1Header2 = httpRequestMessage1Headers2[j];
				
				httpRequestMessage1Header2Key1 = httpRequestMessage1Header2.getKey();
				
				httpRequestMessage1Header2Value1 = httpRequestMessage1Header2.getValue();
				
				cipherOutputStream.write((httpRequestMessage1Header2Key1 + ": " + httpRequestMessage1Header2Value1 + "\r\n").getBytes());
			}
			
			cipherOutputStream.write(("\r\n").getBytes());
			
			httpRequestMessage.read(cipherOutputStream);
			
			Cipher inputCipher = Cipher.getInstance("ARCFOUR");
			inputCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			
			CipherInputStream cipherInputStream = new CipherInputStream(urlConnection.getInputStream(), inputCipher);
			
			HTTPResponseMessage httpResponseMessage1 = new HTTPResponseMessage(httpRequestMessage, cipherInputStream);
			
			httpResponseMessage1.read();
			
			HTTPMessageHeader[] httpResponseMessage1Headers1 = httpResponseMessage1.getHTTPMessageHeaders();
			
			HTTPMessageHeader httpResponseMessage1Header1 = httpResponseMessage1Headers1[0];
			
			String httpResponseMessage1Header1Key1 = httpResponseMessage1Header1.getKey();
			
			String httpResponseMessage1Header1Value1 = httpResponseMessage1Header1.getValue();
			
			logger.log(2, "HTTP_REQUEST/GET_HTTP_RESPONSE_MESSAGE: RESPONSE: " + httpResponseMessage1Header1Value1);
			
			String[] httpResponseMessage1Header1Values1 = httpResponseMessage1Header1Value1.split(" ");
			
			httpResponseMessage1Header1Values1[0] = "HTTP/1.0";
			
			httpResponseMessage1Header1Value1 = httpResponseMessage1Header1Values1[0];
			
			for(int j = 1; j < httpResponseMessage1Header1Values1.length; j = j + 1)
			{
				httpResponseMessage1Header1Value1 = httpResponseMessage1Header1Value1 + " " + httpResponseMessage1Header1Values1[j];
			}
			
			httpResponseMessage1Header1.setValue(httpResponseMessage1Header1Value1);
			
			for(int j = 1; j < httpResponseMessage1Headers1.length; j = j + 1)
			{
				httpResponseMessage1Header1 = httpResponseMessage1Headers1[j];
				
				httpResponseMessage1Header1Key1 = httpResponseMessage1Header1.getKey();
				
				httpResponseMessage1Header1Value1 = httpResponseMessage1Header1.getValue();
				
				// 1.1
				if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Accept-Ranges"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Age"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Allow"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Cache-Control"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Connection"))
				{
					httpResponseMessage1.removeHTTPMessageHeader(httpResponseMessage1Header1);
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Content-Encoding"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Content-Language"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Content-Length"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Content-Location"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Content-MD5"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Content-Range"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Content-Type"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Date"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("ETag"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Expires"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Keep-Alive"))
				{
					httpResponseMessage1.removeHTTPMessageHeader(httpResponseMessage1Header1);
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Last-Modified"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Location"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Pragma"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Proxy-Authenticate"))
				{
					httpResponseMessage1.removeHTTPMessageHeader(httpResponseMessage1Header1);
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Proxy-Connection"))
				{
					httpResponseMessage1.removeHTTPMessageHeader(httpResponseMessage1Header1);
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Retry-After"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Server"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Trailer"))
				{
					httpResponseMessage1.removeHTTPMessageHeader(httpResponseMessage1Header1);
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Transfer-Encoding"))
				{
					httpResponseMessage1.removeHTTPMessageHeader(httpResponseMessage1Header1);
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Vary"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Upgrade"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Via"))
				{
					
				}
				// 1.1
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("Warning"))
				{
					
				}
				// 1.0
				else if(httpResponseMessage1Header1Key1.equalsIgnoreCase("WWW-Authenticate"))
				{
					
				}
				
				logger.log(3, "HTTP_REQUEST/GET_HTTP_RESPONSE_MESSAGE: RESPONSE: " + httpResponseMessage1Header1Key1 + ": " + httpResponseMessage1Header1Value1);
			}
			
			httpResponseMessage1.addHTTPMessageHeader(new HTTPMessageHeader("Connection", "close"));
			
			return httpResponseMessage1;
		}
		catch(Exception e)
		{
			throw new HTTPRequestException("HTTP_REQUEST/GET_HTTP_RESPONSE_MESSAGE", e);
		}
	}
}
