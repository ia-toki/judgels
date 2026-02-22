import { useMutation } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { resetPasswordMutationOptions } from '../../../../modules/queries/userAccount';
import ResetPasswordForm from '../ResetPasswordForm/ResetPasswordForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ResetPasswordPage() {
  const { emailCode } = useParams({ strict: false });
  const navigate = useNavigate();

  const resetMutation = useMutation(resetPasswordMutationOptions);

  const resetPassword = async data => {
    await resetMutation.mutateAsync(
      { emailCode, newPassword: data.password },
      {
        onSuccess: () => {
          toastActions.showSuccessToast('Password has been reset.');
          navigate({ to: '/login' });
        },
      }
    );
  };

  return (
    <SingleColumnLayout>
      <Card title="Reset password">
        <ResetPasswordForm onSubmit={resetPassword} />
      </Card>
    </SingleColumnLayout>
  );
}
