package Algorithms;

import java.util.*;
import Helpers.StepResult;

public class FIFO {
    private int frame_count;
    private HashSet<Integer> frames;
    private Queue<Integer> queue;
    private int page_index = 0;
    private int hits = 0;
    private int misses = 0;
    private int[] pages;

    public FIFO(int[] pages, int frame_count) {
        this.pages = pages;
        this.frame_count = frame_count;
        frames = new HashSet<>(frame_count);
        queue = new LinkedList<>();
    }

    public StepResult step() {
        if (page_index >= pages.length) return null;

        int page = pages[page_index];
        boolean hit = frames.contains(page);
        if (hit) hits++;
        else {
            misses++;
            if (frames.size() < frame_count) {
                frames.add(page);
                queue.add(page);
            } else {
                int removed = queue.poll();
                frames.remove(removed);
                frames.add(page);
                queue.add(page);
            }
        }
        page_index++;
        List<Integer> f = new ArrayList<>(frames);
        return new StepResult(hit, f, hits, misses);
    }
}
