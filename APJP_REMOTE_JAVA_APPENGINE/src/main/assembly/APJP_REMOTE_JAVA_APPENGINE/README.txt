PRE-INSTALLATION
----------------
- create GOOGLE APP ENGINE account (https://appengine.google.com)
- install GOOGLE APP ENGINE JAVA SDK (https://code.google.com/appengine/)

INSTALLATION
------------
- unzip APJP_REMOTE_JAVA_APPENGINE-X.X.X.zip
- open APJP_REMOTE_JAVA_APPENGINE-X.X.X
- open war\WEB-INF\appengine-web.xml
	- application-id
- open war\WEB-INF\APJP_REMOTE.properties
	- APJP_KEY == generated key
	- APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_X_KEY
	- APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_X_VALUE
	- APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_X_KEY
	- APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_X_VALUE
- open CONSOLE
	- cd APJP_REMOTE_JAVA_APPENGINE-X.X.X
	- GOOGLE APP ENGINE JAVA SDK\bin\appcfg.cmd update war\

IMPORTANT
---------
- APJP_REMOTE_HTTP_SERVER_X_REQUEST_URL == http://application-id.appspot.com/HTTP
- APJP_REMOTE_HTTPS_SERVER_X_REQUEST_URL == http://application-id.appspot.com/HTTPS