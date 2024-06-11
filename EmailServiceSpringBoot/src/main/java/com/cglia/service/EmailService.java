package com.cglia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";

	private Key getAESKey(String key) throws Exception {
		byte[] keyBytes = key.getBytes("UTF-8");
		keyBytes = Arrays.copyOf(keyBytes, 16); // use 16 bytes for AES-128, 24 for AES-192, 32 for AES-256
		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	public void encryptFile(String key, File inputFile, File outputFile) throws Exception {
		Key secretKey = getAESKey(key);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		try (FileInputStream inputStream = new FileInputStream(inputFile);
				FileOutputStream outputStream = new FileOutputStream(outputFile);
				CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				cipherOutputStream.write(buffer, 0, bytesRead);
			}
		}
	}

	public void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentPath)
			throws MessagingException, IOException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom("mahanasoumyaranjan@gmail.com");
		helper.setTo(toEmail);
		helper.setSubject(subject);
		helper.setText(body);

		File attachment = new File(attachmentPath);
		helper.addAttachment(attachment.getName(), attachment);

		javaMailSender.send(message);
	}

	public void sendEmailWithEncryptedAttachment(String toEmail, String subject, String body, String filePath,
			String password) throws Exception {
		File inputFile = new File(filePath);
		File encryptedFile = new File(inputFile.getParent(), "encrypted_" + inputFile.getName());

		encryptFile(password, inputFile, encryptedFile);
		sendEmailWithAttachment(toEmail, subject, body, encryptedFile.getAbsolutePath());

		// Optionally, delete the encrypted file after sending
		encryptedFile.delete();
	}

	public void decryptFile(String password, String encryptedFilePath, String decryptedFilePath) throws Exception {
		File encryptedFile = new File(encryptedFilePath);
		File decryptedFile = new File(decryptedFilePath);
		FileDecryption.decryptFile(password, encryptedFile, decryptedFile);
	}
}
