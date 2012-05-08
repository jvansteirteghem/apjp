import APJP_HTTP
import APJP_HTTPS
from webob import Request
from webob import Response

def application(environ, start_response):
  request = Request(environ)
  
  environ['APJP_KEY'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_1_KEY'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_1_VALUE'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_2_KEY'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_2_VALUE'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_3_KEY'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_3_VALUE'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_4_KEY'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_4_VALUE'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_5_KEY'] = ''
  environ['APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_5_VALUE'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_1_KEY'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_1_VALUE'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_2_KEY'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_2_VALUE'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_3_KEY'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_3_VALUE'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_4_KEY'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_4_VALUE'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_5_KEY'] = ''
  environ['APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_5_VALUE'] = ''
  
  if request.path_info == '/HTTP':
    return APJP_HTTP.application(environ, start_response)
  else:
    if request.path_info == '/HTTPS':
      return APJP_HTTPS.application(environ, start_response)
  
  response = Response(None, None, None, None, request)
  return response(environ, start_response)