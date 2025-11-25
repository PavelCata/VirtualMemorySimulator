package Helpers;

import java.util.Random;

public class PageGenerator {
    public static int[] generatePages(int size, int max_page_nr) {
        Random rand = new Random();
        int[] pages = new int[size];
        for (int i = 0; i < size; i++) {
            pages[i] = rand.nextInt(max_page_nr);
        }
        return pages;
    }

    public static int generateFrameCount(int min_frames, int max_frames) {
        Random rand = new Random();
        return rand.nextInt(max_frames - min_frames + 1) + min_frames;
    }
}
