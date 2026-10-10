import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { Card } from '../../../../../components/Card/Card';
import { requestToResetPasswordMutationOptions } from '../../../../../modules/queries/userAccount';
import { getUser } from '../../../../../modules/session';
import ResetPasswordForm from '../ResetPasswordForm/ResetPasswordForm';

export default function ResetPasswordPage() {
  const [submitted, setSubmitted] = useState(false);

  const resetPasswordMutation = useMutation(requestToResetPasswordMutationOptions);

  const resetPassword = () => {
    const email = getUser().email;
    resetPasswordMutation.mutate(email);
    setSubmitted(true);
  };

  const renderContent = () => {
    if (submitted) {
      return (
        <div>
          <p data-key="instruction">An email has been sent to your email with instruction to reset your password.</p>
          <p>Please check your inbox/spam.</p>
        </div>
      );
    }
    return <ResetPasswordForm onSubmit={resetPassword} />;
  };

  return <Card title="Reset password">{renderContent()}</Card>;
}
