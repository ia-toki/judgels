import { Navigate, useLocation } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import ResendActivation from '../ResendActivation/ResendActivation';

export default function NeedActivationPage() {
  const email = useLocation({ select: location => location.state.email });

  if (!email) {
    return <Navigate to="/" replace />;
  }

  return (
    <SingleColumnLayout>
      <Card title="Activation required">
        <p>Your account has not been activated.</p>
        <p data-key="instruction">
          A confirmation email has been sent to&nbsp;
          <strong>{email}</strong> with instruction to activate your account.
        </p>
        <p>Please check your inbox/spam.</p>
        <ResendActivation email={email} />
      </Card>
    </SingleColumnLayout>
  );
}
