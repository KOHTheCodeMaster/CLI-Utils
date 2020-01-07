import stdlib.utils.KOHStringUtil;
import stdlib.utils.MyTimer;
import stdlib.utils.enums.StringOptions;

import java.io.File;
import java.io.IOException;

public class App {

    private MyTimer myTimer;
    private File targetFile;
    private boolean shouldDamageEntireFile;
    private boolean shouldDeleteFiles;

    public static void main(String[] args) {

        App app = new App();
        app.major();

    }

    private void major() {

//        File f = new File("F:\\UNSORTED\\More\\b1\\2\\1 - Copy - Copy\\Z - Copy - Copy - Copy (2) - Copy");
        try {
            myTimer = new MyTimer();
            userInputFilePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TheDataShredder theDataShredder = new TheDataShredder(shouldDamageEntireFile, shouldDeleteFiles, targetFile, 1);
        theDataShredder.start();

    }

    private void userInputFilePath() throws IOException {

        String promptFilePath = "Enter Valid File/Dir. Path : ";
        String filePath = KOHStringUtil.userInputString(promptFilePath, StringOptions.DIR_OR_FILE, myTimer);

        if (filePath == null) {
            System.out.println("No Valid File/Dir. Path Entered!\n" +
                    "Program Terminating...");
            System.exit(-17);
        } else targetFile = new File(filePath);

        String promptDeleteEntireFile = "Wanna Delete Entire File? [Y/N] : ";
        String strYesOrNo = KOHStringUtil.userInputString(promptDeleteEntireFile, StringOptions.YES_OR_NO, myTimer);
        char ch = strYesOrNo != null ? strYesOrNo.charAt(0) : 'n';

        if (ch == 'y' || ch == 'Y')
            shouldDamageEntireFile = true;

        String promptShouldDeleteFiles = "Wanna Delete Files After Shredding? [Y/N] : ";
        strYesOrNo = KOHStringUtil.userInputString(promptShouldDeleteFiles, StringOptions.YES_OR_NO, myTimer);
        ch = strYesOrNo != null ? strYesOrNo.charAt(0) : 'n';

        if (ch == 'y' || ch == 'Y')
            shouldDeleteFiles = true;

        String targetType = targetFile.isDirectory() ? "Directory" : "File";
        System.out.println(targetType + " To Be Destroyed: \n" + targetFile.getCanonicalPath());

    }


}

/*
 *  Date Created : 21st August 2K19, 09:44 PM..!!
 *  Time Stamp : 7th January 2K20, 06:51 PM..!!
 *
 *  Change Log:
 *
 *  3rd Commit - TheDataShredder-V-1-0
 *
 *  Completed the following Pending Task:
 *  Move renameFileNameToStr and deleteFileNow method to KOHFilesUtil [koh-std-lib]
 *
 *  1. Replaced local methods (deleteFileNow, renameFileNameToStr) with
 *     the static methods from the library koh-std-lib version 0.0.3
 *
 *  2nd Commit - Ready-To-Use Module
 *  1. TheDataShredder is made available as Ready-To-Use independent module
 *
 *  Pending Tasks:
 *  a. Abstract Print Logs when Shredding Files
 *  b. Move renameFileNameToStr and deleteFileNow method to KOHFilesUtil [koh-std-lib]
 *
 *  Init Commit - The Data Shredder [CLI]
 *  1. Shred Entire Files & Dirs. as well as Segments / Portions of Data.
 *
 *  Code Developed By,
 *  ~K.O.H..!! ^__^
 */