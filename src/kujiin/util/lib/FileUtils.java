package kujiin.util.lib;

import java.io.File;

public class FileUtils {

    public static boolean validaudiofile(File file) {
        return file.getName().endsWith(".mp3") || file.getName().endsWith(".aac") || file.getName().endsWith(".wav")
                || file.getName().endsWith(".aif") || file.getName().endsWith(".aiff") || file.getName().endsWith("m4a");
    }
}
