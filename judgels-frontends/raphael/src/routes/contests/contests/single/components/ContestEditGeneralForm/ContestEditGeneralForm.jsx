import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { withSubmissionError } from '../../../../../../modules/form/submissionError';
import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';
import { composeValidators, Required, Slug } from '../../../../../../components/forms/validations';
import { FormTableTextInput } from '../../../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { FormTableSelect2 } from '../../../../../../components/forms/FormTableSelect2/FormTableSelect2';
import { FormTableDateInput } from '../../../../../../components/forms/FormTableDateInput/FormTableDateInput';

export default function ContestEditGeneralForm({ onSubmit, initialValues, onCancel }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
  };

  const nameField = {
    name: 'name',
    label: 'Name',
    validate: Required,
  };

  const styleField = {
    name: 'style',
    label: 'Style',
    validate: Required,
    optionValues: [ContestStyle.TROC, ContestStyle.ICPC, ContestStyle.IOI, ContestStyle.GCJ, ContestStyle.Bundle],
    optionNamesMap: {
      [ContestStyle.TROC]: ContestStyle.TROC,
      [ContestStyle.ICPC]: ContestStyle.ICPC,
      [ContestStyle.IOI]: ContestStyle.IOI,
      [ContestStyle.GCJ]: ContestStyle.GCJ,
      [ContestStyle.Bundle]: 'Bundle',
    },
  };

  const beginTimeField = {
    name: 'beginTime',
    label: 'Begin time',
    validate: Required,
  };

  const durationField = {
    name: 'duration',
    label: 'Duration',
    validate: Required,
    inputHelper: 'Example: 10d 5h 30m 15s',
  };

  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <HTMLTable striped>
            <tbody>
              <Field component={FormTableTextInput} {...slugField} />
              <Field component={FormTableTextInput} {...nameField} />
              <Field component={FormTableSelect2} {...styleField} />
              <Field component={FormTableDateInput} {...beginTimeField} />
              <Field component={FormTableTextInput} {...durationField} />
            </tbody>
          </HTMLTable>
          <hr />
          <ActionButtons>
            <Button text="Cancel" disabled={submitting} onClick={onCancel} />
            <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
