import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { withSubmissionError } from '../../../../modules/form/submissionError';
import { problemTagsQueryOptions } from '../../../../modules/queries/problemTag';
import { ProblemTopicTagsInput } from '../ProblemTopicTagsInput/ProblemTopicTagsInput';

const keyStyles = { width: '250px' };

export default function ProblemTopicTagsEditForm({ onSubmit, initialValues, onCancel }) {
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
