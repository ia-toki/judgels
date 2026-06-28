package judgels.api;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.setting.SettingUpdateData;
import judgels.setting.SettingClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class SettingApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final SettingClient settingClient = createClient(SettingClient.class);

    @Test
    void get_settings() {
        assertPermitted(getSettings(adminToken));
        assertForbidden(getSettings(userToken));
    }

    @Test
    void update_settings() {
        assertPermitted(updateSettings(adminToken));
        assertForbidden(updateSettings(userToken));
    }

    private ThrowingCallable getSettings(String token) {
        return () -> settingClient.getSettings(token);
    }

    private ThrowingCallable updateSettings(String token) {
        return () -> settingClient.updateSettings(token, new SettingUpdateData.Builder().build());
    }
}
