import { Navigate, useLocation } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';
import ResendActivationEmailButton from '../../components/ResendActivationEmailButton/ResendActivationEmailButton';

export default function NeedActivationPage() {
  const location = useLocation();
  const email = location.state && location.state.email;

  if (!email) {
    return <Navigate to="/" replace />;
  }

  return (
    <SingleColumnLayout>
      <Card title="Activation required" className="card-need-activation">
        <p>Your account has not been activated.</p>
        <p data-key="instruction">
          A confirmation email has been sent to&nbsp;
          <strong>{email}</strong> with instruction to activate your account.
        </p>
        <p>Please check your inbox/spam.</p>
        <ResendActivationEmailButton email={email} />
      </Card>
    </SingleColumnLayout>
  );
}
