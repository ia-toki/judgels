import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Slug } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface ProblemSetCreateFormData {
  slug: string;
  name: string;
  archiveSlug: string;
  description?: string;
}

export interface ProblemSetCreateFormProps extends InjectedFormProps<ProblemSetCreateFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ProblemSetCreateForm = (props: ProblemSetCreateFormProps) => {
  const slugField: any = {
    name: 'slug',
    label: 'Slug',
    validate: [Required, Slug],
    autoFocus: true,
  };

  const nameField: any = {
    name: 'name',
    label: 'Name',
    validate: [Required],
  };

  const archiveSlugField: any = {
    name: 'archiveSlug',
    label: 'Archive slug',
    validate: [Required],
  };

  const descriptionField: any = {
    name: 'description',
    label: 'Description',
    rows: 5,
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...slugField} />
      <Field component={FormTextInput} {...nameField} />
      <Field component={FormTextInput} {...archiveSlugField} />
      <Field component={FormTextArea} {...descriptionField} />
    </>
  );
  const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ProblemSetCreateFormData>({
  form: 'problem-set-create',
  touchOnBlur: false,
})(ProblemSetCreateForm);
