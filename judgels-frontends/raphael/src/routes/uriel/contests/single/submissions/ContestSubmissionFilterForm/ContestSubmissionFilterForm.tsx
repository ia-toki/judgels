import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from 'components/forms/validations';
import { FormSelect2 } from 'components/forms/FormSelect2/FormSelect2';

import './ContestSubmissionFilterForm.css';

export interface ContestSubmissionFilterFormData {
  username: string;
  problemAlias: string;
}

export interface ContestSubmissionFilterFormProps extends InjectedFormProps<ContestSubmissionFilterFormData> {
  usernames: string[];
  problemAliases: string[];
  isLoading: boolean;
}

const ContestSubmissionFilterForm = (props: ContestSubmissionFilterFormProps) => {
  const { usernames, problemAliases } = props;
  const userField: any = {
    className: 'form-username',
    name: 'username',
    label: 'User',
    validate: [Required],
    optionValues: usernames,
    optionNamesMap: Object.assign({}, ...usernames.map(username => ({ [username]: username }))),
  };

  const problemField: any = {
    className: 'form-problem-alias',
    name: 'problemAlias',
    label: 'Problem',
    validate: [Required],
    optionValues: problemAliases,
    optionNamesMap: Object.assign({}, ...problemAliases.map(alias => ({ [alias]: alias }))),
  };

  return (
    <form onSubmit={props.handleSubmit} className="contest-submission-filter-form">
      <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={props.isLoading} />
      <Field component={FormSelect2} {...problemField} />
      <Field component={FormSelect2} {...userField} />
    </form>
  );
};

export default reduxForm<ContestSubmissionFilterFormData>({
  form: 'contest-submission-filter',
})(ContestSubmissionFilterForm);
