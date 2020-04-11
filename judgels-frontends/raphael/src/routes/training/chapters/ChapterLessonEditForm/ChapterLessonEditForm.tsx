import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required, Max100Lines } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export interface ChapterLessonEditFormData {
  lessons: string;
}

export interface ChapterLessonEditFormProps extends InjectedFormProps<ChapterLessonEditFormData> {
  validator: (value: string) => any;
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ChapterLessonEditForm = (props: ChapterLessonEditFormProps) => {
  const lessonsField: any = {
    name: 'lessons',
    label: 'Lessons',
    labelHelper: '(one lesson per line, max 100 lessons)',
    rows: 10,
    isCode: true,
    validate: [Required, Max100Lines, props.validator],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...lessonsField} />;
  const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ChapterLessonEditFormData>({
  form: 'chapter-lessons-edit',
  touchOnBlur: false,
  enableReinitialize: true,
})(ChapterLessonEditForm);
