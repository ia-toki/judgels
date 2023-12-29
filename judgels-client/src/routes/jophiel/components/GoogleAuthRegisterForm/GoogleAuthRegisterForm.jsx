import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Username, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

const usernameField = {
  name: 'username',
  label: 'Username',
  validate: composeValidators(Required, Username),
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
