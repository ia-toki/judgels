import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm } from 'redux-form';

import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { Required, Max100Lines } from '../../../../../../components/forms/validations';

function ContestProblemEditForm({ handleSubmit, submitting, renderFormComponents, validator }) {
  const problemsField = {
    name: 'problems',
    label: 'Problems',
    labelHelper: '(one problem per line, max 100 problems)',
    rows: 10,
    isCode: true,
    validate: [Required, Max100Lines, validator],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...problemsField} />;
  const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />;

  return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
}

export default reduxForm({
  form: 'contest-problem-edit',
  touchOnBlur: false,
})(ContestProblemEditForm);
