import { useParams } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { callAction } from '../../../../modules/callAction';
import ResetPasswordForm from '../ResetPasswordForm/ResetPasswordForm';

import * as resetPasswordActions from '../modules/resetPasswordActions';

export const ResetPasswordPage = ({ onResetPassword }) => (
  <SingleColumnLayout>
    <Card title="Reset password">
      <ResetPasswordForm onSubmit={onResetPassword} />
    </Card>
  </SingleColumnLayout>
);

export default function ResetPasswordPageContainer() {
  const { emailCode } = useParams({ strict: false });

  const resetPassword = data => callAction(resetPasswordActions.resetPassword(emailCode, data.password));

  return <ResetPasswordPage onResetPassword={resetPassword} />;
}
