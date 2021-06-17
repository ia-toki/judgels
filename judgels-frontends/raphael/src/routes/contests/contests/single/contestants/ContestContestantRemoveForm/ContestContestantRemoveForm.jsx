import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { composeValidators, Required, Max1000Lines } from '../../../../../../components/forms/validations';
import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';

export default function ContestContestantRemoveForm({ onSubmit, renderFormComponents }) {
  const usernamesField = {
    name: 'usernames',
    label: 'Usernames',
    labelHelper: '(one username per line, max 1000 users)',
    rows: 20,
    isCode: true,
    validate: composeValidators(Required, Max1000Lines),
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...usernamesField} />;

  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Remove" intent={Intent.DANGER} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
