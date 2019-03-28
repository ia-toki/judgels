import { HTMLTable, Icon } from '@blueprintjs/core';
import { parse, stringify } from 'query-string';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter, Link } from 'react-router-dom';
import { push } from 'react-router-redux';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { UserRef } from 'components/UserRef/UserRef';
import Pagination from 'components/Pagination/Pagination';
import { ContestItemSubmissionsResponse } from 'modules/api/uriel/contestSubmissionBundle';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { selectContest } from 'routes/uriel/contests/modules/contestSelectors';

import { ContestSubmissionFilterWidget } from '../../ContestSubmissionFilterWidget/ContestSubmissionFilterWidget';
import { VerdictTag } from '../VerdictTag/VerdictTag';
import { FormattedAnswer } from '../FormattedAnswer/FormattedAnswer';
import { contestSubmissionActions as injectedContestSubmissionActions } from '../modules/contestSubmissionActions';

import './ContestSubmissionsPage.css';

export interface ContestSubmissionsPageProps extends RouteComponentProps<{}> {
  contest: Contest;
  onGetSubmissions: (
    contestJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ) => Promise<ContestItemSubmissionsResponse>;
  onAppendRoute: (queries) => any;
}

interface ContestSubmissionsFilter {
  username?: string;
  problemAlias?: string;
}

interface ContestSubmissionsPageState {
  response?: ContestItemSubmissionsResponse;
  filter?: ContestSubmissionsFilter;
  isFilterLoading?: boolean;
}

export class ContestSubmissionsPage extends React.Component<ContestSubmissionsPageProps, ContestSubmissionsPageState> {
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
      <ContentCard className="contest-bundle-submissions-page">
        <h3>Submissions</h3>
        <hr />
        {this.renderFilterWidget()}
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderSubmissions = () => {
    const response = this.state.response;
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap, problemAliasesMap, itemNumbersMap, itemTypesMap } = response;
    const { contest } = this.props;
    const canManage = response.config.canManage;

    return (
      <HTMLTable striped className="table-list-condensed submissions-table">
        <thead>
          <tr>
            <th>User</th>
            <th className="col-prob">Prob</th>
            <th className="col-item-num">No</th>
            <th>Answer</th>
            {canManage && <th className="col-verdict">Verdict</th>}
            <th>Time</th>
            <th className="col-action" />
          </tr>
        </thead>
        <tbody>
          {data.page.map(item => (
            <tr key={item.jid}>
              <td>
                <UserRef profile={profilesMap[item.userJid]} />
              </td>
              <td className="col-prob">{problemAliasesMap[item.problemJid] || '-'}</td>
              <td className="col-item-num">{itemNumbersMap[item.itemJid] || '-'}</td>
              <td>
                <FormattedAnswer answer={item.answer} type={itemTypesMap[item.itemJid]} />
              </td>
              {canManage && (
                <td className="col-verdict">{item.grading ? <VerdictTag verdict={item.grading.verdict} /> : '-'}</td>
              )}
              <td>
                <FormattedRelative value={item.time} />
              </td>
              <td className="col-action">
                <Link to={`/contests/${contest.slug}/submissions/users/${profilesMap[item.userJid].username}`}>
                  <Icon icon="search" />
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
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

  private refreshSubmissions = async (username?: string, problemAlias?: string, page?: number) => {
    const { contest, onGetSubmissions } = this.props;
    const response = await onGetSubmissions(contest.jid, username, problemAlias, page);
    this.setState({ response, isFilterLoading: false });
    return response;
  };

  private onChangePage = async (nextPage: number) => {
    const { username, problemAlias } = this.state.filter!;
    const response = await this.refreshSubmissions(username, problemAlias, nextPage);
    return response.data.totalCount;
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
