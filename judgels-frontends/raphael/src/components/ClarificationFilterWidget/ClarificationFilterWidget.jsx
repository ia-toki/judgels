import * as React from 'react';

import ClarificationFilterForm, { ClarificationFilterFormData } from './ClarificationFilterForm';

export interface ClarificationFilterWidgetProps {
  statuses?: string[];
  onFilter: (filter: any) => Promise<void>;
  isLoading: boolean;

  status?: string;
}

export class ClarificationFilterWidget extends React.Component<ClarificationFilterWidgetProps> {
  render() {
    const { statuses, isLoading, status } = this.props;
    const formProps = {
      statuses: ['-', ...statuses],
      isLoading,
      initialValues: {
        status: status || '-',
      },
    };

    return <ClarificationFilterForm onSubmit={this.onFilter} {...formProps} />;
  }

  private onFilter = async (data: ClarificationFilterFormData) => {
    const status = data.status === '-' ? undefined : data.status;
    return await this.props.onFilter({ status });
  };
}
