import APJP_HTTP
import APJP_HTTPS
from webob import Request
from webob import Response

def application(environ, start_response):
  request = Request(environ)
  
  if request.path_info == '/HTTP':
    return APJP_HTTP.application(environ, start_response)
  else:
    if request.path_info == '/HTTPS':
      return APJP_HTTPS.application(environ, start_response)
  
  response = Response(None, None, None, None, request)
  return response(environ, start_response)