import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import ResetPasswordForm from '../ResetPasswordForm/ResetPasswordForm';
import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import * as resetPasswordActions from '../modules/resetPasswordActions';

export class ResetPasswordPage extends Component {
  state = {
    submitted: false,
  };

  render() {
    return <Card title="Reset password">{this.renderContent()}</Card>;
  }

  renderContent = () => {
    if (this.state.submitted) {
      return (
        <div>
          <p data-key="instruction">An email has been sent to your email with instruction to reset your password.</p>
          <p>Please check your inbox/spam.</p>
        </div>
      );
    }
    return <ResetPasswordForm onSubmit={this.resetPassword} />;
  };

  resetPassword = async () => {
    await this.props.onResetPassword();
    this.setState({ submitted: true });
  };
}

const mapDispatchToProps = {
  onResetPassword: resetPasswordActions.requestToResetPassword,
};

export default withBreadcrumb('Change password')(connect(undefined, mapDispatchToProps)(ResetPasswordPage));
