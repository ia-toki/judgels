import { Card } from '../../../../components/Card/Card';
import { SingleColumnLayout } from '../../../../components/SingleColumnLayout/SingleColumnLayout';

export default function RegisteredPage() {
  setTimeout(() => {
    window.location.href = '/login';
  }, 3000);

  return (
    <SingleColumnLayout>
      <Card title="Registration successful">
        <p>Your account is now active.</p>
        <p>
          You will be redirected to the <a href="/">home page</a> in a moment.
        </p>
      </Card>
    </SingleColumnLayout>
  );
}
