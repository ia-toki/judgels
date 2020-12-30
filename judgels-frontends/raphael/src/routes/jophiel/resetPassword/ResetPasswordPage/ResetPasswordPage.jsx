import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import ResetPasswordForm from '../ResetPasswordForm/ResetPasswordForm';
import * as resetPasswordActions from '../modules/resetPasswordActions';

export const ResetPasswordPage = ({ onResetPassword }) => (
  <SingleColumnLayout>
    <Card title="Reset password">
      <ResetPasswordForm onSubmit={onResetPassword} />
    </Card>
  </SingleColumnLayout>
);

const ResetPasswordPageContainer = ({ match, onResetPassword }) => {
  const resetPassword = data => onResetPassword(match.params.emailCode, data);
  return <ResetPasswordPage onResetPassword={resetPassword} />;
};

const mapDispatchToProps = {
  onResetPassword: (emailCode, data) => resetPasswordActions.resetPassword(emailCode, data.password),
};

export default withRouter(connect(undefined, mapDispatchToProps)(ResetPasswordPageContainer));
