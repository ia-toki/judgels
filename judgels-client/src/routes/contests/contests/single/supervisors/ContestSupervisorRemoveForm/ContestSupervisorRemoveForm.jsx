import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { Max100Lines, Required, composeValidators } from '../../../../../../components/forms/validations';

export default function ContestSupervisorRemoveForm({ onSubmit, renderFormComponents }) {
  const usernamesField = {
    name: 'usernames',
    label: 'Usernames',
    labelHelper: '(one username per line, max 100 users)',
    rows: 20,
    isCode: true,
    validate: composeValidators(Required, Max100Lines),
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
