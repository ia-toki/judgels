import { Button, Intent, Switch } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import FormAceEditor from '../../../../components/forms/FormAceEditor/FormAceEditor';
import { FormRichTextArea } from '../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { ProblemStatementManual } from '../ProblemStatementManual/ProblemStatementManual';

const textField = {
  name: 'text',
  label: 'Text',
  rows: 25,
  permissive: true,
};

export default function ProblemEditorialEditForm({ onSubmit, initialValues, onCancel }) {
  const [isSourceMode, setIsSourceMode] = useState(false);

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <Flex justifyContent="space-between" alignItems="center">
              <Switch
                label="Source mode"
                checked={isSourceMode}
                onChange={e => setIsSourceMode(e.target.checked)}
                style={{ marginBottom: 0 }}
              />
              <ProblemStatementManual />
            </Flex>
            {isSourceMode ? (
              <Field component={FormAceEditor} name={textField.name} />
            ) : (
              <Field component={FormRichTextArea} {...textField} />
            )}
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
