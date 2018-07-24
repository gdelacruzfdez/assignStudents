package uniovi.assign;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gdelacru
 */
public class InputThread extends Thread {

    private AtomicBoolean generateResults;

    public InputThread(AtomicBoolean generateResults) {
        this.generateResults = generateResults;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String input = sc.next();

            switch (input.toUpperCase()) {
                case "G":
                    System.out.println("Generating results...");
                    generateResults.set(true);
            }
        }
    }
}
