import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ChangeAvatarPanel } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';
import { avatarActions as injectedAvatarActions } from '../../../../modules/avatarActions';

export function createChangeAvatarPage(avatarActions) {
  const mapDispatchToProps = {
    onDropAccepted: (files: File[]) => avatarActions.updateAvatar(files[0]),
    onDropRejected: (files: File[]) => avatarActions.rejectAvatar(files[0]),
    onRenderAvatar: avatarActions.renderAvatar,
    onDeleteAvatar: avatarActions.deleteAvatar,
  };

  return connect(undefined, mapDispatchToProps)(ChangeAvatarPanel);
}

export default withBreadcrumb('Change avatar')(createChangeAvatarPage(injectedAvatarActions));
