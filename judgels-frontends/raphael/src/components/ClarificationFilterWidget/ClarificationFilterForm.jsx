import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { Required } from '../forms/validations';
import { FormSelect2 } from '../forms/FormSelect2/FormSelect2';

import './ClarificationFilterForm.scss';

function ClarificationFilterForm({ handleSubmit, isLoading, statuses }) {
  const statusField = {
    className: 'form-status',
    name: 'status',
    label: 'Status',
    validate: [Required],
    optionValues: statuses,
    optionNamesMap: Object.assign({}, ...statuses.map(status => ({ [status]: status }))),
  };

  return (
    <form onSubmit={handleSubmit} className="clarification-filter-form">
      <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={isLoading} />
      {<Field component={FormSelect2} {...statusField} />}
    </form>
  );
}

export default reduxForm({
  form: 'clarification-filter',
  touchOnBlur: false,
  enableReinitialize: true,
})(ClarificationFilterForm);
