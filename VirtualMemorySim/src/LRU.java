import java.util.*;

public class LRU {
    private int no_of_frames;
    private HashSet<Integer> frames;
    private HashMap<Integer, Integer> last_used_frame;
    private int time = 0;
    private int hits = 0;
    private int misses = 0;
    private int page_index = 0;
    private int[] pages;

    public LRU(int[] pages, int no_of_frames) {
        this.pages = pages;
        this.no_of_frames = no_of_frames;
        frames = new HashSet<>(no_of_frames);
        last_used_frame = new HashMap<>();
    }

    public StepResult step() {
        if (page_index >= pages.length) return null;

        int page = pages[page_index];
        time++;
        boolean hit = frames.contains(page);

        if (hit) hits++;
        else {
            misses++;
            if (frames.size() < no_of_frames) {
                frames.add(page);
            } else {
                int lruPage = -1;
                int minTime = Integer.MAX_VALUE;
                for (int f : frames) {
                    if (last_used_frame.get(f) < minTime) {
                        minTime = last_used_frame.get(f);
                        lruPage = f;
                    }
                }
                frames.remove(lruPage);
                frames.add(page);
            }
        }
        last_used_frame.put(page, time);
        page_index++;
        List<Integer> f = new ArrayList<>(frames);
        return new StepResult(hit, f, hits, misses);
    }

}
