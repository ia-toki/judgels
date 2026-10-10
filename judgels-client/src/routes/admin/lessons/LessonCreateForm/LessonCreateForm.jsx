import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormSelect2 } from '../../../../components/forms/FormSelect2/FormSelect2';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { worldLanguageNamesMap } from '../../../../modules/api/worldLanguage';
import { withSubmissionError } from '../../../../modules/form/submissionError';

const initialValues = {
  initialLanguage: 'en-US',
};

export default function LessonCreateForm({ onSubmit, renderFormComponents }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
    autoFocus: true,
  };

  const additionalNoteField = {
    name: 'additionalNote',
    label: 'Additional note',
    rows: 5,
  };

  const initialLanguageField = {
    name: 'initialLanguage',
    label: 'Initial language',
    optionValues: Object.keys(worldLanguageNamesMap),
    optionNamesMap: worldLanguageNamesMap,
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...slugField} />
      <Field component={FormTextArea} {...additionalNoteField} />
      <Field component={FormSelect2} {...initialLanguageField} />
    </>
  );

  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
