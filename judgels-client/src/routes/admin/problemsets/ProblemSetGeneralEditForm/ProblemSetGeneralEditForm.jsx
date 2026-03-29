import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTableDateInput } from '../../../../components/forms/FormTableDateInput/FormTableDateInput';
import { FormTableRichTextArea } from '../../../../components/forms/FormTableRichTextArea/FormTableRichTextArea';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

const keyStyles = { width: '250px' };

const slugField = {
  keyStyles,
  name: 'slug',
  label: 'Slug',
  validate: composeValidators(Required, Slug),
};

const nameField = {
  keyStyles,
  name: 'name',
  label: 'Name',
  validate: Required,
};

const archiveSlugField = {
  keyStyles,
  name: 'archiveSlug',
  label: 'Archive slug',
  validate: Required,
};

const contestTimeField = {
  keyStyles,
  name: 'contestTime',
  label: 'Contest time',
  validate: Required,
};

const descriptionField = {
  keyStyles,
  name: 'description',
  label: 'Description',
  rows: 15,
};

export default function ProblemSetGeneralEditForm({ onSubmit, initialValues, onCancel }) {
  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <HTMLTable striped>
              <tbody>
                <Field component={FormTableTextInput} {...slugField} />
                <Field component={FormTableTextInput} {...nameField} />
                <Field component={FormTableTextInput} {...archiveSlugField} />
                <Field component={FormTableDateInput} {...contestTimeField} />
                <Field component={FormTableRichTextArea} {...descriptionField} />
              </tbody>
            </HTMLTable>
            <ActionButtons justifyContent="end">
              <Button text="Cancel" disabled={submitting} onClick={onCancel} />
              <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
            </ActionButtons>
          </form>
        </Flex>
      )}
    </Form>
  );
}
