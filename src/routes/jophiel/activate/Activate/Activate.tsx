import { Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { Card } from '../../../../components/Card/Card';
import { HorizontalDivider } from '../../../../components/HorizontalDivider/HorizontalDivider';
import { activateActions as injectedActivateActions } from '../modules/activateActions';

interface ActivateProps {
  isLoading: boolean;
}

export const Activate = (props: ActivateProps) => {
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

interface ActivateContainerProps {
  match: {
    params: {
      emailCode: string;
    };
  };

  onActivate: (emailCode: string) => Promise<void>;
}

interface ActivateContainerState {
  isFetching: boolean;
}

class ActivateContainer extends React.Component<ActivateContainerProps, ActivateContainerState> {
  state: ActivateContainerState = {
    isFetching: true,
  };

  async componentDidMount() {
    await this.props.onActivate(this.props.match.params.emailCode);
    this.setState({
      isFetching: false,
    });
  }

  render() {
    return <Activate isLoading={this.state.isFetching} />;
  }
}

export function createActivateContainer(activateActions) {
  const mapDispatchToProps = dispatch => ({
    onActivate: (emailCode: string) => dispatch(activateActions.activate(emailCode)),
  });

  return withRouter<any>(connect(undefined, mapDispatchToProps)(ActivateContainer));
}

export default createActivateContainer(injectedActivateActions);
