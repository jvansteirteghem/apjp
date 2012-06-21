# APJP, A PHP/JAVA PROXY
# Copyright (C) 2009-2012 Jeroen Van Steirteghem
# 
# This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

require "./APJP/APJP.rb"
require "openssl"
require "rack"
require "socket"

module APJP
  module HTTP
    class Application
      def call(env)
        request = Rack::Request.new(env)
        
        http_response_headers = {}
        
        for i in 0 .. APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY.length - 1
          if APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[i] != "" then
            http_response_headers[APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[i]] = APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE[i]
          end
        end
        
        response = Rack::Response.new(ApplicationIterator.new(request), 200, http_response_headers)
        return response
      end
    end
    
    class ApplicationIterator
      def initialize(request)
        @request = request
      end
      
      def each
        cipher = OpenSSL::Cipher::Cipher.new("RC4")
        cipher.decrypt()
        cipher.key = APJP.APJP_KEY
        cipher.iv  = ""
        
        http_request_header = ""
        http_request_header_length = http_request_header.length
        
        buffer = @request.body.read(1)
        while buffer != nil
          http_request_header = http_request_header + cipher.update(buffer)
          http_request_header_length = http_request_header.length
          
          if http_request_header_length >= 4 then
            if \
              http_request_header[http_request_header_length - 4] == "\r" and \
              http_request_header[http_request_header_length - 3] == "\n" and \
              http_request_header[http_request_header_length - 2] == "\r" and \
              http_request_header[http_request_header_length - 1] == "\n" then
              break
            end
          end
          
          buffer = @request.body.read(1)
        end
        
        http_request_header_values = http_request_header.split("\r\n")
        http_request_header_values_length = http_request_header_values.length
        
        http_request_address = ""
        http_request_port = 0
        
        i = 1
        while i < http_request_header_values_length
          http_request_header_value1 = http_request_header_values[i]
          http_request_header_values1 = http_request_header_value1.split(": ")
          http_request_header_values1_length = http_request_header_values1.length
          if http_request_header_values1_length == 2 then
            http_request_header_value2 = http_request_header_values1[0]
            http_request_header_value3 = http_request_header_values1[1]
            if http_request_header_value2.upcase() == "Host".upcase() then
              http_request_header1_values3 = http_request_header_value3.split(":")
              http_request_header1_values3_length = http_request_header1_values3.length
              if http_request_header1_values3_length == 1 then
                http_request_address = http_request_header1_values3[0]
                http_request_port = 80
              else
                if http_request_header1_values3_length == 2 then
                  http_request_address = http_request_header1_values3[0]
                  http_request_port = http_request_header1_values3[1]
                end
              end
            end
          end
          i = i + 1
        end
        
        socket = TCPSocket.new(http_request_address, http_request_port)
        socket.write(http_request_header)
        
        buffer = @request.body.read(5120)
        while buffer != nil
          socket.write(cipher.update(buffer))
          
          buffer = @request.body.read(5120)
        end
        
        socket.write(cipher.final())
        
        cipher = OpenSSL::Cipher::Cipher.new("RC4")
        cipher.encrypt()
        cipher.key = APJP.APJP_KEY
        cipher.iv  = ""
        
        buffer = socket.read(5120)
        while buffer != nil
          yield cipher.update(buffer)
          
          buffer = socket.read(5120)
        end
        
        yield cipher.final()
        
        socket.close()
      end
    end
  end
end