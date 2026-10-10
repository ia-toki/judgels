import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormSelect2 } from '../../../../components/forms/FormSelect2/FormSelect2';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { gradingEngineNamesMap } from '../../../../modules/api/gradingEngine';
import { languageDisplayNamesMap, sortLanguagesByName } from '../../../../modules/api/language';
import { withSubmissionError } from '../../../../modules/form/submissionError';

const gradingEngineOptionNamesMap = { ...gradingEngineNamesMap, Bundle: 'Bundle' };

const initialValues = {
  gradingEngine: 'Batch',
  initialLanguage: 'en-US',
};

export default function ProblemCreateForm({ onSubmit, renderFormComponents }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
    autoFocus: true,
  };

  const gradingEngineField = {
    name: 'gradingEngine',
    label: 'Grading engine',
    optionValues: Object.keys(gradingEngineOptionNamesMap),
    optionNamesMap: gradingEngineOptionNamesMap,
  };

  const additionalNoteField = {
    name: 'additionalNote',
    label: 'Additional note',
    rows: 5,
  };

  const initialLanguageField = {
    name: 'initialLanguage',
    label: 'Initial language',
    optionValues: sortLanguagesByName(Object.keys(languageDisplayNamesMap)),
    optionNamesMap: languageDisplayNamesMap,
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...slugField} />
      <Field component={FormSelect2} {...gradingEngineField} />
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
