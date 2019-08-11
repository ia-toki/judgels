import * as React from 'react';
import { RouteComponentProps, Redirect } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import ResendActivationEmailButton from '../../components/ResendActivationEmailButton/ResendActivationEmailButton';

export default class NeedActivationPage extends React.PureComponent<RouteComponentProps<{}>> {
  render() {
    const email = this.props.history.location.state && this.props.history.location.state.email;

    if (!email) {
      return <Redirect to={{ pathname: '/' }} />;
    }

    return (
      <SingleColumnLayout>
        <Card title="Activation required" className="card-need-activation">
          <p>Your account has not been activated</p>
          <p data-key="instruction">
            A confirmation email has been sent to&nbsp;
            <strong>{email}</strong> with instruction to activate your account.
          </p>
          <p>Please check your inbox/spam.</p>
          <ResendActivationEmailButton email={email} />
        </Card>
      </SingleColumnLayout>
    );
  }
}
