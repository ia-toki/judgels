package judgels.catalog.problem.programming.grading;

class TCFrameFile implements Comparable<TCFrameFile> {
    final String filename;
    final int tgNo;
    final int tcNo;

    TCFrameFile(String filename, int tgNo, int tcNo) {
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
