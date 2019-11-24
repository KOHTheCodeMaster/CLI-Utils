package edc;

import dev.koh.stdlib.utils.KOHStringUtil;
import dev.koh.stdlib.utils.MyTimer;
import dev.koh.stdlib.utils.enums.StringOptions;

import java.io.File;

public class EDCApp {

    public static void main(String[] args) {

        System.out.println("Begin.\n");

        EDCApp obj = new EDCApp();
        obj.begin();

        System.out.println("\nEnd.");

    }

    private void begin() {

        MyTimer myTimer = new MyTimer();
        myTimer.startTimer();

        String promptSrcDir = "Enter Src Dir Path : ";
        String dirPath = KOHStringUtil.userInputString(promptSrcDir, StringOptions.DIR, myTimer);

        if (dirPath == null) {
            System.out.println("Invalid Src Dir Path!" +
                    "\nProgram Terminating...");
            throw new NullPointerException("No Src Dir. Found...");
        }
        File srcDir = new File(dirPath);

        String promptShouldDeleteImmediately = "Wanna Delete Encountered Empty Dirs. Immediately? [Y/N] : ";
        String ansShouldDeleteImmediately = KOHStringUtil.userInputString(promptShouldDeleteImmediately, StringOptions.YES_OR_NO, myTimer);
        boolean shouldDeleteImmediately = ansShouldDeleteImmediately.toLowerCase().charAt(0) == 'y';

        EmptyDirCleaner emptyDirCleaner = new EmptyDirCleaner(srcDir, shouldDeleteImmediately);
        emptyDirCleaner.startCleaning();

        System.out.println("\nCleaning Empty Dirs. Successful..!!");
        myTimer.stopTimer(true);

    }
}
