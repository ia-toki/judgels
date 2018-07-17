import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { AppState } from '../../../../../../modules/store';
import { ChangeAvatarPanel } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';
import { avatarActions as injectedAvatarActions } from '../modules/avatarActions';

export function createChangeAvatarContainer(avatarActions) {
  const mapStateToProps = (state: AppState) => ({
    avatarUrl: state.session.user && state.session.user.avatarUrl,
  });

  const mapDispatchToProps = dispatch => ({
    onDropAccepted: (files: File[]) => dispatch(avatarActions.change(files[0])),
    onDropRejected: (files: File[]) => dispatch(avatarActions.reject(files[0])),
    onRemoveAvatar: () => dispatch(avatarActions.remove()),
  });

  return connect(mapStateToProps, mapDispatchToProps)(ChangeAvatarPanel);
}

const AvatarContainer = withBreadcrumb('Change avatar')(createChangeAvatarContainer(injectedAvatarActions));
export default AvatarContainer;
