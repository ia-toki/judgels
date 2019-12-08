import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from '../../components/forms/validations';
import { FormSelect2 } from '../../components/forms/FormSelect2/FormSelect2';

import './SubmissionFilterForm.css';

export interface SubmissionFilterFormData {
  username: string;
  problemAlias: string;
}

export interface SubmissionFilterFormProps extends InjectedFormProps<SubmissionFilterFormData> {
  usernames?: string[];
  problemAliases?: string[];
  isLoading: boolean;
}

const SubmissionFilterForm = (props: SubmissionFilterFormProps) => {
  const { usernames, problemAliases } = props;
  const userField: any = usernames && {
    className: 'form-username',
    name: 'username',
    label: 'User',
    validate: [Required],
    optionValues: usernames,
    optionNamesMap: Object.assign({}, ...usernames.map(username => ({ [username]: username }))),
  };

  const problemField: any = problemAliases && {
    className: 'form-problem-alias',
    name: 'problemAlias',
    label: 'Problem',
    validate: [Required],
    optionValues: problemAliases,
    optionNamesMap: Object.assign({}, ...problemAliases.map(alias => ({ [alias]: alias }))),
  };

  return (
    <form onSubmit={props.handleSubmit} className="submission-filter-form">
      <Button type="submit" text="Filter" intent={Intent.PRIMARY} loading={props.isLoading} />
      {problemAliases && <Field component={FormSelect2} {...problemField} />}
      {usernames && <Field component={FormSelect2} {...userField} />}
    </form>
  );
};

export default reduxForm<SubmissionFilterFormData>({
  form: 'submission-filter',
  touchOnBlur: false,
  enableReinitialize: true,
})(SubmissionFilterForm);
