package judgels.michael.problem.programming.grading.config;

public class TCFrameFile implements Comparable<TCFrameFile> {
    public String filename;
    public int tgNo;
    public int tcNo;

    public TCFrameFile(String filename, int tgNo, int tcNo) {
        this.filename = filename;
        this.tgNo = tgNo;
        this.tcNo = tcNo;
    }

    @Override
    public int compareTo(TCFrameFile o) {
        if (!filename.equals(o.filename)) {
            return filename.compareTo(o.filename);
        }

        if (tgNo != o.tgNo) {
            return tgNo - o.tgNo;
        }

        if (tcNo != o.tcNo) {
            return tcNo - o.tcNo;
        }

        return 0;
    }
}
