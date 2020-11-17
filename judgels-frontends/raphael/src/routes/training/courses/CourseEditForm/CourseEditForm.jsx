import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Slug } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface CourseEditFormData {
  slug: string;
  name: string;
  description: string;
}

export interface CourseEditFormProps extends InjectedFormProps<CourseEditFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const CourseEditForm = (props: CourseEditFormProps) => {
  const slugField: any = {
    name: 'slug',
    label: 'Slug',
    validate: [Required, Slug],
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
  const submitButton = <Button type="submit" text="Update" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<CourseEditFormData>({
  form: 'course-edit',
  touchOnBlur: false,
  enableReinitialize: true,
})(CourseEditForm);
