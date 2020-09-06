import { parse, stringify } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { push } from 'connected-react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { SubmissionFilterWidget } from '../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { ContestLogsTable } from '../ContestLogsTable/ContestLogsTable';
import { AppState } from '../../../../../../modules/store';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestLogsResponse } from '../../../../../../modules/api/uriel/contestLog';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestLogActions from '../modules/contestLogActions';

export interface ContestLogsPageProps extends RouteComponentProps<{}> {
  contest: Contest;
  onGetLogs: (contestJid: string, userJid?: string, problemJid?: string, page?: number) => Promise<ContestLogsResponse>;
  onAppendRoute: (queries) => any;
}

interface ContestLogsFilter {
  username?: string;
  problemAlias?: string;
}

interface ContestLogsPageState {
  response?: ContestLogsResponse;
  filter?: ContestLogsFilter;
  isFilterLoading?: boolean;
}

export class ContestLogsPage extends React.PureComponent<ContestLogsPageProps, ContestLogsPageState> {
  private static PAGE_SIZE = 100;

  state: ContestLogsPageState = {};

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
        <h3>Logs</h3>
        <hr />
        {this.renderFilterWidget()}
        <div className="clearfix" />
        {this.renderLogs()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderFilterWidget = () => {
    const { response, filter, isFilterLoading } = this.state;
    if (!response) {
      return null;
    }
    const { config, profilesMap, problemAliasesMap } = response;
    const { userJids, problemJids } = config;
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

  private renderLogs = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: logs, profilesMap, problemAliasesMap } = response;
    if (logs.totalCount === 0) {
      return (
        <p>
          <small>No logs.</small>
        </p>
      );
    }

    return <ContestLogsTable logs={logs.page} profilesMap={profilesMap} problemAliasesMap={problemAliasesMap} />;
  };

  private renderPagination = () => {
    const { filter } = this.state;

    const key = '' + filter.username + filter.problemAlias;
    return <Pagination key={key} pageSize={ContestLogsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const { username, problemAlias } = this.state.filter!;
    const data = await this.refreshLogs(username, problemAlias, nextPage);
    return data.totalCount;
  };

  private refreshLogs = async (username?: string, problemAlias?: string, page?: number) => {
    const response = await this.props.onGetLogs(this.props.contest.jid, username, problemAlias, page);
    this.setState({ response, isFilterLoading: false });
    return response.data;
  };

  private onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };
}

const mapStateToProps = (state: AppState) => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetLogs: contestLogActions.getLogs,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withBreadcrumb('Logs')(connect(mapStateToProps, mapDispatchToProps)(ContestLogsPage));
