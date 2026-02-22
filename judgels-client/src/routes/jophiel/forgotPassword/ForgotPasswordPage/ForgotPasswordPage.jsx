import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import { requestResetPasswordMutationOptions } from '../../../../modules/queries/userAccount';
import ForgotPasswordForm from '../ForgotPasswordForm/ForgotPasswordForm';

export default function ForgotPasswordPage() {
  const [submitted, setSubmitted] = useState(false);

  const resetPasswordMutation = useMutation(requestResetPasswordMutationOptions);

  const onForgetPassword = async data => {
    await resetPasswordMutation.mutateAsync(data.email);
    setSubmitted(true);
  };

  let content;
  if (submitted) {
    content = (
      <div>
        <p data-key="instruction">An email has been sent to your email with instruction to reset your password.</p>
        <p>Please check your inbox/spam.</p>
      </div>
    );
  } else {
    content = <ForgotPasswordForm onSubmit={onForgetPassword} />;
  }

  return (
    <SingleColumnLayout>
      <Card title="Forgot password">{content}</Card>
    </SingleColumnLayout>
  );
}
