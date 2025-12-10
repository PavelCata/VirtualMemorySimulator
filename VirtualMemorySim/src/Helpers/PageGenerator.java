package Helpers;

import java.security.SecureRandom;

public class PageGenerator {
    private static final SecureRandom rand = new SecureRandom();

    public static int[] generatePages(int size, int maxPage) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
            arr[i] = rand.nextInt(maxPage) + 1;
        return arr;
    }

    public static int generateFrameCount(int min_frames, int max_frames) {
        return rand.nextInt(max_frames - min_frames + 1) + min_frames;
    }
}

