import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';
import { push } from 'react-router-redux';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import Pagination from 'components/Pagination/Pagination';
import { AppState } from 'modules/store';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Contest } from 'modules/api/uriel/contest';
import { ContestSubmissionConfig, ContestSubmissionsResponse } from 'modules/api/uriel/contestSubmission';
import { Page } from 'modules/api/pagination';
import { Submission } from 'modules/api/sandalphon/submission';

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
  onGetSubmissionConfig: (contestJid: string) => Promise<ContestSubmissionConfig>;
  onAppendRoute: (queries) => any;
}

interface ContestSubmissionsPageState {
  config?: ContestSubmissionConfig;

  submissions?: Page<Submission>;
  profilesMap?: ProfilesMap;
  problemAliasesMap?: { [problemJid: string]: string };

  filter: {
    username?: string;
    problemAlias?: string;
  };
  isFilterLoading?: boolean;
}

export class ContestSubmissionsPage extends React.PureComponent<
  ContestSubmissionsPageProps,
  ContestSubmissionsPageState
> {
  private static PAGE_SIZE = 20;

  state: ContestSubmissionsPageState = { filter: {} };

  async componentDidMount() {
    const config = await this.props.onGetSubmissionConfig(this.props.contest.jid);
    const queries = parse(this.props.location.search);
    this.setState({
      config,
      filter: {
        username: queries.username as string,
        problemAlias: queries.problemAlias as string,
      },
    });
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
    const { config, filter: { username, problemAlias }, isFilterLoading } = this.state;
    if (!config || !config.isAllowedToViewAllSubmissions) {
      return null;
    }

    const { usernamesMap, problemJids, problemAliasesMap } = config;
    return (
      <ContestSubmissionFilterWidget
        usernames={Object.keys(usernamesMap).map(jid => usernamesMap[jid])}
        problemAliases={problemJids.map(jid => problemAliasesMap[jid])}
        username={username}
        problemAlias={problemAlias}
        onFilter={this.onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  private renderSubmissions = () => {
    const { config, submissions, profilesMap, problemAliasesMap } = this.state;
    if (!config || !submissions) {
      return <LoadingState />;
    }

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
        profilesMap={profilesMap!}
        problemAliasesMap={problemAliasesMap!}
        showUserColumn={config.isAllowedToViewAllSubmissions}
      />
    );
  };

  private renderPagination = () => {
    const { config, filter } = this.state;
    return (
      config && (
        <Pagination
          key={'' + filter.username + filter.problemAlias}
          currentPage={1}
          pageSize={ContestSubmissionsPage.PAGE_SIZE}
          onChangePage={this.onChangePage}
        />
      )
    );
  };

  private onChangePage = async (nextPage: number) => {
    const { filter } = this.state;
    const data = await this.refreshSubmissions(filter.username, filter.problemAlias, nextPage);
    return data.totalData;
  };

  private refreshSubmissions = async (username?: string, problemAlias?: string, page?: number) => {
    const { userJid, problemJid } = this.getFilterJids(username, problemAlias);
    const { data, profilesMap, problemAliasesMap } = await this.props.onGetSubmissions(
      this.props.contest.jid,
      userJid,
      problemJid,
      page
    );
    this.setState({
      submissions: data,
      profilesMap,
      problemAliasesMap,
      isFilterLoading: false,
    });
    return data;
  };

  private getFilterJids = (username?: string, problemAlias?: string) => {
    const { usernamesMap, problemJids, problemAliasesMap } = this.state.config!;
    const userJid = Object.keys(usernamesMap).find(jid => usernamesMap[jid] === username);
    const problemJid = problemJids.find(jid => problemAliasesMap[jid] === problemAlias);
    return { userJid, problemJid };
  };

  private onFilter = async (username?: string, problemAlias?: string) => {
    const filter = { username, problemAlias };
    this.setState(prevState => ({
      filter,
      isFilterLoading: prevState.filter.username !== username || prevState.filter.problemAlias !== problemAlias,
    }));
    this.props.onAppendRoute(filter);
  };
}

function createContestSubmissionsPage(contestSubmissionActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetSubmissions: contestSubmissionActions.getSubmissions,
    onGetSubmissionConfig: contestSubmissionActions.getSubmissionConfig,
    onAppendRoute: queries => push({ search: stringify(queries) }),
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
}

export default createContestSubmissionsPage(injectedContestSubmissionActions);
