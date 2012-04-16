/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.HTTPS;

import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Name;
import iaik.security.rsa.RSAKeyPairGenerator;
import iaik.x509.X509Certificate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import APJP.APJP;
import APJP.Logger;

public class HTTPS
{
	private static Logger logger;
	private static KeyStore defaultKeyStore;
	
	static
	{
		logger = Logger.getLogger(APJP.APJP_LOCAL_HTTPS_SERVER_LOGGER_ID);
		
		defaultKeyStore = null;
	}
	
	private static KeyStore getDefaultKeyStore() throws HTTPSException
	{
		try
		{
			if(defaultKeyStore == null)
			{
				defaultKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				try
				{
					defaultKeyStore.load(new FileInputStream("APJP_LOCAL.jks"), "APJP".toCharArray());
				}
				catch(Exception e)
				{
					defaultKeyStore.load(null, "APJP".toCharArray());
					
					KeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
					keyPairGenerator.initialize(1024);
					
					KeyPair keyPair = keyPairGenerator.generateKeyPair();
					
					X509Certificate x509CertificateAuthority = new X509Certificate();
					
					Name name = new Name();
					name.addRDN(new ObjectID("2.5.4.3"), "APJP"); //CN
					name.addRDN(new ObjectID("2.5.4.10"), "APJP"); // O
					name.addRDN(new ObjectID("2.5.4.11"), "APJP"); // OU
					
					x509CertificateAuthority.setSubjectDN(name);
					x509CertificateAuthority.setIssuerDN(name);
					x509CertificateAuthority.setValidNotBefore(new Date(new Date().getTime() - 1 * (1000L * 60 * 60 * 24 * 365)));           
					x509CertificateAuthority.setValidNotAfter(new Date(new Date().getTime() + 10 * (1000L * 60 * 60 * 24 * 365)));
					x509CertificateAuthority.setSerialNumber(BigInteger.valueOf(new Date().getTime()));
					x509CertificateAuthority.setPublicKey(keyPair.getPublic());
					
					x509CertificateAuthority.sign(new AlgorithmID(new ObjectID("1.2.840.113549.1.1.5")), keyPair.getPrivate()); // SHA1_WITH_RSA_ENCRYPTION
					
					x509CertificateAuthority.writeTo(new FileOutputStream("APJP_LOCAL.pem"));
					
					X509Certificate[] x509CertificateArray = new X509Certificate[1];
					x509CertificateArray[0] = x509CertificateAuthority;
					
					defaultKeyStore.setCertificateEntry("APJP", x509CertificateAuthority);
					defaultKeyStore.setKeyEntry("APJP", keyPair.getPrivate(), "APJP".toCharArray(), x509CertificateArray);
					
					defaultKeyStore.store(new FileOutputStream("APJP_LOCAL.jks"), "APJP".toCharArray());
				}
			}
			
			return defaultKeyStore;
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS/GET_DEFAULT_KEY_STORE: EXCEPTION", e);
			
			throw new HTTPSException("HTTPS/GET_DEFAULT_KEY_STORE", e);
		}
	}
	
	public static synchronized SSLSocket createSSLSocket() throws HTTPSException
	{
		try
		{
			KeyStore defaultKeyStore = getDefaultKeyStore();
			
			PrivateKey privateKey = (PrivateKey) defaultKeyStore.getKey("APJP", "APJP".toCharArray());
			
			Certificate certificateAuthority = defaultKeyStore.getCertificate("APJP");
			
			Certificate[] certificateArray = new Certificate[1];
			certificateArray[0] = certificateAuthority;
			
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, "APJP".toCharArray());
			keyStore.setCertificateEntry("APJP", certificateAuthority);
			keyStore.setKeyEntry("APJP", privateKey, "APJP".toCharArray(), certificateArray);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "APJP".toCharArray());
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			
			sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(), null);
			
			SSLSocketFactory sslSocketFactory = (SSLSocketFactory) sslContext.getSocketFactory();
			
			return (SSLSocket) sslSocketFactory.createSocket();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS/CREATE_SSL_SOCKET: EXCEPTION", e);
			
			throw new HTTPSException("HTTPS/CREATE_SSL_SOCKET", e);
		}
	}
	
	public static synchronized SSLServerSocket createSSLServerSocket(String remoteAddress, int remotePort) throws HTTPSException
	{
		try
		{
			KeyStore defaultKeyStore = getDefaultKeyStore();
			
			PrivateKey privateKey = (PrivateKey) defaultKeyStore.getKey("APJP", "APJP".toCharArray());
			
			Certificate certificateAuthority = defaultKeyStore.getCertificate("APJP");
			
			String certificateAlias;
			
			if(remotePort == 443)
			{
				certificateAlias = remoteAddress;
			}
			else
			{
				certificateAlias = remoteAddress + ":" + remotePort;
			}
			
			Certificate certificate = defaultKeyStore.getCertificate(certificateAlias);
			
			if(certificate == null)
			{
				X509Certificate x509CertificateAuthority = new X509Certificate(certificateAuthority.getEncoded());
				
				X509Certificate x509Certificate = new X509Certificate();
				
				Name name = new Name();
				name.addRDN(new ObjectID("2.5.4.3"), certificateAlias); //CN
				name.addRDN(new ObjectID("2.5.4.10"), "APJP"); // O
				name.addRDN(new ObjectID("2.5.4.11"), "APJP"); // OU
				
				x509Certificate.setSubjectDN(name);
				x509Certificate.setIssuerDN(x509CertificateAuthority.getIssuerDN());
				x509Certificate.setValidNotBefore(new Date(new Date().getTime() - 1 * (1000L * 60 * 60 * 24 * 365)));           
				x509Certificate.setValidNotAfter(new Date(new Date().getTime() + 10 * (1000L * 60 * 60 * 24 * 365)));
				x509Certificate.setSerialNumber(BigInteger.valueOf(new Date().getTime()));
				x509Certificate.setPublicKey(x509CertificateAuthority.getPublicKey());
				
				x509Certificate.sign(new AlgorithmID(new ObjectID("1.2.840.113549.1.1.5")), privateKey); // SHA1_WITH_RSA_ENCRYPTION
				
				X509Certificate[] x509CertificateArray = new X509Certificate[2];
				x509CertificateArray[0] = x509Certificate;
				x509CertificateArray[1] = x509CertificateAuthority;
				
				defaultKeyStore.setCertificateEntry(certificateAlias, x509Certificate);
				defaultKeyStore.setKeyEntry(certificateAlias, privateKey, "APJP".toCharArray(), x509CertificateArray);
				
				certificate = x509Certificate;
			}
			
			Certificate[] certificateArray = new Certificate[2];
			certificateArray[0] = certificate;
			certificateArray[1] = certificateAuthority;
			
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, "APJP".toCharArray());
			keyStore.setCertificateEntry("APJP", certificate);
			keyStore.setKeyEntry("APJP", privateKey, "APJP".toCharArray(), certificateArray);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "APJP".toCharArray());
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			
			sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(), null);
			
			SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) sslContext.getServerSocketFactory();
			
			return (SSLServerSocket) sslServerSocketFactory.createServerSocket();
		}
		catch(Exception e)
		{
			logger.log(2, "HTTPS/CREATE_SSL_SERVER_SOCKET: EXCEPTION", e);
			
			throw new HTTPSException("HTTPS/CREATE_SSL_SERVER_SOCKET", e);
		}
	}
}
