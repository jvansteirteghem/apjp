PRE-INSTALLATION
----------------
- create DOTCLOUD account (http://www.dotcloud.com/)
- install DOTCLOUD CLI (http://docs.dotcloud.com/firststeps/)

INSTALLATION
------------
- unzip APJP_REMOTE_JAVA_DOTCLOUD-X.X.X.zip
- open APJP_REMOTE_JAVA_DOTCLOUD-X.X.X
- unzip ROOT.war
	- WEB-INF
	- META-INF
	- index.html
- open WEB-INF\APJP_REMOTE.properties
	- APJP_KEY == generated key
	- APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_X_KEY
	- APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_X_VALUE
	- APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_X_KEY
	- APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_X_VALUE
- zip ROOT.war
	- WEB-INF
	- META-INF
	- index.html
- open CONSOLE
	- cd APJP_REMOTE_JAVA_DOTCLOUD-X.X.X
	- dotcloud create application-id
	- dotcloud push application-id

IMPORTANT
---------
- APJP_REMOTE_HTTP_SERVER_X_REQUEST_URL == http://application-id.dotcloud.com/HTTP
- APJP_REMOTE_HTTPS_SERVER_X_REQUEST_URL == http://application-id.dotcloud.com/HTTPS