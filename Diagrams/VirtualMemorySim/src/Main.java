import java.util.Arrays;
import java.util.Scanner;
public class Main {

    public static void print_step(StepResult step, int no_of_frames, int[] a){
        System.out.println("Frames: " + no_of_frames);
        System.out.println("Array: " + Arrays.toString(a));
        System.out.println("Current frames: " + step.frames.toString());
        System.out.println("Hit: " + step.hit + " | Hits: " + step.hits + " | Misses: " + step.misses);
    }


    public static void main(String[] args) {
        int[] pages = PageGenerator.generatePages(10,6);
        int no_of_frames = PageGenerator.generateFrameCount(2,5);

        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("Choose algorithm:");
        System.out.println("1 - Optimal");
        System.out.println("2 - FIFO");
        System.out.println("3 - LRU");
        System.out.println("exit");

        System.out.print("> ");
        input = sc.nextLine().trim().toLowerCase();
        StepResult result;
        switch (input) {
            case "1" -> {
                System.out.println("Optimal algorithm (pas-cu-pas):");
                Optimal optimal = new Optimal(pages, no_of_frames);
                while ((result = optimal.step()) != null) {
                    print_step(result,no_of_frames,pages);
                    System.out.println("Press Enter for next step...");
                    sc.nextLine();
                }
            }
            case "2" -> {
                System.out.println("FIFO algorithm (pas-cu-pas):");
                FIFO fifo = new FIFO(pages, no_of_frames);
                while ((result = fifo.step()) != null) {
                    print_step(result,no_of_frames,pages);
                    System.out.println("Press Enter for next step...");
                    sc.nextLine();
                }
            }
            case "3" -> {
                System.out.println("LRU algorithm (pas-cu-pas):");
                LRU lru = new LRU(pages, no_of_frames);
                while ((result = lru.step()) != null) {
                    print_step(result,no_of_frames,pages);
                    System.out.println("Press Enter for next step...");
                    sc.nextLine();
                }
            }
            case "exit" -> {
                sc.close();
                System.exit(0);
                System.out.println("Exiting...");
            }
            default -> System.out.println("Invalid input");
        }

        sc.close();
        System.out.println("Simulation finished.");
    }

}
