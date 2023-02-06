package org.iatoki.judgels.sandalphon.problem.programming.grading;

public final class TokilibFile implements Comparable<TokilibFile> {

    public String filename;
    public int batchNo;
    public int tcNo;

    public TokilibFile(String filename, int batchNo, int tcNo) {
        this.filename = filename;
        this.batchNo = batchNo;
        this.tcNo = tcNo;
    }

    @Override
    public int compareTo(TokilibFile o) {
        if (!filename.equals(o.filename)) {
            return filename.compareTo(o.filename);
        }

        if (batchNo != o.batchNo) {
            return batchNo - o.batchNo;
        }

        if (tcNo != o.tcNo) {
            return tcNo - o.tcNo;
        }

        return 0;
    }
}
