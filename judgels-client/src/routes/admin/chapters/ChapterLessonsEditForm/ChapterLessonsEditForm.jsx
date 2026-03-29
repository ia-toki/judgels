import { Button, Callout, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Max100Lines, Required, composeValidators } from '../../../../components/forms/validations';

export default function ChapterLessonsEditForm({ onSubmit, initialValues, validator, onCancel }) {
  const lessonsField = {
    name: 'lessons',
    label: 'Lessons',
    labelHelper: '(one lesson per line, max 100 lessons)',
    rows: 10,
    isCode: true,
    validate: composeValidators(Required, Max100Lines, validator),
    autoFocus: true,
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Flex flexDirection="column" gap={2}>
            <Field component={FormTextArea} {...lessonsField} />
            <Callout icon={null}>
              <p>
                <strong>Format:</strong> <code>alias,slug</code>
              </p>
              <p>
                <strong>Example:</strong>
              </p>
              <pre>{'A,hello\nB,tree'}</pre>
            </Callout>
            <ActionButtons justifyContent="end">
              <Button text="Cancel" disabled={submitting} onClick={onCancel} />
              <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
            </ActionButtons>
          </Flex>
        </form>
      )}
    </Form>
  );
}
