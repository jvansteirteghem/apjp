require "net/http"
require "openssl"
require "rack"

$APJP_KEY = ""
$APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY = ["", "", "", "", ""]
$APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE = ["", "", "", "", ""]

class HTTP
  def call(env)
    request = Rack::Request.new(env)
    
    http_response_headers = {}
    
    for i in 0 .. $APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY.length - 1
      if $APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[i] != "" then
        http_response_headers[$APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[i]] = $APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE[i]
      end
    end
    
    response = Rack::Response.new(HTTPEnumerable.new(request), 200, http_response_headers)
    return response
  end
end

class HTTPEnumerable
  include Enumerable
  
  def initialize(request)
    @request = request
  end
  
  def each
    decipher = OpenSSL::Cipher::Cipher.new("RC4")
    decipher.decrypt
    decipher.key = $APJP_KEY
    decipher.iv  = ""
    
    http_request_header = ""
    http_request_header_length = http_request_header.length
    
    buffer = @request.body.read(1)
    while buffer != nil
      http_request_header = http_request_header + decipher.update(buffer)
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
    http_request_headers = {}
    
    i = 1
    while i < http_request_header_values_length
      http_request_header_value1 = http_request_header_values[i]
      http_request_header_values1 = http_request_header_value1.split(": ")
      http_request_header_values1_length = http_request_header_values1.length
      if http_request_header_values1_length == 2 then
        http_request_header_value2 = http_request_header_values1[0]
        http_request_header_value3 = http_request_header_values1[1]
        if http_request_headers[http_request_header_value2] == nil then
          http_request_headers[http_request_header_value2] = http_request_header_value3
        else
          http_request_headers[http_request_header_value2] = http_request_headers[http_request_header_value2] + ", " + http_request_header_value3
        end
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
    
    http_request_body = ""
    
    buffer = @request.body.read(5120)
    while buffer != nil
      http_request_body = http_request_body + decipher.update(buffer)
      
      buffer = @request.body.read(5120)
    end
    
    http_request_body = http_request_body + decipher.final()
    
    http_request_method = ""
    http_request_url = ""
    
    http_request_header_value1 = http_request_header_values[0]
    http_request_header_values1 = http_request_header_value1.split(" ")
    http_request_header_values1_length = http_request_header_values1.length
    if http_request_header_values1_length == 3 then
      http_request_method = http_request_header_values1[0]
      http_request_url = http_request_header_values1[1]
    end
    
    http = Net::HTTP.new(http_request_address, http_request_port)
    
    http_request = nil
    
    if http_request_method.upcase() == "COPY".upcase() then
      http_request = Net::HTTP::Copy.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "DELETE".upcase() then
      http_request = Net::HTTP::Delete.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "GET".upcase() then
      http_request = Net::HTTP::Get.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "HEAD".upcase() then
      http_request = Net::HTTP::Head.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "LOCK".upcase() then
      http_request = Net::HTTP::Lock.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "MKCOL".upcase() then
      http_request = Net::HTTP::Mkcol.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "MOVE".upcase() then
      http_request = Net::HTTP::Move.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "OPTIONS".upcase() then
      http_request = Net::HTTP::Options.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "PATCH".upcase() then
      http_request = Net::HTTP::Patch.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "POST".upcase() then
      http_request = Net::HTTP::Post.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "PROPFIND".upcase() then
      http_request = Net::HTTP::Propfind.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "PROPPATCH".upcase() then
      http_request = Net::HTTP::Proppatch.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "PUT".upcase() then
      http_request = Net::HTTP::Put.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "TRACE".upcase() then
      http_request = Net::HTTP::Trace.new(http_request_url, http_request_headers)
    elsif http_request_method.upcase() == "UNLOCK".upcase() then
      http_request = Net::HTTP::Unlock.new(http_request_url, http_request_headers)
    end
    
    http_request.body = http_request_body
    
    http.request(http_request){ |http_response|
      cipher = OpenSSL::Cipher::Cipher.new("RC4")
      cipher.encrypt
      cipher.key = $APJP_KEY
      cipher.iv  = ""
      
      http_response_header = "HTTP/" + http_response.http_version + " " + http_response.code + " " + http_response.message + "\r\n"
      
      http_response.each_header { |http_response_header_key|
        http_response_header_value = http_response[http_response_header_key]
        
        if http_response_header_key.upcase() == "Set-Cookie".upcase() then
          http_response_header_values = http_response_header_value.split(", ")
          http_response_header_value1 = ""
          for http_response_header_value2 in http_response_header_values
            if http_response_header_value1 == "" then
              http_response_header_value1 = http_response_header_value2
            else
              http_response_header_values2 = http_response_header_value2.split(";")
              http_response_header_value3 = http_response_header_values2[0]
              http_response_header_values3 = http_response_header_value3.split(" ")
              http_response_header_values3_length = http_response_header_values3.length
              if http_response_header_values3_length == 3 then
                http_response_header_value1 = http_response_header_value1 + ", " + http_response_header_value2
              else
                http_response_header = http_response_header + http_response_header_key + ": " + http_response_header_value1 + "\r\n"
                http_response_header_value1 = http_response_header_value2
              end
            end
          end
          http_response_header = http_response_header + http_response_header_key + ": " + http_response_header_value1 + "\r\n"
        else
          http_response_header = http_response_header + http_response_header_key + ": " + http_response_header_value + "\r\n"
        end
      }
      
      http_response_header = http_response_header + "\r\n"
      
      yield cipher.update(http_response_header)
      
      http_response.read_body { |http_response_body|
        if http_response_body != "" then
          yield cipher.update(http_response_body)
        end
      }
      
      yield cipher.final()
    }
  end
end