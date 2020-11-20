import * as React from 'react';

import SubmissionFilterForm from './SubmissionFilterForm';

export function SubmissionFilterWidget({ usernames, problemAliases, isLoading, onFilter, username, problemAlias }) {
  const filter = async data => {
    const username = data.username === '-' ? undefined : data.username;
    const problemAlias = data.problemAlias === '-' ? undefined : data.problemAlias;
    return await onFilter({ username, problemAlias });
  };

  const formProps = {
    usernames: usernames && ['-', ...usernames],
    problemAliases: problemAliases && ['-', ...problemAliases],
    isLoading,
    initialValues: {
      username: username || '-',
      problemAlias: problemAlias || '-',
    },
  };

  return <SubmissionFilterForm onSubmit={filter} {...formProps} />;
}
