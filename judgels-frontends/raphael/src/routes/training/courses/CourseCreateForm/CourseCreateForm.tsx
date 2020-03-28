import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Slug } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';

export interface CourseCreateFormData {
  slug: string;
}

export interface CourseCreateFormProps extends InjectedFormProps<CourseCreateFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const CourseCreateForm = (props: CourseCreateFormProps) => {
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

export default reduxForm<CourseCreateFormData>({
  form: 'course-create',
  touchOnBlur: false,
})(CourseCreateForm);
