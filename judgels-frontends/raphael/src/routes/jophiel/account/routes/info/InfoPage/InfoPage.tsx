import * as React from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { UserInfo } from '../../../../../../modules/api/jophiel/userInfo';
import { InfoPanel } from '../../../../panels/info/InfoPanel/InfoPanel';
import { AppState } from '../../../../../../modules/store';
import { selectUserJid } from '../../../../../../modules/session/sessionSelectors';
import { infoActions as injectedInfoActions } from '../../../../modules/infoActions';

interface InfoPageProps {
  onGetInfo: () => Promise<UserInfo>;
  onUpdateInfo: (info: UserInfo) => Promise<void>;
}

interface InfoPageState {
  info?: UserInfo;
}

class InfoPage extends React.PureComponent<InfoPageProps, InfoPageState> {
  state: InfoPageState = {};

  async componentDidMount() {
    await this.refreshInfo();
  }

  render() {
    if (!this.state.info) {
      return <LoadingState />;
    }
    return <InfoPanel info={this.state.info} onUpdateInfo={this.onUpdateInfo} />;
  }

  private refreshInfo = async () => {
    const info = await this.props.onGetInfo();
    this.setState({ info });
  };

  private onUpdateInfo = async (info: UserInfo) => {
    await this.props.onUpdateInfo(info);
    await this.refreshInfo();
  };
}

export function createInfoPage(infoActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
  });
  const mapDispatchToProps = {
    onGetInfo: infoActions.getInfo,
    onUpdateInfo: infoActions.updateInfo,
  };
  const mergeProps = (stateProps, dispatchProps) => ({
    onGetInfo: () => dispatchProps.onGetInfo(stateProps.userJid),
    onUpdateInfo: (info: UserInfo) => dispatchProps.onUpdateInfo(stateProps.userJid, info),
  });
  return connect<any>(mapStateToProps, mapDispatchToProps, mergeProps)(InfoPage);
}

export default withBreadcrumb('Info')(createInfoPage(injectedInfoActions));
