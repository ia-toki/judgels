import { Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { Required } from '../forms/validations';
import { FormSelect2 } from '../forms/FormSelect2/FormSelect2';

import './SubmissionFilterForm.scss';

export default function SubmissionFilterForm({ onSubmit, initialValues, usernames, problemAliases, isLoading }) {
  const userField = usernames && {
    className: 'form-username',
    name: 'username',
    label: 'User',
    validate: Required,
    optionValues: usernames,
    optionNamesMap: Object.assign({}, ...usernames.map(username => ({ [username]: username }))),
  };

  const problemField = problemAliases && {
    className: 'form-problem-alias',
    name: 'problemAlias',
    label: 'Problem',
    validate: Required,
    optionValues: problemAliases,
    optionNamesMap: Object.assign({}, ...problemAliases.map(alias => ({ [alias]: alias }))),
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit }) => (
        <form onSubmit={handleSubmit} className="submission-filter-form">
          <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={isLoading} />
          {problemAliases && <Field component={FormSelect2} {...problemField} />}
          {usernames && <Field component={FormSelect2} {...userField} />}
        </form>
      )}
    </Form>
  );
}
