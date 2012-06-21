# APJP, A PHP/JAVA PROXY
# Copyright (C) 2009-2012 Jeroen Van Steirteghem
# 
# This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

import APJP
import Crypto.Cipher.ARC4
import socket
import ssl
import webob

class Application():
  def __call__(self, environ, start_response):
    request = webob.Request(environ)
    
    http_response_headers = []
    
    i = 0
    while i < len(APJP.APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY):
      if APJP.APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[i] != '':
        http_response_headers.append((APJP.APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[i], APJP.APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE[i]))
      i = i + 1
    
    response = webob.Response(None, 200, http_response_headers, ApplicationIterator(request))
    return response(environ, start_response)

class ApplicationIterator():
  def __init__(self, request):
    self.request = request
  
  def __iter__(self):
    cipher = Crypto.Cipher.ARC4.new(APJP.APJP.APJP_KEY)
    
    http_request_header = ''
    
    while True:
      buffer = self.request.body_file.read(1)
      buffer_length = len(buffer)
      if buffer_length == 0:
        break
      http_request_header = http_request_header + cipher.decrypt(buffer)
      http_request_header_length = len(http_request_header)
      if http_request_header_length >= 4:
        if \
          http_request_header[http_request_header_length - 4] == '\r' and \
          http_request_header[http_request_header_length - 3] == '\n' and \
          http_request_header[http_request_header_length - 2] == '\r' and \
          http_request_header[http_request_header_length - 1] == '\n':
          break
    
    http_request_header_values = http_request_header.split('\r\n')
    http_request_header_values_length = len(http_request_header_values)
    
    http_request_address = ''
    http_request_port = 0
    
    i = 1
    while i < http_request_header_values_length - 1:
      http_request_header_value1 = http_request_header_values[i]
      http_request_header_values1 = http_request_header_value1.split(': ')
      http_request_header_values1_length = len(http_request_header_values1)
      if http_request_header_values1_length == 2:
        http_request_header_value2 = http_request_header_values1[0]
        http_request_header_value3 = http_request_header_values1[1]
        if http_request_header_value2.upper() == 'Host'.upper():
          http_request_header1_values3 = http_request_header_value3.split(':')
          http_request_header1_values3_length = len(http_request_header1_values3)
          if http_request_header1_values3_length == 1:
            http_request_address = http_request_header1_values3[0]
            http_request_port = 443
          else:
            if http_request_header1_values3_length == 2:
              http_request_address = http_request_header1_values3[0]
              http_request_port = http_request_header1_values3[1]
      i = i + 1
    
    socket1 = socket.socket()
    ssl_socket1 = ssl.wrap_socket(socket1)
    ssl_socket1.connect((http_request_address, http_request_port))
    ssl_socket1.send(http_request_header)
    
    while True:
      buffer = self.request.body_file.read(5120)
      buffer_length = len(buffer)
      if buffer_length == 0:
        break
      ssl_socket1.send(cipher.decrypt(buffer))
    
    cipher = Crypto.Cipher.ARC4.new(APJP.APJP.APJP_KEY)
    
    while True:
      buffer = ssl_socket1.recv(5120)
      buffer_length = len(buffer)
      if buffer_length == 0:
        break
      yield cipher.encrypt(buffer)
    
    ssl_socket1.close()