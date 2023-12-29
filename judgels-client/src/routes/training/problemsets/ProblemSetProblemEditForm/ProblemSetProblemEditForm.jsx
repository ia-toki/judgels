import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Max100Lines, Required, composeValidators } from '../../../../components/forms/validations';

export default function ProblemSetProblemEditForm({ onSubmit, initialValues, validator, renderFormComponents }) {
  const problemsField = {
    name: 'problems',
    label: 'Problems',
    labelHelper: '(one problem per line, max 100 problems)',
    rows: 10,
    isCode: true,
    validate: composeValidators(Required, Max100Lines, validator),
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...problemsField} />;

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
