import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from '../../components/forms/validations';
import { FormSelect2 } from '../../components/forms/FormSelect2/FormSelect2';

import './ClarificationFilterForm.css';

export interface ClarificationFilterFormData {
  status: string;
}

export interface ClarificationFilterFormProps extends InjectedFormProps<ClarificationFilterFormData> {
  statuses?: string[];
  isLoading: boolean;
}

const ClarificationFilterForm = (props: ClarificationFilterFormProps) => {
  const { statuses } = props;
  const statusField: any = {
    className: 'form-status',
    name: 'status',
    label: 'Status',
    validate: [Required],
    optionValues: statuses,
    optionNamesMap: Object.assign({}, ...statuses.map(status => ({ [status]: status }))),
  };

  return (
    <form onSubmit={props.handleSubmit} className="clarification-filter-form">
      <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={props.isLoading} />
      {<Field component={FormSelect2} {...statusField} />}
    </form>
  );
};

export default reduxForm<ClarificationFilterFormData>({
  form: 'clarification-filter',
  touchOnBlur: false,
  enableReinitialize: true,
})(ClarificationFilterForm);
