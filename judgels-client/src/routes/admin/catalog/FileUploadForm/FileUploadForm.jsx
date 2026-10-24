import { Button, Checkbox, FileInput, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

export default function FileUploadForm({ onSubmit }) {
  const submit = async (data, form) => {
    await onSubmit(data);
    form.restart();
  };

  return (
    <Form onSubmit={submit}>
      {({ handleSubmit, submitting, values, form }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form onSubmit={handleSubmit}>
            <Flex gap={2} alignItems="center">
              <Field name="file">
                {({ input: { value, onChange } }) => {
                  const changeFile = e => {
                    const file = e.target.files.length ? e.target.files[0] : undefined;
                    onChange(file);
                    form.change('isZip', !!file && file.name.toLowerCase().endsWith('.zip'));
                  };
                  return (
                    <FileInput inputProps={{ onChange: changeFile }} text={value ? value.name : 'Upload new file...'} />
                  );
                }}
              </Field>
              <Button
                type="submit"
                text="Upload"
                intent={Intent.PRIMARY}
                disabled={!values.file}
                loading={submitting}
              />
            </Flex>
            <Field name="isZip" type="checkbox">
              {({ input }) => <Checkbox {...input} label="Extract as zip" style={{ marginBottom: 0 }} />}
            </Field>
          </form>
        </Flex>
      )}
    </Form>
  );
}
