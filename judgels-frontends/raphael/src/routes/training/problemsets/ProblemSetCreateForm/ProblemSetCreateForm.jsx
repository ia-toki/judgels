import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { Required, Slug } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { FormDateInput } from '../../../../components/forms/FormDateInput/FormDateInput';

function ProblemSetCreateForm({ handleSubmit, submitting, renderFormComponents }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: [Required, Slug],
    autoFocus: true,
  };

  const nameField = {
    name: 'name',
    label: 'Name',
    validate: [Required],
  };

  const archiveSlugField = {
    name: 'archiveSlug',
    label: 'Archive slug',
    validate: [Required],
  };

  const descriptionField = {
    name: 'description',
    label: 'Description',
    rows: 5,
  };

  const contestTimeField = {
    name: 'contestTime',
    label: 'Contest time',
    validate: [Required],
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...slugField} />
      <Field component={FormTextInput} {...nameField} />
      <Field component={FormTextInput} {...archiveSlugField} />
      <Field component={FormTextArea} {...descriptionField} />
      <Field component={FormDateInput} {...contestTimeField} />
    </>
  );
  const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />;

  return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
}

export default reduxForm({
  form: 'problem-set-create',
  touchOnBlur: false,
})(ProblemSetCreateForm);
