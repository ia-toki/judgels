import { Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { Card } from '../../../../components/Card/Card';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import * as activateActions from '../modules/activateActions';

function ActivatePage({ isLoading }) {
  const content = !isLoading && (
    <Card title="Activation successful" className="card-activate">
      <p>Your account has been activated.</p>

      <HorizontalDivider />

      <ButtonLink to="/login" intent={Intent.PRIMARY}>
        Log in
      </ButtonLink>
    </Card>
  );

  return <SingleColumnLayout>{content}</SingleColumnLayout>;
}

class ActivatePageContainer extends React.Component {
  state = {
    isFetching: true,
  };

  async componentDidMount() {
    await this.props.onActivateUser(this.props.match.params.emailCode);
    this.setState({
      isFetching: false,
    });
  }

  render() {
    return <ActivatePage isLoading={this.state.isFetching} />;
  }
}

const mapDispatchToProps = {
  onActivateUser: activateActions.activateUser,
};

export default withRouter(connect(undefined, mapDispatchToProps)(ActivatePageContainer));
