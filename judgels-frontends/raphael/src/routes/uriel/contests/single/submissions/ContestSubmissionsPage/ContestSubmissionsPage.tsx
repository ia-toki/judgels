import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';
import { push } from 'react-router-redux';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import Pagination from 'components/Pagination/Pagination';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { ContestSubmissionsResponse } from 'modules/api/uriel/contestSubmission';

import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';
import { ContestSubmissionFilterWidget } from '../ContestSubmissionFilterWidget/ContestSubmissionFilterWidget';
import { selectContest } from '../../../modules/contestSelectors';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';

export interface ContestSubmissionsPageProps extends RouteComponentProps<{}> {
  contest: Contest;
  onGetSubmissions: (
    contestJid: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ) => Promise<ContestSubmissionsResponse>;
  onAppendRoute: (queries) => any;
}

interface ContestSubmissionsFilter {
  username?: string;
  problemAlias?: string;
}

interface ContestSubmissionsPageState {
  response?: ContestSubmissionsResponse;
  filter?: ContestSubmissionsFilter;
  isFilterLoading?: boolean;
}

export class ContestSubmissionsPage extends React.PureComponent<
  ContestSubmissionsPageProps,
  ContestSubmissionsPageState
> {
  private static PAGE_SIZE = 20;

  state: ContestSubmissionsPageState = {};

  async componentDidMount() {
    const queries = parse(this.props.location.search);
    const username = queries.username as string;
    const problemAlias = queries.problemAlias as string;

    if (username || problemAlias) {
      await this.refreshSubmissions();
    }

    this.setState({ filter: { username, problemAlias } });
  }

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderFilterWidget()}
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderFilterWidget = () => {
    const { response, filter, isFilterLoading } = this.state;
    if (!response || !filter) {
      return null;
    }
    const { config, profilesMap, problemAliasesMap } = response;
    const { userJids, problemJids, canSupervise } = config;
    if (!canSupervise) {
      return null;
    }

    const { username, problemAlias } = filter;
    return (
      <ContestSubmissionFilterWidget
        usernames={userJids.map(jid => profilesMap[jid] && profilesMap[jid].username)}
        problemAliases={problemJids.map(jid => problemAliasesMap[jid])}
        username={username}
        problemAlias={problemAlias}
        onFilter={this.onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  private renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.totalData === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ContestSubmissionsTable
        contest={this.props.contest}
        submissions={submissions.data}
        canSupervise={config.canSupervise}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
      />
    );
  };

  private renderPagination = () => {
    const { filter } = this.state;
    if (!filter) {
      return null;
    }

    // updates pagination when the filter is updated
    const key = '' + filter.username + filter.problemAlias;

    return (
      <Pagination
        key={key}
        currentPage={1}
        pageSize={ContestSubmissionsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const { username, problemAlias } = this.state.filter!;
    const data = await this.refreshSubmissions(username, problemAlias, nextPage);
    return data.totalData;
  };

  private refreshSubmissions = async (username?: string, problemAlias?: string, page?: number) => {
    const { userJid, problemJid } = this.getFilterJids(username, problemAlias);
    const response = await this.props.onGetSubmissions(this.props.contest.jid, userJid, problemJid, page);
    this.setState({ response, isFilterLoading: false });
    return response.data;
  };

  private getFilterJids = (username?: string, problemAlias?: string) => {
    const { response } = this.state;
    if (!response) {
      return {};
    }

    const { config, profilesMap, problemAliasesMap } = response;
    const { userJids, problemJids } = config;

    const userJid = userJids.find(jid => profilesMap[jid].username === username);
    const problemJid = problemJids.find(jid => problemAliasesMap[jid] === problemAlias);
    return { userJid, problemJid };
  };

  private onFilter = async (username?: string, problemAlias?: string) => {
    const filter = { username, problemAlias };
    this.setState(prevState => {
      const prevFilter = prevState.filter || {};
      return {
        filter,
        isFilterLoading: prevFilter.username !== username || prevFilter.problemAlias !== problemAlias,
      };
    });
    this.props.onAppendRoute(filter);
  };
}

export function createContestSubmissionsPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetSubmissions: contestSubmissionActions.getSubmissions,
    onAppendRoute: queries => push({ search: stringify(queries) }),
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
