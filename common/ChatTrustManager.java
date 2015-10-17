package common;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ChatTrustManager implements X509TrustManager
{
	private static final String CERT_PATH = "/resc/distribCert.jks";
	
	private static X509TrustManager pkixTM;
	
	public ChatTrustManager() throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException
	{
		InputStream in = this.getClass().getResourceAsStream(CERT_PATH);
		
		Certificate cert = CertificateFactory.getInstance("X509").generateCertificate(in);
		
		KeyStore keyStore = KeyStore.getInstance("JKS");
		
		keyStore.load(null, null);
		keyStore.setCertificateEntry("distribCert", cert);
		
		TrustManagerFactory tmFact = TrustManagerFactory.getInstance("PKIX");
		tmFact.init(keyStore);
		
		for (TrustManager tm: tmFact.getTrustManagers()) {
			if (tm instanceof X509TrustManager) {
				pkixTM = (X509TrustManager) tm;
			}
		}
	}
	

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{
		pkixTM.checkClientTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{
		pkixTM.checkServerTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return pkixTM.getAcceptedIssuers();
	}
}
