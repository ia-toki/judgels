import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { ContestStyle } from 'modules/api/uriel/contest';
import { ActionButtons } from 'components/ActionButtons/ActionButtons';
import { Required, Slug } from 'components/forms/validations';
import { FormTableTextInput } from 'components/forms/FormTableTextInput/FormTableTextInput';
import { FormTableSelect2 } from 'components/forms/FormTableSelect2/FormTableSelect2';
import { FormTableDateInput } from 'components/forms/FormTableDateInput/FormTableDateInput';

export interface ContestEditGeneralFormData {
  slug: string;
  name: string;
  style: string;
  beginTime: Date;
  duration: string;
}

interface ContestEditGeneralFormProps extends InjectedFormProps<ContestEditGeneralFormData> {
  onCancel: () => void;
}

const ContestEditGeneralForm = (props: ContestEditGeneralFormProps) => {
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

  const styleField: any = {
    name: 'style',
    label: 'Style',
    validate: [Required],
    optionValues: [ContestStyle.ICPC, ContestStyle.IOI],
    optionNamesMap: { [ContestStyle.ICPC]: ContestStyle.ICPC, [ContestStyle.IOI]: ContestStyle.IOI },
  };

  const beginTimeField: any = {
    name: 'beginTime',
    label: 'Begin time',
    validate: [Required],
  };

  const durationField: any = {
    name: 'duration',
    label: 'Duration',
    validate: [Required],
    inputHelper: 'Example: 10d 5h 30m 15s',
  };

  return (
    <form onSubmit={props.handleSubmit}>
      <table className="bp3-html-table bp3-html-table-striped">
        <tbody>
          <Field component={FormTableTextInput} {...slugField} />
          <Field component={FormTableTextInput} {...nameField} />
          <Field component={FormTableSelect2} {...styleField} />
          <Field component={FormTableDateInput} {...beginTimeField} />
          <Field component={FormTableTextInput} {...durationField} />
        </tbody>
      </table>
      <hr />
      <ActionButtons>
        <Button text="Cancel" disabled={props.submitting} onClick={props.onCancel} />
        <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={props.submitting} />
      </ActionButtons>
    </form>
  );
};

export default reduxForm<ContestEditGeneralFormData>({
  form: 'contest-edit',
  touchOnBlur: false,
})(ContestEditGeneralForm);
