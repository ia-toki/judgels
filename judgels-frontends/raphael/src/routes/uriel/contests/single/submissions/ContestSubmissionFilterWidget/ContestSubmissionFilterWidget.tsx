import * as React from 'react';

import ContestSubmissionFilterForm, {
  ContestSubmissionFilterFormData,
} from '../ContestSubmissionFilterForm/ContestSubmissionFilterForm';

export interface ContestSubmissionFilterWidgetProps {
  usernames: string[];
  problemAliases: string[];
  onFilter: (username?: string, problemAlias?: string) => Promise<void>;
  isLoading: boolean;

  username?: string;
  problemAlias?: string;
}

export class ContestSubmissionFilterWidget extends React.Component<ContestSubmissionFilterWidgetProps> {
  render() {
    const { usernames, problemAliases, isLoading, username, problemAlias } = this.props;
    const sortedUsernames = usernames.slice().sort((username1, username2) => username1.localeCompare(username2));

    const formProps = {
      usernames: ['-', ...sortedUsernames],
      problemAliases: ['-', ...problemAliases],
      isLoading,
      initialValues: {
        username: username || '-',
        problemAlias: problemAlias || '-',
      },
    };

    return (
      <>
        <ContestSubmissionFilterForm onSubmit={this.onFilter} {...formProps} />
        <div className="clearfix " />
      </>
    );
  }

  private onFilter = async (data: ContestSubmissionFilterFormData) => {
    const username = data.username === '-' ? undefined : data.username;
    const problemAlias = data.problemAlias === '-' ? undefined : data.problemAlias;
    return await this.props.onFilter(username, problemAlias);
  };
}
