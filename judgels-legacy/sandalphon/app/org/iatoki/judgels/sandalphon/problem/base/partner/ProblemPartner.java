package org.iatoki.judgels.sandalphon.problem.base.partner;

import com.google.gson.Gson;

public final class ProblemPartner {

    private final long id;
    private final String problemJid;
    private final String partnerJid;
    private final String baseConfig;
    private final String childConfig;

    public ProblemPartner(long id, String problemJid, String partnerJid, String baseConfig, String childConfig) {
        this.id = id;
        this.problemJid = problemJid;
        this.partnerJid = partnerJid;
        this.baseConfig = baseConfig;
        this.childConfig = childConfig;
    }

    public long getId() {
        return id;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getPartnerJid() {
        return partnerJid;
    }

    public ProblemPartnerConfig getBaseConfig() {
        return new Gson().fromJson(baseConfig, ProblemPartnerConfig.class);
    }

    public <T extends ProblemPartnerChildConfig> T getChildConfig(Class<T> clazz) {
        return new Gson().fromJson(childConfig, clazz);
    }
}
