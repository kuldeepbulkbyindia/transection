package com.bbi.transactionsp2.model;

import java.security.SecureRandom;
import java.util.Random;

public class OrderIdGenerator {
    private static final String PREFIX = "BBI";
    private static final int LENGTH = 13;
    private static final String DIGITS = "0123456789";

    public static String generateOrderId() {
        Random random = new SecureRandom();
        StringBuilder orderId = new StringBuilder(LENGTH);

        orderId.append(PREFIX);

        // Generating 10 random digits after BBI
        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(DIGITS.length());
            char randomDigit = DIGITS.charAt(randomIndex);
            orderId.append(randomDigit);
        }
        //Example orderId: BBI6582031475
        return orderId.toString();
    }
//9,999,999,999 max-limit
}
