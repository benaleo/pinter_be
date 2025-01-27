package com.kopibery.pos.util;

import org.jsoup.Jsoup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    // apps formater
    private static final DateTimeFormatter formatterApps = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String formatDateTimeApps(LocalDateTime formatDate) {
        return formatDate != null ? formatDate.format(formatterApps) : null;
    }

    // apps formater with second
    private static final DateTimeFormatter formatterAppsWithSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatterAppsWithSeconds(LocalDateTime formatDate) {
        return formatDate != null ? formatDate.format(formatterAppsWithSeconds) : null;
    }

    // string to LocalDate
    public static LocalDate stringToLocalDate(String date) {
        LocalDate parsedDate = null;
        if (date != null && !date.isEmpty()) {
            try {
                parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid date format: " + date, e);
            }
        }
        return parsedDate;
    }


    // cms formatter
    private static final DateTimeFormatter formatterLocalDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String formatLocalDateTime(LocalDateTime formatDate) {
        return formatDate != null ? formatDate.format(formatterLocalDateTime) : null;
    }

    private static final DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatLocalDate(LocalDate dateTime) {
        return dateTime != null ? dateTime.format(formatterLocalDate) : null;
    }

    public static String formatDescription(String description) {
        return Jsoup.parse(description).text();
    }


    public static String formatDescriptionReplyComment(String description) {
        // Handle null input
        if (description == null) {
            return "";  // Return empty string if description is null
        }

        // Menyiapkan regex untuk menangkap format mention @<UUID>(<Nama>)
        String regex = "@\\[__[^\\]]+__\\]\\(__([^\\)]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(description);

        // Loop untuk memproses semua bagian yang cocok
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            // Mengambil nama yang ada di dalam tanda kurung (__...)
            String name = matcher.group(1);

            // Mengganti spasi dengan underscore di dalam nama
            String modifiedName = name.replace(" ", "_");

            // Mengganti teks yang cocok dengan nama yang sudah dimodifikasi
            matcher.appendReplacement(result, "@" + modifiedName);
        }

        // Menyelesaikan string hasil
        matcher.appendTail(result);

        // Menggunakan Jsoup untuk membersihkan HTML jika ada
        return Jsoup.parse(result.toString()).text();
    }


    // elastic
    public static LocalDateTime parseToLocalDateTime(String dateString) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString);
        return offsetDateTime.toLocalDateTime();
    }

    // to get month
    public static String formatDateWithMonthName(LocalDateTime tanggal) {
        Map<Integer, String> bulan = new HashMap<>();
        bulan.put(1, "Jan");
        bulan.put(2, "Feb");
        bulan.put(3, "Mar");
        bulan.put(4, "Apr");
        bulan.put(5, "May");
        bulan.put(6, "Jun");
        bulan.put(7, "Jul");
        bulan.put(8, "Aug");
        bulan.put(9, "Sep");
        bulan.put(10, "Oct");
        bulan.put(11, "Nov");
        bulan.put(12, "Dec");

        int hari = tanggal.getDayOfMonth();
        String bulanStr = bulan.get(tanggal.getMonthValue());
        int tahun = tanggal.getYear();
        int jam = tanggal.getHour();
        int menit = tanggal.getMinute();

        return String.format("%02d %s %d %02d.%02d", hari, bulanStr, tahun, jam, menit);
    }
}