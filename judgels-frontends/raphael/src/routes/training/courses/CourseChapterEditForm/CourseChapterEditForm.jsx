import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { Required, Max100Lines } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

function CourseChapterEditForm({ handleSubmit, submitting, validator, renderFormComponents }) {
  const chaptersField = {
    name: 'chapters',
    label: 'Chapters',
    labelHelper: '(one chapter per line, max 100 chapters)',
    rows: 10,
    isCode: true,
    validate: [Required, Max100Lines, validator],
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...chaptersField} />;
  const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />;

  return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
}

export default reduxForm({
  form: 'course-chapters-edit',
  touchOnBlur: false,
  enableReinitialize: true,
})(CourseChapterEditForm);
