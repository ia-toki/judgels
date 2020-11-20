import * as React from 'react';

import ClarificationFilterForm from './ClarificationFilterForm';

export function ClarificationFilterWidget({ statuses, isLoading, status, onFilter }) {
  const formProps = {
    statuses: ['-', ...statuses],
    isLoading,
    initialValues: {
      status: status || '-',
    },
  };

  const filter = async data => {
    const status = data.status === '-' ? undefined : data.status;
    return await onFilter({ status });
  };

  return <ClarificationFilterForm onSubmit={filter} {...formProps} />;
}
