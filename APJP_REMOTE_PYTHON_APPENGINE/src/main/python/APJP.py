from webob import Request
from webob import Response

def execute(environ, start_response):
  request = Request(environ)
  response = Response()
  
  if request.method == 'POST':
    if request.path_info == '/HTTP':
      import APJP_HTTP
      return APJP_HTTP.execute(environ, start_response)
    else:
      if request.path_info == '/HTTPS':
        import APJP_HTTPS
        return APJP_HTTPS.execute(environ, start_response)
  
  return response(environ, start_response)