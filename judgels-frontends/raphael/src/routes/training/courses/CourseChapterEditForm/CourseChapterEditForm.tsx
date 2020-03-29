import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Max100Lines } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface CourseChapterEditFormData {
  chapters: string;
}

export interface CourseChapterEditFormProps extends InjectedFormProps<CourseChapterEditFormData> {
  validator: (value: string) => any;
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const CourseChapterEditForm = (props: CourseChapterEditFormProps) => {
  const chaptersField: any = {
    name: 'chapters',
    label: 'Chapters',
    labelHelper: '(one chapter per line, max 100 chapters)',
    rows: 10,
    isCode: true,
    validate: [Required, Max100Lines, props.validator],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...chaptersField} />;
  const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<CourseChapterEditFormData>({
  form: 'course-chapters-edit',
  touchOnBlur: false,
  enableReinitialize: true,
})(CourseChapterEditForm);
