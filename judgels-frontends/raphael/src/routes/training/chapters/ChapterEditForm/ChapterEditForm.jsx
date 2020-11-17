import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from '../../../../components/forms/validations';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';

export interface ChapterEditFormData {
  name: string;
}

export interface ChapterEditFormProps extends InjectedFormProps<ChapterEditFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ChapterEditForm = (props: ChapterEditFormProps) => {
  const nameField: any = {
    name: 'name',
    label: 'Name',
    validate: [Required],
    autoFocus: true,
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...nameField} />
    </>
  );
  const submitButton = <Button type="submit" text="Update" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ChapterEditFormData>({
  form: 'chapter-edit',
  touchOnBlur: false,
  enableReinitialize: true,
})(ChapterEditForm);
