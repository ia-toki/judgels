import { Button, Intent } from '@blueprintjs/core';
import { Form } from 'react-final-form';

export default function PasswordForm({ onSubmit }) {
  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Button type="submit" text="Request to reset password" intent={Intent.PRIMARY} loading={submitting} />
        </form>
      )}
    </Form>
  );
}
