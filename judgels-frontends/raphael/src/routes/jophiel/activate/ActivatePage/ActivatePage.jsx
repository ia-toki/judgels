import { Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { Card } from '../../../../components/Card/Card';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import * as activateActions from '../modules/activateActions';

interface ActivatePageProps {
  isLoading: boolean;
}

const ActivatePage = (props: ActivatePageProps) => {
  const content = !props.isLoading && (
    <Card title="Activation successful" className="card-activate">
      <p>Your account has been activated.</p>

      <HorizontalDivider />

      <ButtonLink to="/login" intent={Intent.PRIMARY}>
        Log in
      </ButtonLink>
    </Card>
  );

  return <SingleColumnLayout>{content}</SingleColumnLayout>;
};

interface ActivatePageContainerProps extends RouteComponentProps<{ emailCode: string }> {
  onActivateUser: (emailCode: string) => Promise<void>;
}

interface ActivatePageContainerState {
  isFetching: boolean;
}

class ActivatePageContainer extends React.PureComponent<ActivatePageContainerProps, ActivatePageContainerState> {
  state: ActivatePageContainerState = {
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
