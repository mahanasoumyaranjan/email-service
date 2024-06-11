package com.cglia.utility;

import java.io.File;

import com.cglia.service.FileDecryption;

public class DecryptionTool {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java DecryptionTool <password> <encryptedFilePath> <decryptedFilePath>");
            return;
        }

        String password = args[0];
        String encryptedFilePath = args[1];
        String decryptedFilePath = args[2];

        try {
            FileDecryption.decryptFile(password, new File(encryptedFilePath), new File(decryptedFilePath));
            System.out.println("File decrypted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Decryption failed.");
        }
    }
}
