import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';
import { Required, Slug } from '../../../../../../components/forms/validations';
import { FormTableTextInput } from '../../../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { FormTableSelect2 } from '../../../../../../components/forms/FormTableSelect2/FormTableSelect2';
import { FormTableDateInput } from '../../../../../../components/forms/FormTableDateInput/FormTableDateInput';

function ContestEditGeneralForm({ handleSubmit, submitting, onCancel }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: [Required, Slug],
  };

  const nameField = {
    name: 'name',
    label: 'Name',
    validate: [Required],
  };

  const styleField = {
    name: 'style',
    label: 'Style',
    validate: [Required],
    optionValues: [ContestStyle.ICPC, ContestStyle.IOI, ContestStyle.GCJ, ContestStyle.Bundle],
    optionNamesMap: {
      [ContestStyle.ICPC]: ContestStyle.ICPC,
      [ContestStyle.IOI]: ContestStyle.IOI,
      [ContestStyle.GCJ]: ContestStyle.GCJ,
      [ContestStyle.Bundle]: 'Bundle',
    },
  };

  const beginTimeField = {
    name: 'beginTime',
    label: 'Begin time',
    validate: [Required],
  };

  const durationField = {
    name: 'duration',
    label: 'Duration',
    validate: [Required],
    inputHelper: 'Example: 10d 5h 30m 15s',
  };

  return (
    <form onSubmit={handleSubmit}>
      <HTMLTable striped>
        <tbody>
          <Field component={FormTableTextInput} {...slugField} />
          <Field component={FormTableTextInput} {...nameField} />
          <Field component={FormTableSelect2} {...styleField} />
          <Field component={FormTableDateInput} {...beginTimeField} />
          <Field component={FormTableTextInput} {...durationField} />
        </tbody>
      </HTMLTable>
      <hr />
      <ActionButtons>
        <Button text="Cancel" disabled={submitting} onClick={onCancel} />
        <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
      </ActionButtons>
    </form>
  );
}

export default reduxForm({
  form: 'contest-edit',
  touchOnBlur: false,
})(ContestEditGeneralForm);
