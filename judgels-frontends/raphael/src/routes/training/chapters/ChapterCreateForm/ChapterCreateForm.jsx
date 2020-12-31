import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { Required } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';

function ChapterCreateForm({ handleSubmit, submitting, renderFormComponents }) {
  const nameField = {
    name: 'name',
    label: 'Name',
    validate: [Required],
    autoFocus: true,
  };

  const fields = <Field component={FormTextInput} {...nameField} />;
  const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />;

  return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
}

export default reduxForm({
  form: 'chapter-create',
  touchOnBlur: false,
})(ChapterCreateForm);
