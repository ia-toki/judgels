import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { ActionButtons } from 'components/ActionButtons/ActionButtons';
import { Required, Slug } from 'components/forms/validations';
import { FormTableTextInput } from 'components/forms/FormTableTextInput/FormTableTextInput';
import { ContestData } from 'modules/api/uriel/contest';

interface ContestEditGeneralFormData extends ContestData {}

interface ContestEditGeneralFormProps extends InjectedFormProps<ContestEditGeneralFormData> {
  onCancel: () => void;
}

const ContestEditGeneralForm = (props: ContestEditGeneralFormProps) => {
  const jidField: any = {
    name: 'jid',
    label: 'JID',
    disabled: true,
  };

  const slugField: any = {
    name: 'slug',
    label: 'Slug',
    validate: [Required, Slug],
  };

  const nameField: any = {
    name: 'name',
    label: 'Name',
    validate: [Required],
  };

  return (
    <form onSubmit={props.handleSubmit}>
      <table className="bp3-html-table bp3-html-table-striped">
        <tbody>
          <Field component={FormTableTextInput} {...jidField} />
          <Field component={FormTableTextInput} {...slugField} />
          <Field component={FormTableTextInput} {...nameField} />
        </tbody>
      </table>
      <hr />
      <ActionButtons>
        <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />
        <Button text="Cancel" disabled={props.submitting} onClick={props.onCancel} />
      </ActionButtons>
    </form>
  );
};

export default reduxForm<ContestEditGeneralFormData>({
  form: 'contest-edit',
})(ContestEditGeneralForm);
