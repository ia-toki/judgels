import * as React from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import ForgotPasswordForm from '../ForgotPasswordForm/ForgotPasswordForm';
import * as forgotPasswordActions from '../modules/forgotPasswordActions';

export class ForgotPasswordPage extends React.Component {
  state = {
    submitted: false,
  };

  render() {
    let content;

    if (this.state.submitted) {
      content = (
        <div>
          <p data-key="instruction">An email has been sent to your email with instruction to reset your password.</p>
          <p>Please check your inbox/spam.</p>
        </div>
      );
    } else {
      content = <ForgotPasswordForm onSubmit={this.onForgetPassword} />;
    }
    return (
      <SingleColumnLayout>
        <Card title="Forgot password">{content}</Card>
      </SingleColumnLayout>
    );
  }

  onForgetPassword = async data => {
    await this.props.onForgetPassword(data.email);
    this.setState({ submitted: true });
  };
}

const mapDispatchToProps = {
  onForgetPassword: forgotPasswordActions.requestToResetPassword,
};

export default connect(undefined, mapDispatchToProps)(ForgotPasswordPage);
