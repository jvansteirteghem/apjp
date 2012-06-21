PRE-INSTALLATION
----------------
- create DOTCLOUD account (http://www.dotcloud.com/)
- install DOTCLOUD CLI (http://docs.dotcloud.com/firststeps/)

INSTALLATION
------------
- unzip APJP_REMOTE_PHP_DOTCLOUD-X.X.X.zip
- open APJP_REMOTE_PHP_DOTCLOUD-X.X.X
- open index.php
	- APJP_APJP::$APJP_KEY == generated key
	- APJP_APJP::$APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[X]
	- APJP_APJP::$APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE[X]
	- APJP_APJP::$APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[X]
	- APJP_APJP::$APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE[X]
- open CONSOLE
	- cd APJP_REMOTE_PHP_DOTCLOUD-X.X.X
	- dotcloud create application-id
	- dotcloud push application-id

IMPORTANT
---------
- APJP_REMOTE_HTTP_SERVER_X_REQUEST_URL == http://application-id.dotcloud.com/?HTTP
- APJP_REMOTE_HTTPS_SERVER_X_REQUEST_URL == http://application-id.dotcloud.com/?HTTPS