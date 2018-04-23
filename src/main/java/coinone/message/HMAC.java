package coinone.message;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {

	private final SecretKeySpec _keySpec;
	private final String _alg;
	
	public HMAC(String key, String alg) throws UnsupportedEncodingException {
		_alg = alg;        
        _keySpec = new SecretKeySpec(key.getBytes(), alg);
	}
	
	public byte[] digest(byte[] a) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance(_alg);
		mac.init(_keySpec);
		return mac.doFinal(a);
	}
	
	
}
