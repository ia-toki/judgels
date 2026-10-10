import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTableTextArea } from '../../../../components/forms/FormTableTextArea/FormTableTextArea';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';
import { problemTagsQueryOptions } from '../../../../modules/queries/problemTag';
import { ProblemTopicTagsInput } from '../ProblemTopicTagsInput/ProblemTopicTagsInput';

const keyStyles = { width: '250px' };

const slugField = {
  keyStyles,
  name: 'slug',
  label: 'Slug',
  validate: composeValidators(Required, Slug),
};

const additionalNoteField = {
  keyStyles,
  name: 'additionalNote',
  label: 'Additional note',
};

const setterInputHelpers = {
  developerUsernames: 'Comma-separated usernames. Fill only if different from writers.',
};

export default function ProblemGeneralEditForm({ onSubmit, initialValues, setterFields, onCancel }) {
  const {
    data: { topicTags },
  } = useSuspenseQuery(problemTagsQueryOptions());

  const topicTagsField = {
    keyStyles,
    name: 'topicTags',
    label: 'Tags',
    topicTags,
  };

  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <HTMLTable striped>
              <tbody>
                <Field component={FormTableTextInput} {...slugField} />
                <Field component={FormTableTextArea} {...additionalNoteField} />
                {setterFields.map(({ name, title }) => (
                  <Field
                    key={name}
                    component={FormTableTextInput}
                    keyStyles={keyStyles}
                    name={name}
                    label={title}
                    inputHelper={setterInputHelpers[name] || 'Comma-separated usernames.'}
                  />
                ))}
                <Field component={ProblemTopicTagsInput} {...topicTagsField} />
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
