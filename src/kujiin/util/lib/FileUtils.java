package kujiin.util.lib;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static boolean validaudiofile(File file) {
        return file.getName().endsWith(".mp3") || file.getName().endsWith(".aac") || file.getName().endsWith(".wav")
                || file.getName().endsWith(".aif") || file.getName().endsWith(".aiff") || file.getName().endsWith("m4a");
    }

    public static String getFileContents(File file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file);
        } catch (IOException ignored) {return "";}
    }

    public static boolean writeFileContents(File file, String contents) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file, contents);
            return true;
        } catch (IOException ignored) {return false;}
    }

}
