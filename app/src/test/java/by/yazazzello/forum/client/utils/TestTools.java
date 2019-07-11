package by.yazazzello.forum.client.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class TestTools {

    static Random RANDOM = new Random();
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String TEST_ROOM_UUID = "00000000-0000-0000-0000-000000000000";
    public static final String MESSAGES = "messages";

    public static int randomInt() {
        Random generator = new Random();
        return generator.nextInt();
    }

    public static int randomInt(int maxVal) {
        Random generator = new Random();
        return generator.nextInt(maxVal);
    }

    public static long randomLong() {
        Random generator = new Random();
        return generator.nextLong();
    }

    public static double randomDouble() {
        Random generator = new Random();
        return generator.nextDouble();
    }

    public static String randomString(final int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(RANDOM.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static String readFile(String name) {
        InputStream stream = TestTools.class.getClassLoader().getResourceAsStream(name);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return name;
        }
    }


}
