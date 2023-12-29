import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { FormTableFileInput } from '../../../../../../components/forms/FormTableFileInput/FormTableFileInput';
import { MaxFileSize20MB, Required, composeValidators } from '../../../../../../components/forms/validations';

import './ContestFileUploadForm.scss';

export default function ContestFileUploadForm({ onSubmit }) {
  const field = {
    name: 'file',
    placeholder: 'Upload new file...',
    validate: composeValidators(Required, MaxFileSize20MB),
  };

  return (
    <Form onSubmit={onSubmit}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit} className="contest-file-upload-form">
          <table className="contest-file-upload-form__table">
            <tbody>
              <Field component={FormTableFileInput} {...field} />
            </tbody>
          </table>
          <Button
            className="contest-file-upload-form__button"
            type="submit"
            text="Upload"
            intent={Intent.PRIMARY}
            loading={submitting}
          />
        </form>
      )}
    </Form>
  );
}
