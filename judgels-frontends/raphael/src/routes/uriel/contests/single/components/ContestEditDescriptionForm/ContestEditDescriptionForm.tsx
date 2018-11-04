import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { ActionButtons } from 'components/ActionButtons/ActionButtons';
import { FormRichTextArea } from 'components/forms/FormRichTextArea/FormRichTextArea';

export interface ContestEditDescriptionFormData {
  description: string;
}

interface ContestEditDescriptionFormProps extends InjectedFormProps<ContestEditDescriptionFormData> {
  onCancel: () => void;
}

const ContestEditDescriptionForm = (props: ContestEditDescriptionFormProps) => {
  const descriptionField: any = {
    name: 'description',
    rows: 19,
    validate: [],
  };

  return (
    <form className="contest-edit-dialog__content" onSubmit={props.handleSubmit}>
      <Field component={FormRichTextArea} {...descriptionField} />
      <hr />
      <ActionButtons>
        <Button text="Cancel" disabled={props.submitting} onClick={props.onCancel} />
        <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />
      </ActionButtons>
    </form>
  );
};

export default reduxForm<ContestEditDescriptionFormData>({
  form: 'contest-description-edit',
  touchOnBlur: false,
})(ContestEditDescriptionForm);
