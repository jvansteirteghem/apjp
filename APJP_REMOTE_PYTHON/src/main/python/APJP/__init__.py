import APJP.HTTP
import APJP.HTTPS
from webob import Request
from webob import Response

def application(environ, start_response):
  request = Request(environ)
  
  if request.path_info == '/HTTP':
    return APJP.HTTP.application(environ, start_response)
  else:
    if request.path_info == '/HTTPS':
      return APJP.HTTPS.application(environ, start_response)
  
  response = Response(None, None, None, None, request)
  return response(environ, start_response)