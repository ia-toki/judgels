import { Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { Field, Form } from 'react-final-form';

import { FormSelect2 } from '../forms/FormSelect2/FormSelect2';
import { Required } from '../forms/validations';

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
          <Flex justifyContent="end" gap={2} alignItems="end">
            {usernames && <Field component={FormSelect2} {...userField} />}
            {problemAliases && <Field component={FormSelect2} {...problemField} />}
            <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={isLoading} />
          </Flex>
        </form>
      )}
    </Form>
  );
}
