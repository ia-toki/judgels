import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { requestToResetPasswordMutationOptions } from '../../../../modules/queries/accountPassword';
import { getUser } from '../../../../modules/session';
import PasswordForm from '../PasswordForm/PasswordForm';

export default function PasswordPage() {
  const [submitted, setSubmitted] = useState(false);

  const resetPasswordMutation = useMutation(requestToResetPasswordMutationOptions());

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
    return <PasswordForm onSubmit={resetPassword} />;
  };

  return <ContentCard title="Reset password">{renderContent()}</ContentCard>;
}
