package org.iatoki.judgels.play.forms;

import java.util.List;

public class ListTableSelectionForm {

    public boolean selectAll;

    public List<String> selectJids;

    public boolean getSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public List<String> getSelectJids() {
        return selectJids;
    }

    public void setSelectJids(List<String> selectJids) {
        this.selectJids = selectJids;
    }
}
