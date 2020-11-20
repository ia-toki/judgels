import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm } from 'redux-form';

import { Required, Max100Lines } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

function ChapterLessonEditForm({ handleSubmit, submitting, validator, renderFormComponents }) {
  const lessonsField = {
    name: 'lessons',
    label: 'Lessons',
    labelHelper: '(one lesson per line, max 100 lessons)',
    rows: 10,
    isCode: true,
    validate: [Required, Max100Lines, validator],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...lessonsField} />;
  const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />;

  return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
}

export default reduxForm({
  form: 'chapter-lessons-edit',
  touchOnBlur: false,
  enableReinitialize: true,
})(ChapterLessonEditForm);
