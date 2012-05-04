from Crypto.Cipher import ARC4
from httplib import HTTPSConnection
from webob import Request
from webob import Response

def execute(environ, start_response):
  request = Request(environ)
  response = Response()
  
  decipher = ARC4.new(environ['APJP_KEY'])
  
  http_request_header = ''
  
  while True:
    buffer = request.body_file.read(1)
    buffer_length = len(buffer)
    if buffer_length == 0:
      break
    http_request_header = http_request_header + decipher.decrypt(buffer)
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
  http_request_headers = {}
  
  i = 1
  while i < http_request_header_values_length - 1:
    http_request_header_value1 = http_request_header_values[i]
    http_request_header_values1 = http_request_header_value1.split(': ')
    http_request_header_values1_length = len(http_request_header_values1)
    if http_request_header_values1_length == 2:
      http_request_header_value2 = http_request_header_values1[0]
      http_request_header_value3 = http_request_header_values1[1]
      if http_request_headers.get(http_request_header_value2) is None:
        http_request_headers[http_request_header_value2] = http_request_header_value3
      else:
        http_request_headers[http_request_header_value2] = http_request_headers[http_request_header_value2] + ', ' + http_request_header_value3
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
  
  http_request_body = ''
  
  while True:
    buffer = request.body_file.read(5120)
    buffer_length = len(buffer)
    if buffer_length == 0:
      break
    http_request_body = http_request_body + decipher.decrypt(buffer)
  
  http_request_method = ''
  http_request_url = ''
  
  http_request_header_value1 = http_request_header_values[0]
  http_request_header_values1 = http_request_header_value1.split(' ')
  http_request_header_values1_length = len(http_request_header_values1)
  if http_request_header_values1_length == 3:
    http_request_method = http_request_header_values1[0]
    http_request_url = http_request_header_values1[1]
  
  https_connection = HTTPSConnection(http_request_address, http_request_port, timeout=60)
  https_connection.request(http_request_method, http_request_url, http_request_body, http_request_headers)
  http_response = https_connection.getresponse()
  
  i = 1
  while i <= 5:
    if environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_' + str(i) + '_KEY'] != '':
      response.headers.add(environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_' + str(i) + '_KEY'], environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_' + str(i) + '_VALUE'])
    i = i + 1
  
  cipher = ARC4.new(environ['APJP_KEY'])
  
  http_response_header = ''
  
  if http_response.version == 10:
    http_response_header = cipher.encrypt('HTTP/1.0 ' + str(http_response.status) + '\r\n')
  else:
    http_response_header = cipher.encrypt('HTTP/1.1 ' + str(http_response.status) + '\r\n')
  
  http_response_headers = http_response.getheaders()
  
  for (http_response_header_key, http_response_header_value) in http_response_headers:
    if http_response_header_key.upper() == 'Set-Cookie'.upper():
      http_response_header_values = http_response_header_value.split(', ')
      http_response_header_value1 = ''
      for http_response_header_value2 in http_response_header_values:
        if http_response_header_value1 == '':
          http_response_header_value1 = http_response_header_value2
        else:
          http_response_header_values2 = http_response_header_value2.split(';')
          http_response_header_value3 = http_response_header_values2[0]
          http_response_header_values3 = http_response_header_value3.split(' ')
          http_response_header_values3_length = len(http_response_header_values3)
          if http_response_header_values3_length == 3:
            http_response_header_value1 = http_response_header_value1 + ', ' + http_response_header_value2
          else:
            http_response_header = http_response_header + cipher.encrypt(http_response_header_key + ': ' + http_response_header_value1 + '\r\n')
            http_response_header_value1 = http_response_header_value2
      http_response_header = http_response_header + cipher.encrypt(http_response_header_key + ': ' + http_response_header_value1 + '\r\n')
    else:
      http_response_header = http_response_header + cipher.encrypt(http_response_header_key + ': ' + http_response_header_value + '\r\n')
  
  http_response_header = http_response_header + cipher.encrypt('\r\n')
  
  response.body_file.write(http_response_header)
  
  while True:
    buffer = http_response.read(5120)
    buffer_length = len(buffer)
    if buffer_length == 0:
      break
    response.body_file.write(cipher.encrypt(buffer))
  
  https_connection.close()
  
  return response(environ, start_response)