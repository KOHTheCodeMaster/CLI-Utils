package edc;

import com.google.gson.Gson;
import stdlib.utils.KOHFilesUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EmptyDirCleaner {

    private File srcDir;
    private boolean shouldDeleteImmediately;

    /**
     * @param srcDir                  Source Directory for cleaning its empty dirs.
     * @param shouldDeleteImmediately <br>
     *                                true - delete empty dirs. immediately.<br>
     *                                false - skip the deletion process.
     */
    EmptyDirCleaner(File srcDir, boolean shouldDeleteImmediately) {

        if (!srcDir.isDirectory()) throw new NullPointerException("No Valid SrcDir found.");
        this.srcDir = srcDir;
        this.shouldDeleteImmediately = shouldDeleteImmediately;

    }

    /**
     * Firstly, check for the srcDir to be a valid dir. if not then return NullPointerException <br>
     * Next, Instantiate {@link MyDirVisitor} with shouldDeleteImmediately <br>
     * Next, Walk File Tree with srcDir <br>
     * Finally, Convert the list of empty dirs. found into Json file. <br>
     */
    void startCleaning() {

        if (!srcDir.isDirectory()) {
            String msg = "Invalid SrcDir found...";
            throw new NullPointerException(msg);
        }

        MyDirVisitor myDirVisitor = new MyDirVisitor(shouldDeleteImmediately);

        try {
            Files.walkFileTree(Paths.get(srcDir.getAbsolutePath()), myDirVisitor);
        } catch (IOException e) {
            System.out.println("IOException [01]");
            e.printStackTrace();
        }

        System.out.println("Total Empty Dirs : " + myDirVisitor.emptyDirList.size());

        String jsonFileName = "JSON-" + srcDir.getName() + "-" + System.currentTimeMillis() + ".json";
        myDirVisitor.convertListToJson(myDirVisitor.emptyDirList, srcDir.getParentFile(), jsonFileName);

    }

    static class MyDirVisitor implements FileVisitor<Path> {

        private boolean shouldDeleteImmediately;
        private Map<String, Boolean> dirHasFileMap;
        private List<File> emptyDirList;

        /**
         * @param shouldDeleteImmediately true - delete any empty dirs. encountered immediately.
         *                                false - skip the deletion process.
         */
        MyDirVisitor(boolean shouldDeleteImmediately) {
            this.shouldDeleteImmediately = shouldDeleteImmediately;
            this.dirHasFileMap = new HashMap<>();
            this.emptyDirList = new ArrayList<>();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            dirHasFileMap.put(dir.toAbsolutePath().toString(), false);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                /*
                    Time Stamp: 7th November 2K19, 08:16 PM..!!
                    Update dirHasFileMap for file's Parents to the Root level with value true
                    Note: Optimization Required to reduce redundant traversal up-to Root dir.
                    Using additional Map to check before putting into dHFM
                 */

            updateDirHasFileMap(file);
            return FileVisitResult.CONTINUE;

        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            updateDirHasFileMap(file);
            System.out.println("Error with File : " + file.toAbsolutePath() +
                    "\nException : " + exc);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

            //  Skip current dir if dirHasFileMap doesn't have value as true for its abs. path
            if (!dirHasFileMap.get(dir.toAbsolutePath().toString())) {

                emptyDirList.add(dir.toFile());

                if (shouldDeleteImmediately) {
                    if (!Files.deleteIfExists(dir))
                        System.out.println("Failed to Delete Dir : " + dir.toAbsolutePath());

                }
            }
            return FileVisitResult.CONTINUE;

        }

        /**
         * This method updates dirHasFileMap by setting value as true for all the
         * parent directories upto root dir. for the current file.
         *
         * @param file File path whose boolean value is required to be Updated in dirHasFileMap
         */
        private void updateDirHasFileMap(Path file) {

            File parentDir = file.toFile().getParentFile();

            do {
                dirHasFileMap.put(parentDir.getAbsolutePath(), true);
                parentDir = parentDir.getParentFile();
            } while (parentDir != null);

        }

        /**
         * This method converts the given list into json using Gson library
         * and saves it to the given targetFile as .json file. <br>
         * <p>
         * Name for the saved json file is in following format: <br>
         * jsonFileName = JSON-targetFileName-currentTimeMillis.json
         *
         * @param list         List collection that needs to be converted into Json
         * @param targetFile   destination where the Json file will be saved
         * @param jsonFileName name for the target Json file including ".json" extension
         */
        private void convertListToJson(List<File> list, File targetFile, String jsonFileName) {

            //  Convert list<File> to dirsPathList<String>
            List<String> dirsPathList = new ArrayList<>();
            list.forEach(file -> dirsPathList.add(file.getAbsolutePath()));

            //  Convert each file's absolute path from dirsPathList into json
            String json = new Gson().toJson(dirsPathList);

            //  Separate Each Entry with a new line
            json = json.replaceAll("\"},", "\"},\n");

            //  Save json (string) to .json file i.e. targetFile
            File jsonFile = new File(targetFile.getParent(), jsonFileName);
            System.out.println("Writing into File : " + jsonFile.getAbsolutePath());
            KOHFilesUtil.writeStrToFile(json, jsonFile);

        }

    }

}
