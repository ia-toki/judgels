package judgels.gabriel.api;

import java.util.HashMap;
import java.util.Map;

public class Verdicts {
    private static final Map<String, Verdict> verdictsByCode;

    static {
        verdictsByCode = new HashMap<>();
        for (Verdict verdict : Verdict.values()) {
            verdictsByCode.put(verdict.getCode(), verdict);
        }
    }

    private Verdicts() {}

    public static Verdict fromCode(String code) {
        return verdictsByCode.get(code);
    }
}
