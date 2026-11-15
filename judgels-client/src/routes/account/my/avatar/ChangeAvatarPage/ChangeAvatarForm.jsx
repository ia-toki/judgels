import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTableFileInput } from '../../../../../components/forms/FormTableFileInput/FormTableFileInput';
import {
  HasImageExtension,
  MaxFileSize100KB,
  Required,
  composeValidators,
} from '../../../../../components/forms/validations';

export default function ChangeAvatarForm({ onSubmit }) {
  const field = {
    name: 'file',
    validate: composeValidators(Required, MaxFileSize100KB, HasImageExtension),
  };

  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <Field component={FormTableFileInput} {...field} />
          <Button type="submit" text="Upload" intent={Intent.PRIMARY} loading={submitting} />
        </form>
      )}
    </Form>
  );
}
