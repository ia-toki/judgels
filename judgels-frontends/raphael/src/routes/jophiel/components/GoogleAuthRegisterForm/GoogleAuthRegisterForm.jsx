import { Button, Intent } from '@blueprintjs/core';
import { Form, Field } from 'react-final-form';

import { withSubmissionError } from '../../../../modules/form/submissionError';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required } from '../../../../components/forms/validations';

const usernameField = {
  name: 'username',
  label: 'Username',
  validate: Required,
  autoFocus: true,
};

export default function GoogleAuthRegisterForm({ onSubmit, renderFormComponents }) {
  const fields = <Field component={FormTextInput} {...usernameField} />;

  return (
    <Form onSubmit={withSubmissionError(onSubmit)}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Register" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
