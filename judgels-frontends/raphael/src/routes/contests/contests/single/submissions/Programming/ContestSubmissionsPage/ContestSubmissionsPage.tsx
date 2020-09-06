import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { push } from 'connected-react-router';

import { reallyConfirm } from '../../../../../../../utils/confirmation';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { RegradeAllButton } from '../../../../../../../components/RegradeAllButton/RegradeAllButton';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { AppState } from '../../../../../../../modules/store';
import { Contest } from '../../../../../../../modules/api/uriel/contest';
import { ContestSubmissionsResponse } from '../../../../../../../modules/api/uriel/contestSubmissionProgramming';
import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';
import { selectContest } from '../../../../modules/contestSelectors';
import * as contestSubmissionActions from '../modules/contestSubmissionActions';

export interface ContestSubmissionsPageProps extends RouteComponentProps<{}> {
  contest: Contest;
  onGetProgrammingSubmissions: (
    contestJid: string,
    username?: string,
    problemAlias?: string,
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

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const username = queries.username as string;
    const problemAlias = queries.problemAlias as string;

    this.state = { filter: { username, problemAlias } };
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const username = queries.username as string;
    const problemAlias = queries.problemAlias as string;

    if (username !== this.state.filter.username || problemAlias !== this.state.filter.problemAlias) {
      this.setState({ filter: { username, problemAlias }, isFilterLoading: true });
    }
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
    if (!response) {
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

    const key = '' + filter.username + filter.problemAlias;
    return <Pagination key={key} pageSize={ContestSubmissionsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const { username, problemAlias } = this.state.filter!;
    const data = await this.refreshSubmissions(username, problemAlias, nextPage);
    return data.totalCount;
  };

  private refreshSubmissions = async (username?: string, problemAlias?: string, page?: number) => {
    const response = await this.props.onGetProgrammingSubmissions(this.props.contest.jid, username, problemAlias, page);
    this.setState({ response, isFilterLoading: false });
    return response.data;
  };

  private onRegrade = async (submissionJid: string) => {
    await this.props.onRegrade(submissionJid);
    const { username, problemAlias } = this.state.filter!;
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(username, problemAlias, queries.page);
  };

  private onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      const { username, problemAlias } = this.state.filter!;
      await this.props.onRegradeAll(this.props.contest.jid, username, problemAlias);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(username, problemAlias, queries.page);
    }
  };

  private onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };
}

const mapStateToProps = (state: AppState) => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetProgrammingSubmissions: contestSubmissionActions.getSubmissions,
  onRegrade: contestSubmissionActions.regradeSubmission,
  onRegradeAll: contestSubmissionActions.regradeSubmissions,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withBreadcrumb('Submissions')(connect(mapStateToProps, mapDispatchToProps)(ContestSubmissionsPage));
