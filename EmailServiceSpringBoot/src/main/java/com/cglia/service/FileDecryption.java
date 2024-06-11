package com.cglia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class FileDecryption {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";

	private static Key getAESKey(String key) throws Exception {
		byte[] keyBytes = key.getBytes("UTF-8");
		keyBytes = Arrays.copyOf(keyBytes, 16); // use 16 bytes for AES-128, 24 for AES-192, 32 for AES-256
		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	public static void decryptFile(String key, File inputFile, File outputFile) throws Exception {
		Key secretKey = getAESKey(key);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		try (FileInputStream inputStream = new FileInputStream(inputFile);
				FileOutputStream outputStream = new FileOutputStream(outputFile);
				CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
	}
}
