import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm } from 'redux-form';

import { FormTableFileInput } from '../../../../../../components/forms/FormTableFileInput/FormTableFileInput';
import { MaxFileSize20MB, Required } from '../../../../../../components/forms/validations';

import './ContestFileUploadForm.css';

function ContestFileUploadForm({ handleSubmit, submitting }) {
  const field = {
    name: 'file',
    placeholder: 'Upload new file...',
    validate: [Required, MaxFileSize20MB],
  };

  return (
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
  );
}

export default reduxForm({
  form: 'contest-file-upload',
  touchOnBlur: false,
})(ContestFileUploadForm);
