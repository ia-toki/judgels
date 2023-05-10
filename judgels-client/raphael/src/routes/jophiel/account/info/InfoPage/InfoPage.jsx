import { Component } from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { selectUserJid } from '../../../../../modules/session/sessionSelectors';
import { InfoPanel } from '../../../panels/info/InfoPanel/InfoPanel';
import * as infoActions from '../../../modules/infoActions';
import * as userActions from '../../../../system/modules/userActions';

class InfoPage extends Component {
  state = {
    user: undefined,
    info: undefined,
  };

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

  refreshInfo = async () => {
    const [user, info] = await Promise.all([this.props.onGetUser(), this.props.onGetInfo()]);
    this.setState({ user, info });
  };

  onUpdateInfo = async info => {
    await this.props.onUpdateInfo(info);
    await this.refreshInfo();
  };
}

const mapStateToProps = state => ({
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
  onUpdateInfo: info => dispatchProps.onUpdateInfo(stateProps.userJid, info),
});
export default withBreadcrumb('Info')(connect(mapStateToProps, mapDispatchToProps, mergeProps)(InfoPage));
