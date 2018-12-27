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

import './ContestsPage.css';

export interface ContestsPageProps extends RouteComponentProps<{ name: string }> {
  onGetContests: (name?: string, page?: number) => Promise<ContestsResponse>;
  onCreateContest: (data: ContestCreateData) => Promise<Contest>;
}

interface ContestsFilter {
  name?: string;
}

export interface ContestsPageState {
  response?: Partial<ContestsResponse>;
  filter?: ContestsFilter;
  isFilterLoading?: boolean;
}

class ContestsPage extends React.Component<ContestsPageProps, ContestsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestsPageState = {};

  componentWillMount() {
    const queries = parse(this.props.location.search);
    const name = queries.name as string;

    this.setState({ filter: { name }, isFilterLoading: false });
  }

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
        <div className="content-card__section search-box-inline">{this.renderFilter()}</div>
        <div className="clearfix" />
        {this.renderFilterResultsBanner()}
      </>
    );
  };

  private renderFilterResultsBanner = () => {
    const name = this.getNameFilter(this.state);
    if (!name) {
      return null;
    }

    return (
      <div className="content-card__section">
        Showing results for: <b>{name}</b>
      </div>
    );
  };

  private renderFilter = () => {
    const name = this.getNameFilter(this.state);
    return (
      <SearchBox
        onRouteChange={this.searchBoxUpdateQueries}
        initialValue={name || ''}
        isLoading={this.state.isFilterLoading}
      />
    );
  };

  private renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config || !config.canAdminister) {
      return null;
    }
    return <ContestCreateDialog onCreateContest={this.props.onCreateContest} />;
  };

  private renderContests = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContestCard />;
    }

    const { data: contests, rolesMap } = response;
    if (!contests || !rolesMap) {
      return <LoadingContestCard />;
    }

    return contests.page.map(contest => (
      <ContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };

  private renderPagination = () => {
    return (
      <Pagination
        pageSize={ContestsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
        key={this.getNameFilter(this.state) || ''}
      />
    );
  };

  private onChangePage = async (nextPage?: number) => {
    this.setState({ response: { ...this.state.response, data: undefined } });
    const response = await this.props.onGetContests(this.getNameFilter(this.state), nextPage);
    this.setState({ response, isFilterLoading: false });
    return response.data.totalCount;
  };

  private searchBoxUpdateQueries = (name: string, queries: any) => {
    this.setState(prevState => {
      const isFilterLoading = this.getNameFilter(prevState) !== name;
      return {
        filter: {
          name,
        },
        isFilterLoading,
        isContestsListLoading: isFilterLoading,
      };
    });
    return { ...queries, page: undefined, name };
  };

  private getNameFilter = (state: ContestsPageState) => {
    if (!state.filter || !state.filter.name) {
      return '';
    }
    return state.filter.name;
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
