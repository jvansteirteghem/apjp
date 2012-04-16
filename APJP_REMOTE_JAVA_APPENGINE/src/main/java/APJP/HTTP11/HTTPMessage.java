/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTP11;

import java.io.OutputStream;

public abstract class HTTPMessage
{
	protected HTTPMessageHeaders httpMessageHeaders;
	
	public HTTPMessage()
	{
		httpMessageHeaders = new HTTPMessageHeaders();
	}
	
	public HTTPMessageHeader getHTTPMessageHeader(String key)
	{	
		return httpMessageHeaders.getHTTPMessageHeader(key);
	}
	
	public HTTPMessageHeader[] getHTTPMessageHeaders(String key)
	{
		return httpMessageHeaders.getHTTPMessageHeaders(key);
	}
	
	public HTTPMessageHeader[] getHTTPMessageHeaders()
	{
		return httpMessageHeaders.getHTTPMessageHeaders();
	}
	
	public void addHTTPMessageHeader(HTTPMessageHeader httpMessageHeader)
	{
		httpMessageHeaders.addHTTPMessageHeader(httpMessageHeader);
	}
	
	public void removeHTTPMessageHeader(HTTPMessageHeader httpMessageHeader)
	{
		httpMessageHeaders.removeHTTPMessageHeader(httpMessageHeader);
	}
	
	public abstract void read() throws HTTPMessageException;
	
	public abstract void read(OutputStream outputStream) throws HTTPMessageException;
}
