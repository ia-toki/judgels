import { useDispatch } from 'react-redux';
import { useParams } from 'react-router';

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

export default function ResetPasswordPageContainer() {
  const { emailCode } = useParams();
  const dispatch = useDispatch();

  const resetPassword = data => dispatch(resetPasswordActions.resetPassword(emailCode, data.password));

  return <ResetPasswordPage onResetPassword={resetPassword} />;
}
