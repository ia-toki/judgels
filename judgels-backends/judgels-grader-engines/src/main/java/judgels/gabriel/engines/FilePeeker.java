package judgels.gabriel.engines;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FilePeeker {
    private FilePeeker() {}

    public static String peekInputOutput(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[1024];
            int bytesRead = bis.read(buffer, 0, 1024);
            if (bytesRead <= 0) {
                return "";
            }

            String str = new String(buffer, 0, bytesRead);
            String[] lines = str.split("\n");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length && i < 10; i++) {
                String line = lines[i];
                if (line.length() > 128) {
                    line = line.substring(0, 128) + "... (truncated)";
                }
                sb.append(line).append('\n');
            }

            if (lines.length > 10) {
                sb.append("... (truncated)\n");
            }
            return sb.toString();
        }
    }
}
