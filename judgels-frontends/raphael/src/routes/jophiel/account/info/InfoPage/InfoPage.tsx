import * as React from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { User } from '../../../../../modules/api/jophiel/user';
import { UserInfo } from '../../../../../modules/api/jophiel/userInfo';
import { AppState } from '../../../../../modules/store';
import { selectUserJid } from '../../../../../modules/session/sessionSelectors';

import { InfoPanel } from '../../../panels/info/InfoPanel/InfoPanel';
import { infoActions as injectedInfoActions } from '../../../modules/infoActions';
import { userActions as injectedUserActions } from '../../../modules/userActions';

interface InfoPageProps {
  onGetUser: () => Promise<User>;
  onGetInfo: () => Promise<UserInfo>;
  onUpdateInfo: (info: UserInfo) => Promise<void>;
}

interface InfoPageState {
  user?: User;
  info?: UserInfo;
}

class InfoPage extends React.PureComponent<InfoPageProps, InfoPageState> {
  state: InfoPageState = {};

  async componentDidMount() {
    await this.refreshInfo();
  }

  render() {
    const { user, info } = this.state;
    if (!user || !info) {
      return <LoadingState />;
    }
    return <InfoPanel email={user.email} info={info} onUpdateInfo={this.onUpdateInfo} />;
  }

  private refreshInfo = async () => {
    const [user, info] = await Promise.all([this.props.onGetUser(), this.props.onGetInfo()]);
    this.setState({ user, info });
  };

  private onUpdateInfo = async (info: UserInfo) => {
    await this.props.onUpdateInfo(info);
    await this.refreshInfo();
  };
}

export function createInfoPage(userActions, infoActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
  });
  const mapDispatchToProps = {
    onGetUser: userActions.getUser,
    onGetInfo: infoActions.getInfo,
    onUpdateInfo: infoActions.updateInfo,
  };
  const mergeProps = (stateProps, dispatchProps) => ({
    onGetUser: () => dispatchProps.onGetUser(stateProps.userJid),
    onGetInfo: () => dispatchProps.onGetInfo(stateProps.userJid),
    onUpdateInfo: (info: UserInfo) => dispatchProps.onUpdateInfo(stateProps.userJid, info),
  });
  return connect<any>(mapStateToProps, mapDispatchToProps, mergeProps)(InfoPage);
}

export default withBreadcrumb('Info')(createInfoPage(injectedUserActions, injectedInfoActions));
