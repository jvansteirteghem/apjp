/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTP11;

import java.util.ArrayList;
import java.util.List;

public class HTTPMessageHeaders
{
	private List<HTTPMessageHeader> httpMessageHeaders;
	
	public HTTPMessageHeaders()
	{
		super();
		
		httpMessageHeaders = new ArrayList<HTTPMessageHeader>();
	}

	public HTTPMessageHeader getHTTPMessageHeader(String key)
	{
		for(HTTPMessageHeader httpMessageHeader: httpMessageHeaders)
		{
			if(httpMessageHeader.getKey().equalsIgnoreCase(key) == true)
			{
				return httpMessageHeader;
			}
		}
		
		return null;
	}
	
	public HTTPMessageHeader[] getHTTPMessageHeaders(String key)
	{
		List<HTTPMessageHeader> httpMessageHeaders = new ArrayList<HTTPMessageHeader>();

		for(HTTPMessageHeader httpMessageHeader: this.httpMessageHeaders)
		{
			if(httpMessageHeader.getKey().equalsIgnoreCase(key) == true)
			{
				httpMessageHeaders.add(httpMessageHeader);
			}
		}
		
		return httpMessageHeaders.toArray(new HTTPMessageHeader[]{});
	}
	
	public HTTPMessageHeader[] getHTTPMessageHeaders()
	{
		List<HTTPMessageHeader> httpMessageHeaders = new ArrayList<HTTPMessageHeader>();

		for(HTTPMessageHeader httpMessageHeader: this.httpMessageHeaders)
		{
			httpMessageHeaders.add(httpMessageHeader);
		}
		
		return httpMessageHeaders.toArray(new HTTPMessageHeader[]{});
	}
	
	public void addHTTPMessageHeader(HTTPMessageHeader httpMessageHeader)
	{
		httpMessageHeaders.add(httpMessageHeader);
	}
	
	public void removeHTTPMessageHeader(HTTPMessageHeader httpMessageHeader)
	{
		httpMessageHeaders.remove(httpMessageHeader);
	}
}
