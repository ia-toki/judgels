import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTableFileInput } from '../../../../../../components/forms/FormTableFileInput/FormTableFileInput';
import { MaxFileSize20MB, Required } from '../../../../../../components/forms/validations';

import './ContestFileUploadForm.css';

export interface ContestFileUploadFormData {
  file: File;
}

class ContestFileUploadForm extends React.PureComponent<InjectedFormProps<ContestFileUploadFormData>> {
  render() {
    const field: any = {
      name: 'file',
      placeholder: 'Upload new file...',
      validate: [Required, MaxFileSize20MB],
    };

    return (
      <form onSubmit={this.props.handleSubmit} className="contest-file-upload-form">
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
          loading={this.props.submitting}
        />
      </form>
    );
  }
}

export default reduxForm<ContestFileUploadFormData>({
  form: 'contest-file-upload',
  touchOnBlur: false,
})(ContestFileUploadForm);
