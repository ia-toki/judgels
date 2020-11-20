import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { selectUserJid } from '../../../../../modules/session/sessionSelectors';
import { ChangeAvatarPanel } from '../../../panels/avatar/ChangeAvatarPanel/ChangeAvatarPanel';
import * as avatarActions from '../../../modules/avatarActions';

const mapStateToProps = state => ({
  userJid: selectUserJid(state),
});
const mapDispatchToProps = {
  onDropAccepted: (userJid, files) => avatarActions.updateAvatar(userJid, files[0]),
  onDropRejected: files => avatarActions.rejectAvatar(files[0]),
  onAvatarExists: avatarActions.avatarExists,
  onRenderAvatar: avatarActions.renderAvatar,
  onDeleteAvatar: avatarActions.deleteAvatar,
};
const mergeProps = (stateProps, dispatchProps) => ({
  onDropAccepted: files => dispatchProps.onDropAccepted(stateProps.userJid, files),
  onDropRejected: dispatchProps.onDropRejected,
  onAvatarExists: () => dispatchProps.onAvatarExists(stateProps.userJid),
  onRenderAvatar: () => dispatchProps.onRenderAvatar(stateProps.userJid),
  onDeleteAvatar: () => dispatchProps.onDeleteAvatar(stateProps.userJid),
});
export default withBreadcrumb('Change avatar')(
  connect(mapStateToProps, mapDispatchToProps, mergeProps)(ChangeAvatarPanel)
);
