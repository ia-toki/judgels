import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { parse } from 'query-string';

import Pagination from 'components/Pagination/Pagination';
import { Card } from 'components/Card/Card';
import SearchBox from 'components/SearchBox/SearchBox';
import { Contest, ContestCreateData, ContestsResponse } from 'modules/api/uriel/contest';

import { LoadingContestCard } from '../ContestCard/LoadingContestCard';
import { ContestCard } from '../ContestCard/ContestCard';
import { ContestCreateDialog } from '../ContestCreateDialog/ContestCreateDialog';
import { contestActions as injectedContestActions } from '../modules/contestActions';

import './ContestPage.css';

export interface ContestsPageProps extends RouteComponentProps<{ name: string }> {
  onGetContests: (name?: string, page?: number) => Promise<ContestsResponse>;
  onCreateContest: (data: ContestCreateData) => Promise<Contest>;
}

export interface ContestsPageState {
  response?: ContestsResponse;
}

class ContestsPage extends React.Component<ContestsPageProps, ContestsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestsPageState = {};

  render() {
    return (
      <Card title="Contests">
        {this.renderHeader()}
        {this.renderContests()}
        {this.renderPagination()}
      </Card>
    );
  }

  private renderHeader = () => {
    return (
      <>
        <div className="content-card__section create-contest-button-inline">{this.renderCreateDialog()}</div>
        <div className="content-card__section search-box-inline">{this.renderSearchBox()}</div>
        <div className="clearfix" />
      </>
    );
  };

  private renderSearchBox = () => {
    const queries = parse(this.props.location.search);
    return <SearchBox nextRoute={this.searchBoxUpdateQueries} initialValue={queries.name} />;
  };

  private renderCreateDialog = () => {
    return <ContestCreateDialog onCreateContest={this.props.onCreateContest} />;
  };

  private renderContests = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContestCard />;
    }

    const { data: contests, rolesMap } = response;
    return contests.page.map(contest => (
      <ContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };

  private renderPagination = () => {
    const queries = parse(this.props.location.search);
    return <Pagination pageSize={ContestsPage.PAGE_SIZE} onChangePage={this.onChangePage} key={queries.name} />;
  };

  private onChangePage = async (nextPage?: number) => {
    const queries = parse(this.props.location.search);
    const response = await this.props.onGetContests(queries.name, nextPage);
    this.setState({ response });
    return response.data.totalCount;
  };

  private searchBoxUpdateQueries = (content: string, queries: any) => {
    this.setState({ response: undefined });
    queries.page = undefined;
    queries.name = content;
    return queries;
  };
}

export function createContestsPage(contestActions) {
  const mapDispatchToProps = {
    onGetContests: contestActions.getContests,
    onCreateContest: contestActions.createContest,
  };
  return connect(undefined, mapDispatchToProps)(ContestsPage);
}

export default createContestsPage(injectedContestActions);
