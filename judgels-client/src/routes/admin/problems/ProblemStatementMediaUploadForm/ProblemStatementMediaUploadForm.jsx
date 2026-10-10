import { Button, Checkbox, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { FormTableFileInput } from '../../../../components/forms/FormTableFileInput/FormTableFileInput';
import { Required } from '../../../../components/forms/validations';

const fileField = {
  name: 'file',
  placeholder: 'Upload new file...',
  validate: Required,
};

export default function ProblemStatementMediaUploadForm({ onSubmit }) {
  const submit = async (data, form) => {
    await onSubmit(data);
    form.restart();
  };

  return (
    <Form onSubmit={submit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Flex gap={2} alignItems="center">
            <table style={{ width: '245px' }}>
              <tbody>
                <Field component={FormTableFileInput} {...fileField} />
              </tbody>
            </table>
            <Field name="isZip" type="checkbox">
              {({ input }) => <Checkbox {...input} label="Extract as zip" style={{ marginBottom: 0 }} />}
            </Field>
            <Button type="submit" text="Upload" intent={Intent.PRIMARY} loading={submitting} />
          </Flex>
        </form>
      )}
    </Form>
  );
}
