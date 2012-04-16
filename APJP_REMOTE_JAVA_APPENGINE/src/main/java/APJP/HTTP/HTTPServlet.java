/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTP;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import APJP.HTTP11.HTTPMessageHeader;
import APJP.HTTP11.HTTPRequest;
import APJP.HTTP11.HTTPRequestMessage;
import APJP.HTTP11.HTTPResponseMessage;

public class HTTPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger;
	
	private static String APJP_KEY;
	private static String[] APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY;
	private static String[] APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE;
	
	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		
		logger = Logger.getLogger(HTTPServlet.class.getName());
		
		APJP_KEY = System.getProperty("APJP_KEY", "");
		
		APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY = new String[5];
		APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE = new String[5];
		for(int i = 0; i < APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY.length; i = i + 1)
		{
			APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[i] = System.getProperty("APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_" + (i + 1) + "_KEY", "");
			APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE[i] = System.getProperty("APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_" + (i + 1) + "_VALUE", "");
		}
	}
	
	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
	{
		try
		{
			httpServletResponse.setStatus(200);
			
			for(int i = 0; i < APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY.length; i = i + 1)
			{
				if(APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[i].equalsIgnoreCase("") == false)
				{
					httpServletResponse.addHeader(APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[i], APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE[i]);
				}
			}
			
			SecretKeySpec secretKeySpec = new SecretKeySpec(APJP_KEY.getBytes(), "ARCFOUR");
			
			Cipher inputStreamCipher = Cipher.getInstance("ARCFOUR");
			inputStreamCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			
			CipherInputStream httpRequestInputStream = new CipherInputStream(httpServletRequest.getInputStream(), inputStreamCipher);
			
			Cipher outputStreamCipher = Cipher.getInstance("ARCFOUR");
			outputStreamCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			
			CipherOutputStream httpResponseOutputStream = new CipherOutputStream(httpServletResponse.getOutputStream(), outputStreamCipher);
			
			HTTPRequestMessage httpRequestMessage1 = new HTTPRequestMessage(httpRequestInputStream);
			
			httpRequestMessage1.read();
			
			HTTPRequest httpRequest1 = new HTTPRequest(httpRequestMessage1);
			
			httpRequest1.open();
			
			try
			{
				HTTPResponseMessage httpResponseMessage1 = httpRequest1.getHTTPResponseMessage();
				
				HTTPMessageHeader[] httpResponseMessage1Headers1 = httpResponseMessage1.getHTTPMessageHeaders();
				
				HTTPMessageHeader httpResponseMessage1Header1 = httpResponseMessage1Headers1[0];
				
				String httpResponseMessage1Header1Key1 = httpResponseMessage1Header1.getKey();
				
				String httpResponseMessage1Header1Value1 = httpResponseMessage1Header1.getValue();
				
				httpResponseOutputStream.write((httpResponseMessage1Header1Value1 + "\r\n").getBytes());
				
				for(int i = 1; i < httpResponseMessage1Headers1.length; i = i + 1)
				{
					httpResponseMessage1Header1 = httpResponseMessage1Headers1[i];
					
					httpResponseMessage1Header1Key1 = httpResponseMessage1Header1.getKey();
					
					httpResponseMessage1Header1Value1 = httpResponseMessage1Header1.getValue();
					
					httpResponseOutputStream.write((httpResponseMessage1Header1Key1 + ": " + httpResponseMessage1Header1Value1 + "\r\n").getBytes());
				}
				
				httpResponseOutputStream.write(("\r\n").getBytes());
				
				httpResponseMessage1.read(httpResponseOutputStream);
			}
			catch(Exception e)
			{
				throw e;
			}
			finally
			{
				try
				{
					httpRequest1.close();
				}
				catch(Exception e)
				{
					
				}
			}
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, "EXCEPTION", e);
			
			httpServletResponse.setStatus(500);
		}
	}
}
