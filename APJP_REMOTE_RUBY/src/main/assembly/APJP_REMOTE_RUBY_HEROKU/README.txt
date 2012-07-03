PRE-INSTALLATION
----------------
- create HEROKU account (http://devcenter.heroku.com/articles/quickstart)
- install HEROKU SDK (http://devcenter.heroku.com/articles/quickstart)

INSTALLATION
------------
- unzip APJP_REMOTE_RUBY_HEROKU-X.X.X.zip
- open APJP_REMOTE_RUBY_HEROKU-X.X.X
- open config.ru
	- APJP::APJP.APJP_KEY == generated key
	- APJP::APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY[X]
	- APJP::APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE[X]
	- APJP::APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[X]
	- APJP::APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE[X]
- open CONSOLE
	- cd APJP_REMOTE_RUBY_HEROKU-X.X.X
	- heroku login
	- git init
	- git add .
	- git commit -m "init"
	- heroku create --stack cedar
	- git push heroku master
	- heroku open

IMPORTANT
---------
- APJP_REMOTE_HTTP_SERVER_X_REQUEST_URL == http://application-id.herokuapp.com/HTTP
- APJP_REMOTE_HTTPS_SERVER_X_REQUEST_URL == http://application-id.herokuapp.com/HTTPS