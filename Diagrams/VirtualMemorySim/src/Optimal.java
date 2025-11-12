import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Optimal {
    private int[] frames;
    private int[] pages;
    private int frame_count;
    private int page_index = 0;
    private int hits = 0;

    public Optimal(int[] pages, int frame_count) {
        this.pages = pages;
        this.frame_count = frame_count;
        this.frames = new int[frame_count];
        Arrays.fill(frames, -1);
    }

    private boolean search(int key) {
        for (int i = 0; i < frames.length; i++)
            if (frames[i] == key)
                return true;
        return false;
    }

    private int predict(int index) {
        int res = -1, farthest = index;
        for (int i = 0; i < frames.length; i++) {
            int j;
            for (j = index; j < pages.length; j++) {
                if (frames[i] == pages[j]) {
                    if (j > farthest) {
                        farthest = j;
                        res = i;
                    }
                    break;
                }
            }
            if (j == pages.length)
                return i;
        }
        if (res == -1)
            return 0;
        else
            return res;
    }

    public StepResult step() {
        if (page_index >= pages.length)
            return null;

        int curr_page = pages[page_index];
        boolean hit = search(curr_page);

        if (hit) {
            hits++;
        } else {
            int empty = -1;
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] == -1) {
                    empty = i;
                    break;
                }
            }
            if (empty != -1) {
                frames[empty] = curr_page;
            } else {
                int new_index = predict(page_index + 1);
                frames[new_index] = curr_page;
            }
        }
        List<Integer> frame_list = new ArrayList<>();
        for (int f : frames) {
            if (f != -1) frame_list.add(f);
        }

        page_index++;
        return new StepResult(hit, frame_list, hits, page_index - hits);
    }


}
