package Helpers;

import java.util.List;

public class StepResult {
    public boolean hit;
    public List<Integer> frames;
    public int hits;
    public int misses;

    public StepResult(boolean hit, List<Integer> frames, int hits, int misses) {
        this.hit = hit;
        this.frames = frames;
        this.hits = hits;
        this.misses = misses;
    }
}