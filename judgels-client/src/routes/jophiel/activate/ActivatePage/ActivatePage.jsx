import { push } from 'connected-react-router';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';

import * as activateActions from '../modules/activateActions';

class ActivatePage extends Component {
  async componentDidMount() {
    await this.props.onActivateUser(this.props.match.params.emailCode);
    this.props.onPush('/registered?source=internal');
  }

  render() {
    return <SingleColumnLayout></SingleColumnLayout>;
  }
}

const mapDispatchToProps = {
  onActivateUser: activateActions.activateUser,
  onPush: push,
};

export default withRouter(connect(undefined, mapDispatchToProps)(ActivatePage));
