import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';
import { push } from 'connected-react-router';

import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { RegradeAllButton } from '../../../../../../../components/RegradeAllButton/RegradeAllButton';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { AppState } from '../../../../../../../modules/store';
import { Contest } from '../../../../../../../modules/api/uriel/contest';
import { ContestSubmissionsResponse } from '../../../../../../../modules/api/uriel/contestSubmissionProgramming';

import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';
import { selectContest } from '../../../../modules/contestSelectors';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';

export interface ContestSubmissionsPageProps extends RouteComponentProps<{}> {
  contest: Contest;
  onGetProgrammingSubmissions: (
    contestJid: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ) => Promise<ContestSubmissionsResponse>;
  onRegrade: (submissionJid: string) => Promise<void>;
  onRegradeAll: (contestJid: string, userJid?: string, problemJid?: string) => Promise<void>;
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
        {this.renderRegradeAllButton()}
        {this.renderFilterWidget()}
        <div className="clearfix" />
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={this.onRegradeAll} />;
  };

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
      <SubmissionFilterWidget
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
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ContestSubmissionsTable
        contest={this.props.contest}
        submissions={submissions.page}
        canSupervise={config.canSupervise}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={this.onRegrade}
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
    return data.totalCount;
  };

  private refreshSubmissions = async (username?: string, problemAlias?: string, page?: number) => {
    const { userJid, problemJid } = this.getFilterJids(username, problemAlias);
    const response = await this.props.onGetProgrammingSubmissions(this.props.contest.jid, userJid, problemJid, page);
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

  private onRegrade = async (submissionJid: string) => {
    await this.props.onRegrade(submissionJid);
    const { username, problemAlias } = this.state.filter!;
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(username, problemAlias, queries.page);
  };

  private onRegradeAll = async () => {
    if (window.confirm('Regrade all submissions in all pages for the current filter?')) {
      const { username, problemAlias } = this.state.filter!;
      const { userJid, problemJid } = this.getFilterJids(username, problemAlias);
      await this.props.onRegradeAll(this.props.contest.jid, userJid, problemJid);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(username, problemAlias, queries.page);
    }
  };

  private onFilter = async filter => {
    const { username, problemAlias } = filter;
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

export function createContestSubmissionsPage(contestProgrammingSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetProgrammingSubmissions: contestProgrammingSubmissionActions.getSubmissions,
    onRegrade: contestProgrammingSubmissionActions.regradeSubmission,
    onRegradeAll: contestProgrammingSubmissionActions.regradeSubmissions,
    onAppendRoute: queries => push({ search: stringify(queries) }),
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
