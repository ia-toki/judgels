import * as React from 'react';

import SubmissionFilterForm, { SubmissionFilterFormData } from './SubmissionFilterForm';

export interface SubmissionFilterWidgetProps {
  usernames?: string[];
  problemAliases?: string[];
  onFilter: (filter: any) => Promise<void>;
  isLoading: boolean;

  username?: string;
  problemAlias?: string;
}

export class SubmissionFilterWidget extends React.Component<SubmissionFilterWidgetProps> {
  render() {
    const { usernames, problemAliases, isLoading, username, problemAlias } = this.props;
    const formProps = {
      usernames: usernames && ['-', ...usernames],
      problemAliases: problemAliases && ['-', ...problemAliases],
      isLoading,
      initialValues: {
        username: username || '-',
        problemAlias: problemAlias || '-',
      },
    };

    return <SubmissionFilterForm onSubmit={this.onFilter} {...formProps} />;
  }

  private onFilter = async (data: SubmissionFilterFormData) => {
    const username = data.username === '-' ? undefined : data.username;
    const problemAlias = data.problemAlias === '-' ? undefined : data.problemAlias;
    return await this.props.onFilter({ username, problemAlias });
  };
}
