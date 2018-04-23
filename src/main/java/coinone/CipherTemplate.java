package coinone;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;


public class CipherTemplate {
   
   private String _algorithm;
   private SecretKeySpec _keySpec;
   
   public CipherTemplate(String algorithm, String secretKey) {
	   _algorithm = algorithm;
	   
       byte[] raw = stringToBytes(secretKey);
       _keySpec = new SecretKeySpec(raw, algorithm); 
   }
   
   public byte[] encrypt(byte[] a) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	   Cipher cipher = Cipher.getInstance(_algorithm);
       cipher.init(Cipher.ENCRYPT_MODE, _keySpec);
       return cipher.doFinal(a);	   
   }

   public String encrypt(String str) {
	   try {
		   byte[] encrypted = encrypt(str.getBytes());
		   return new String(Hex.encodeHex(encrypted));
	   } catch(Exception e) {
		   throw new RuntimeException("CipherTemplate encryption is failed", e);
	   }
   }

   public byte[] decrypt(byte[] a) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	   Cipher cipher = Cipher.getInstance(_algorithm);
       cipher.init(Cipher.DECRYPT_MODE, _keySpec);
       return cipher.doFinal(a);
   }
   
   public String decrypt(String encStr) {
		try {
			byte[] encrypted = Hex.decodeHex(encStr.toCharArray());			
			return new String(decrypt(encrypted));
		} catch (Exception e) {
			throw new RuntimeException("CipherTemplate decryption is failed", e);
		}
   }

   public static String generateRandomSecretKey(String algorithm) throws Exception{
      KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
      keyGen.init(128);
      SecretKey key = keyGen.generateKey();
      byte[] raw = key.getEncoded();
      return bytesToString(raw);
   }

   private static String bytesToString(byte[] bytes){
      byte[] b2 = new byte[bytes.length + 1];
      b2[0] = 1;
      System.arraycopy(bytes, 0, b2, 1, bytes.length);
      return new BigInteger(b2).toString(Character.MAX_RADIX);
   }

   private static byte[] stringToBytes(String str) {
      byte[] bytes = new BigInteger(str, Character.MAX_RADIX).toByteArray();
      return Arrays.copyOfRange(bytes, 1, bytes.length);
   }

}
