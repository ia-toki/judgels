import * as React from 'react';
import { connect } from 'react-redux';

import ForgotPasswordForm, { ForgotPasswordFormData } from '../ForgotPasswordForm/ForgotPasswordForm';
import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { forgotPasswordActions as injectedForgotPasswordActions } from '../modules/forgotPasswordActions';

export interface ForgotPasswordProps {
  onForgetPassword: (data: ForgotPasswordFormData) => Promise<void>;
}

interface ForgotPasswordState {
  submitted: boolean;
}

export class ForgotPassword extends React.Component<ForgotPasswordProps, ForgotPasswordState> {
  state: ForgotPasswordState = { submitted: false };

  render() {
    let content: JSX.Element;

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

  private onForgetPassword = async (data: ForgotPasswordFormData) => {
    await this.props.onForgetPassword(data);
    this.setState({ submitted: true });
  };
}

export function createForgotPasswordContainer(forgotPasswordActions) {
  const mapDispatchToProps = dispatch => ({
    onForgetPassword: (data: ForgotPasswordFormData) => dispatch(forgotPasswordActions.requestToReset(data.email)),
  });

  return connect(undefined, mapDispatchToProps)(ForgotPassword);
}

const ForgotPasswordContainer = createForgotPasswordContainer(injectedForgotPasswordActions);
export default ForgotPasswordContainer;
