package judgels.sandalphon.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

public final class WorldLanguageRegistry {

    private static final WorldLanguageRegistry INSTANCE = new WorldLanguageRegistry();

    private Map<String, String> registry;

    @SuppressWarnings("checkstyle:MethodLength")
    private WorldLanguageRegistry() {
        registry = Maps.newLinkedHashMap();
        registry.put("af-ZA", "Afrikaans (af-ZA)");
        registry.put("ak-GH", "Akan (ak-GH)");
        registry.put("am-ET", "Amharic (am-ET)");
        registry.put("ar-001", "Arabic (ar-001)");
        registry.put("as-IN", "Assamese (as-IN)");
        registry.put("az-Cyrl", "Azerbaijani (az-Cyrl)");
        registry.put("be-BY", "Belarusian (be-BY)");
        registry.put("bg-BG", "Bulgarian (bg-BG)");
        registry.put("bm-Latn", "Bambara (bm-Latn)");
        registry.put("bn-BD", "Bengali (bn-BD)");
        registry.put("bo-IN", "Tibetan (bo-IN)");
        registry.put("br-FR", "Breton (br-FR)");
        registry.put("bs-Cyrl", "Bosnian (bs-Cyrl)");
        registry.put("ca-ES", "Catalan (ca-ES)");
        registry.put("cs-CZ", "Czech (cs-CZ)");
        registry.put("cy-GB", "Welsh (cy-GB)");
        registry.put("da-DK", "Danish (da-DK)");
        registry.put("de-DE", "German (de-DE)");
        registry.put("dz-BT", "Dzongkha (dz-BT)");
        registry.put("el-GR", "Greek (el-GR)");
        registry.put("en-US", "English (en-US)");
        registry.put("es-ES", "Spanish (es-ES)");
        registry.put("et-EE", "Estonian (et-EE)");
        registry.put("eu-ES", "Basque (eu-ES)");
        registry.put("fa-IR", "Persian (fa-IR)");
        registry.put("fi-FI", "Finnish (fi-FI)");
        registry.put("fo-FO", "Faroese (fo-FO)");
        registry.put("fr-FR", "French (fr-FR)");
        registry.put("ga-IE", "Irish (ga-IE)");
        registry.put("gd-GB", "Scottish Gaelic (gd-GB)");
        registry.put("gl-ES", "Galician (gl-ES)");
        registry.put("gu-IN", "Gujarati (gu-IN)");
        registry.put("gv-IM", "Manx (gv-IM)");
        registry.put("he-IL", "Hebrew (he-IL)");
        registry.put("hi-IN", "Hindi (hi-IN)");
        registry.put("hr-HR", "Croatian (hr-HR)");
        registry.put("hu-HU", "Hungarian (hu-HU)");
        registry.put("hy-AM", "Armenian (hy-AM)");
        registry.put("id-ID", "Indonesian (id-ID)");
        registry.put("ig-NG", "Igbo (ig-NG)");
        registry.put("ii-CN", "Sichuan Yi (ii-CN)");
        registry.put("is-IS", "Icelandic (is-IS)");
        registry.put("it-IT", "Italian (it-IT)");
        registry.put("ja-JP", "Japanese (ja-JP)");
        registry.put("ka-GE", "Georgian (ka-GE)");
        registry.put("ki-KE", "Kikuyu (ki-KE)");
        registry.put("kk-Cyrl", "Kazakh (kk-Cyrl)");
        registry.put("kl-GL", "Kalaallisut (kl-GL)");
        registry.put("km-KH", "Khmer (km-KH)");
        registry.put("kn-IN", "Kannada (kn-IN)");
        registry.put("ko-KR", "Korean (ko-KR)");
        registry.put("ks-Arab", "Kashmiri (ks-Arab)");
        registry.put("kw-GB", "Cornish (kw-GB)");
        registry.put("ky-Cyrl", "Kyrgyz (ky-Cyrl)");
        registry.put("lb-LU", "Luxembourgish (lb-LU)");
        registry.put("lg-UG", "Ganda (lg-UG)");
        registry.put("lo-LA", "Lao (lo-LA)");
        registry.put("lt-LT", "Lithuanian (lt-LT)");
        registry.put("lu-CD", "Luba-Katanga (lu-CD)");
        registry.put("lv-LV", "Latvian (lv-LV)");
        registry.put("mg-MG", "Malagasy (mg-MG)");
        registry.put("mk-MK", "Macedonian (mk-MK)");
        registry.put("ml-IN", "Malayalam (ml-IN)");
        registry.put("mn-Cyrl", "Mongolian (mn-Cyrl)");
        registry.put("mr-IN", "Marathi (mr-IN)");
        registry.put("ms-Latn-MY", "Malay (ms-Latn-MY)");
        registry.put("mt-MT", "Maltese (mt-MT)");
        registry.put("my-MM", "Burmese (my-MM)");
        registry.put("nb-NO", "Norwegian Bokmål (nb-NO)");
        registry.put("nd-ZW", "North Ndebele (nd-ZW)");
        registry.put("ne-NP", "Nepali (ne-NP)");
        registry.put("nl-NL", "Dutch (nl-NL)");
        registry.put("or-IN", "Oriya (or-IN)");
        registry.put("pa-Guru-IN", "Punjabi (pa-Guru-IN)");
        registry.put("pl-PL", "Polish (pl-PL)");
        registry.put("ps-AF", "Pashto (ps-AF)");
        registry.put("pt-PT", "Portuguese (pt-PT)");
        registry.put("rm-CH", "Romansh (rm-CH)");
        registry.put("rn-BI", "Rundi (rn-BI)");
        registry.put("ro-RO", "Romanian (ro-RO)");
        registry.put("ru-RU", "Russian (ru-RU)");
        registry.put("rw-RW", "Kinyarwanda (rw-RW)");
        registry.put("sg-CF", "Sango (sg-CF)");
        registry.put("si-LK", "Sinhala (si-LK)");
        registry.put("sk-SK", "Slovak (sk-SK)");
        registry.put("sl-SI", "Slovenian (sl-SI)");
        registry.put("sn-ZW", "Shona (sn-ZW)");
        registry.put("sq-AL", "Albanian (sq-AL)");
        registry.put("sr-Cyrl", "Serbian (sr-Cyrl)");
        registry.put("sr-Latn", "Serbian (sr-Latn)");
        registry.put("sv-SE", "Swedish (sv-SE)");
        registry.put("sw-KE", "Swahili (sw-KE)");
        registry.put("ta-IN", "Tamil (ta-IN)");
        registry.put("te-IN", "Telugu (te-IN)");
        registry.put("th-TH", "Thai (th-TH)");
        registry.put("ti-ER", "Tigrinya (ti-ER)");
        registry.put("to-TO", "Tongan (to-TO)");
        registry.put("tr-TR", "Turkish (tr-TR)");
        registry.put("ug-Arab", "Uyghur (ug-Arab)");
        registry.put("uk-UA", "Ukrainian (uk-UA)");
        registry.put("ur-IN", "Urdu (ur-IN)");
        registry.put("uz-Cyrl", "Uzbek (uz-Cyrl)");
        registry.put("vi-VN", "Vietnamese (vi-VN)");
        registry.put("yi-001", "Yiddish (yi-001)");
        registry.put("zh-Hans", "Chinese (zh-Hans)");
        registry.put("zh-Hans-CN", "Chinese (zh-Hans-CN)");
        registry.put("zh-Hans-HK", "Chinese (zh-Hans-HK)");
        registry.put("zh-Hans-MO", "Chinese (zh-Hans-MO)");
        registry.put("zh-Hans-SG", "Chinese (zh-Hans-SG)");
        registry.put("zh-Hant", "Chinese (zh-Hant)");
        registry.put("zh-Hant-HK", "Chinese (zh-Hant-HK)");
        registry.put("zh-Hant-MO", "Chinese (zh-Hant-MO)");
        registry.put("zh-Hant-TW", "Chinese (zh-Hant-TW)");
        registry.put("zu-ZA", "Zulu (zu-ZA)");
    }

    public static WorldLanguageRegistry getInstance() {
        return INSTANCE;
    }

    public Map<String, String> getLanguages() {
        return ImmutableMap.copyOf(registry);
    }

    public String getDisplayLanguage(String languageCode) {
        return registry.get(languageCode);
    }
}
