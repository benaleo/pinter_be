package com.kasirpinter.pos.util;

import java.util.Random;

public class RandomStringGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String EXCLUDE = "0O1LiSZ52";

    public static String generateRandomTextQREvent(int LENGTH) {
        // Membuat daftar karakter valid dengan menghapus karakter yang dikecualikan
        StringBuilder validChars = new StringBuilder();
        for (char c : ALPHABET.toCharArray()) {
            if (EXCLUDE.indexOf(c) == -1) { // Jika karakter tidak ada di EXCLUDE
                validChars.append(c);
            }
        }

        // Memastikan ada karakter valid tersedia
        if (validChars.length() == 0) {
            throw new IllegalArgumentException("Tidak ada karakter valid tersedia setelah penghapusan.");
        }

        // Membangun string acak
        Random random = new Random();
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(validChars.length());
            char randomChar = validChars.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String generateRandomAlphabetString(int LENGTH) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            char randomChar = ALPHABET.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}