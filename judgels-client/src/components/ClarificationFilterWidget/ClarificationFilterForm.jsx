import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { Required } from '../forms/validations';
import { FormSelect2 } from '../forms/FormSelect2/FormSelect2';

import './ClarificationFilterForm.scss';

export default function ClarificationFilterForm({ onSubmit, initialValues, statuses }) {
  const statusField = {
    className: 'form-status',
    name: 'status',
    label: 'Status',
    validate: Required,
    optionValues: statuses,
    optionNamesMap: Object.assign({}, ...statuses.map(status => ({ [status]: status }))),
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, isLoading }) => (
        <form onSubmit={handleSubmit} className="clarification-filter-form">
          <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={isLoading} />
          <Field component={FormSelect2} {...statusField} />
          <div className="clearfix" />
        </form>
      )}
    </Form>
  );
}
