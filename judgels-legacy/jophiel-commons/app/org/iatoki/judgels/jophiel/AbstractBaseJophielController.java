package org.iatoki.judgels.jophiel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.api.jophiel.JophielUser;
import org.iatoki.judgels.play.AbstractJudgelsController;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractBaseJophielController extends AbstractJudgelsController {

    protected final String getCurrentUserSessionVersion() {
        return "1";
    }

    protected final boolean isCurrentUserSessionValid() {
        if (!session().containsKey("version") || !session().get("version").equals(getCurrentUserSessionVersion())) {
            session().clear();
            return false;
        }

        if (request().method().equals("POST")) {
            return true;
        }

        try {
            if (!session().containsKey("expirationTime") || !(System.currentTimeMillis() < Long.parseLong(session().get("expirationTime")))) {
                session().clear();
                return false;
            }
        } catch (NumberFormatException e) {
            session().clear();
            return false;
        }

        return true;
    }

    protected String getCurrentUserJid() {
        return session("userJid");
    }

    protected List<String> getDefaultUserRoles() {
        return ImmutableList.of("user");
    }

    protected List<String> getCurrentUserRoles() {
        if (!session().containsKey("role")) {
            return ImmutableList.of();
        }
        return Lists.newArrayList(session("role").split(","));
    }

    protected void setCurrentUserRoles(List<String> roles) {
        session().put("role", StringUtils.join(roles, ","));
    }

    protected boolean currentUserHasRole(String role) {
        if (!session().containsKey("role")) {
            return false;
        }

        return Arrays.asList(session().get("role").split(",")).contains(role);
    }

    protected void backUpCurrentUserSession() {
        session().put("realUserJid", session().get("userJid"));
        session().put("realName", session().get("name"));
        session().put("realUsername", session().get("username"));
        session().put("realRole", session().get("role"));
        session().put("realAvatar", session().get("avatar"));
    }

    protected void setCurrentUserSession(JophielUser jophielUser, List<String> roles) {
        session().put("userJid", jophielUser.getJid());
        session().put("name", jophielUser.getName());
        session().put("username", jophielUser.getUsername());
        setCurrentUserRoles(roles);
        session().put("avatar", jophielUser.getProfilePictureUrl());
    }

    protected void restoreBackedUpUserSession() {
        session().put("userJid", session().get("realUserJid"));
        session().remove("realUserJid");
        session().put("name", session().get("realName"));
        session().remove("realName");
        session().put("username", session().get("realUsername"));
        session().remove("realUsername");
        session().put("role", session().get("realRole"));
        session().remove("realRole");
        session().put("avatar", session().get("realAvatar"));
        session().remove("realAvatar");
    }

    protected boolean backedUpOrCurrentUserHasRole(String role) {
        if (session().containsKey("realRole")) {
            return Arrays.asList(session().get("realRole").split(",")).contains(role);
        } else {
            return currentUserHasRole(role);
        }
    }

    protected String getBackedUpOrCurrentUserJid() {
        if (hasViewPoint()) {
            return session().get("realUserJid");
        } else {
            return getCurrentUserJid();
        }
    }

    protected boolean isCurrentUserAdmin() {
        return currentUserHasRole("admin");
    }

    protected boolean isCurrentUserGuest() {
        String role = session("role");
        return role == null || role.startsWith("guest");
    }

    protected boolean isBackedUpOrCurrentUserAdmin() {
        return backedUpOrCurrentUserHasRole("admin");
    }

    protected boolean hasViewPoint() {
        return session().containsKey("viewpoint");
    }

    protected String getViewPoint() {
        return session("viewpoint");
    }

    protected void setViewPointInSession(String userJid) {
        session("viewpoint", userJid);
    }

    protected void removeViewPoint() {
        session().remove("viewpoint");
    }

    protected String getCurrentUserAvatarUrl() {
        return session("avatar");
    }

    protected String getCurrentUsername() {
        return session("username");
    }

    protected String getCurrentUserRealName() {
        return session("name");
    }
}
