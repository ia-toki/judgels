import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { AppState } from '../../../../../../modules/store';
import { ChangeAvatarPanel } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';
import { avatarActions as injectedAvatarActions } from '../modules/avatarActions';

export function createChangeAvatarPage(avatarActions) {
  const mapStateToProps = (state: AppState) => ({
    avatarUrl: state.session.user && state.session.user.avatarUrl,
  });

  const mapDispatchToProps = {
    onDropAccepted: (files: File[]) => avatarActions.change(files[0]),
    onDropRejected: (files: File[]) => avatarActions.reject(files[0]),
    onRemoveAvatar: avatarActions.remove,
  };

  return connect(mapStateToProps, mapDispatchToProps)(ChangeAvatarPanel);
}

export default withBreadcrumb('Change avatar')(createChangeAvatarPage(injectedAvatarActions));
