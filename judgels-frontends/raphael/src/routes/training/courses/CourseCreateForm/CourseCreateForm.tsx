import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Slug } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface CourseCreateFormData {
  slug: string;
  name: string;
  description?: string;
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

  const nameField: any = {
    name: 'name',
    label: 'Name',
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
      <Field component={FormTextArea} {...descriptionField} />
    </>
  );
  const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<CourseCreateFormData>({
  form: 'course-create',
  touchOnBlur: false,
})(CourseCreateForm);
