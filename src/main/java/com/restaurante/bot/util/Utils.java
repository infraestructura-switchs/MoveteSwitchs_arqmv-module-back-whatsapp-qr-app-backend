package com.restaurante.bot.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@Service
public class Utils {

    private static final BCryptPasswordEncoder passwordEcorder = new BCryptPasswordEncoder();
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Utils() {

    }

    public String bcryptEncryptor(String plainText) {
        return passwordEcorder.encode(plainText);
    }

    public Boolean doPasswordsMatch(String rawPassword, String encodedPassword) {
        return passwordEcorder.matches(rawPassword, encodedPassword);
    }

    public String dateFormat(Timestamp date, String sbFormat) {
        String formattedDate = new SimpleDateFormat(sbFormat).format(date);
        return formattedDate;
    }

    public Timestamp parseTimestampDateTime(String timestamp) {
        try {
            return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public Timestamp parseTimestampDate(String timestamp) {
        try {
            return new Timestamp(DATE_FORMAT.parse(timestamp).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
}
