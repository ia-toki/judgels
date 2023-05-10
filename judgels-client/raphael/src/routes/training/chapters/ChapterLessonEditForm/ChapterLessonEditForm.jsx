import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { composeValidators, Required, Max100Lines } from '../../../../components/forms/validations';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';

export default function ChapterLessonEditForm({ onSubmit, initialValues, validator, renderFormComponents }) {
  const lessonsField = {
    name: 'lessons',
    label: 'Lessons',
    labelHelper: '(one lesson per line, max 100 lessons)',
    rows: 10,
    isCode: true,
    validate: composeValidators(Required, Max100Lines, validator),
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...lessonsField} />;

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
