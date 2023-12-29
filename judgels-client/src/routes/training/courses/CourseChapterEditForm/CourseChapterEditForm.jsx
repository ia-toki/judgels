import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Max100Lines, Required, composeValidators } from '../../../../components/forms/validations';

export default function CourseChapterEditForm({ onSubmit, initialValues, validator, renderFormComponents }) {
  const chaptersField = {
    name: 'chapters',
    label: 'Chapters',
    labelHelper: '(one chapter per line, max 100 chapters)',
    rows: 10,
    isCode: true,
    validate: composeValidators(Required, Max100Lines, validator),
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...chaptersField} />;

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
