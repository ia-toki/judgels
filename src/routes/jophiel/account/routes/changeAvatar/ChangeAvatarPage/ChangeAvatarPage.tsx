import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ChangeAvatarPanel } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';
import { avatarActions as injectedAvatarActions } from '../modules/avatarActions';

export function createChangeAvatarPage(avatarActions) {
  const mapDispatchToProps = {
    onDropAccepted: (files: File[]) => avatarActions.change(files[0]),
    onDropRejected: (files: File[]) => avatarActions.reject(files[0]),
    onRenderAvatar: avatarActions.render,
    onRemoveAvatar: avatarActions.remove,
  };

  return connect(undefined, mapDispatchToProps)(ChangeAvatarPanel);
}

export default withBreadcrumb('Change avatar')(createChangeAvatarPage(injectedAvatarActions));
