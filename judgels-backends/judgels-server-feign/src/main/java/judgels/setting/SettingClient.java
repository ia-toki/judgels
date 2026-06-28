package judgels.setting;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;

public interface SettingClient {
    @RequestLine("GET /api/v2/settings")
    @Headers("Authorization: Bearer {token}")
    Settings getSettings(@Param("token") String token);

    @RequestLine("PUT /api/v2/settings")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Settings updateSettings(@Param("token") String token, SettingUpdateData data);
}
