import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Slug } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';

export interface ContestCreateFormData {
  slug: string;
}

export interface ContestCreateFormProps extends InjectedFormProps<ContestCreateFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ContestCreateForm = (props: ContestCreateFormProps) => {
  const slugField: any = {
    name: 'slug',
    label: 'Slug',
    validate: [Required, Slug],
    autoFocus: true,
  };

  const fields = <Field component={FormTextInput} {...slugField} />;
  const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ContestCreateFormData>({
  form: 'contest-create',
  touchOnBlur: false,
})(ContestCreateForm);
