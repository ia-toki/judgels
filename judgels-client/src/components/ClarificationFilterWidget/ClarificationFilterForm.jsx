import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { FormSelect2 } from '../forms/FormSelect2/FormSelect2';
import { Required } from '../forms/validations';

import './ClarificationFilterForm.scss';

export default function ClarificationFilterForm({ onSubmit, initialValues, statuses, isLoading }) {
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
      {({ handleSubmit }) => (
        <form onSubmit={handleSubmit} className="clarification-filter-form">
          <Flex gap={2} justifyContent="end">
            <Field component={FormSelect2} {...statusField} />
            <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={isLoading} />
          </Flex>
        </form>
      )}
    </Form>
  );
}
