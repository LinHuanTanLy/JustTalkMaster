package com.luwei.testjusttalk.utils;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Locale;

public class Signer {
	
	public static String signWithKey(String key, String id, String nonce, int expires) {
		if (key == null || key.length() == 0) {
			return null;
		}

		long curTime = System.currentTimeMillis() / 1000;
		String strBuf = String.format(Locale.getDefault(),
				"ID=%s\nNonce=%s\nBegin=%d\nEnd=%d\n\n", id, nonce, curTime,
				curTime + expires);
		byte[] bufBytes = strBuf.getBytes();
		byte[] encryptBytes = null;
		try {
			RSAPrivateKey privateKey = loadPrivateKey(key);
			encryptBytes = encrypt(privateKey, bufBytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] totalBytes = new byte[bufBytes.length + encryptBytes.length];
		System.arraycopy(bufBytes, 0, totalBytes, 0, bufBytes.length);
		System.arraycopy(encryptBytes, 0, totalBytes, bufBytes.length,
				encryptBytes.length);
		return Base64Utils.encode(totalBytes);
	}
	
	public static String signWithFile(String fileName, String id, String nonce, int expires) {
		if (fileName == null || fileName.length() == 0) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		try {
			File file = new File(fileName);
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String readLine = null;
	        while((readLine = br.readLine()) != null) {  
	            if(readLine.charAt(0) == '-') {  
	                continue;  
	            }else{  
	                sb.append(readLine);  
	            }  
	        }  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return signWithKey(sb.toString(), id, nonce, expires);
	}
	
	public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
		byte[] buffer = Base64Utils.decode(privateKeyStr);
        RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(buffer));
    	RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
    	KeyFactory keyFactory= KeyFactory.getInstance("RSA");
    	return (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivKeySpec);
	}
	
	public static byte[] encrypt(RSAPrivateKey privateKey, byte[] data) throws Exception {
        if(privateKey == null){  
        	return null;  
        }  
        
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    } 
}
