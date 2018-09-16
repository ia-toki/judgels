import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { ActionButtons } from 'components/ActionButtons/ActionButtons';
import { FormTextArea } from 'components/forms/FormTextArea/FormTextArea';

export interface ContestEditDescriptionFormData {
  description: string;
}

interface ContestEditDescriptionFormProps extends InjectedFormProps<ContestEditDescriptionFormData> {
  onCancel: () => void;
}

const ContestEditDescriptionForm = (props: ContestEditDescriptionFormProps) => {
  const descriptionField: any = {
    name: 'description',
    validate: [],
  };

  return (
    <form onSubmit={props.handleSubmit}>
      <Field component={FormTextArea} {...descriptionField} />
      <hr />
      <ActionButtons>
        <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />
        <Button text="Cancel" disabled={props.submitting} onClick={props.onCancel} />
      </ActionButtons>
    </form>
  );
};

export default reduxForm<ContestEditDescriptionFormData>({
  form: 'contest-description-edit',
})(ContestEditDescriptionForm);
