package org.iatoki.judgels.jophiel;

import org.iatoki.judgels.jophiel.avatar.BaseAvatarCacheService;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.jid.BaseJidCacheService;
import play.mvc.Result;

public abstract class AbstractBaseJophielClientController extends AbstractBaseJophielController {

    protected final BaseJidCacheService jidCacheService;
    protected final BaseAvatarCacheService avatarCacheService;

    protected AbstractBaseJophielClientController(BaseJidCacheService jidCacheService, BaseAvatarCacheService avatarCacheService) {
        this.jidCacheService = jidCacheService;
        this.avatarCacheService = avatarCacheService;
    }

    protected void updateUserJidCache() {
        if (getCurrentUserJid() != null) {
            jidCacheService.putDisplayName(IdentityUtils.getUserJid(), getCurrentUsername(), getCurrentUserJid(), getCurrentUserIpAddress());
        }
    }

    protected void updateUserAvatarCache() {
        if (getCurrentUserJid() != null) {
            avatarCacheService.putImageUrl(getCurrentUserJid(), getCurrentUserAvatarUrl(), getCurrentUserJid(), getCurrentUserIpAddress());
        }
    }

    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        return super.renderTemplate(template);
    }
}
