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
  response?: ContestsResponse;
  filter?: ContestsFilter;
  isSearchBoxLoading?: boolean;
  isContestsListLoading?: boolean;
}

class ContestsPage extends React.Component<ContestsPageProps, ContestsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestsPageState = {};

  componentWillMount() {
    const queries = parse(this.props.location.search);
    const name = queries.name as string;

    this.setState({ filter: { name }, isSearchBoxLoading: false, isContestsListLoading: true });
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
        <div className="content-card__section search-box-inline">{this.renderSearchBox()}</div>
        <div className="clearfix" />
        {this.renderSearchResultsBanner()}
      </>
    );
  };

  private renderSearchResultsBanner = () => {
    const filter = this.state.filter || {};
    if (!filter.name) {
      return null;
    }

    return (
      <div className="content-card__section">
        Showing results for: <b>{filter.name}</b>
      </div>
    );
  };

  private renderSearchBox = () => {
    const filter = this.state.filter || {};
    return (
      <SearchBox
        onRouteChange={this.searchBoxUpdateQueries}
        initialValue={filter.name || ''}
        isLoading={this.state.isSearchBoxLoading}
      />
    );
  };

  private renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.canAdminister) {
      return null;
    }
    return <ContestCreateDialog onCreateContest={this.props.onCreateContest} />;
  };

  private renderContests = () => {
    const { response, isContestsListLoading } = this.state;
    if (!response || isContestsListLoading) {
      return <LoadingContestCard />;
    }

    const { data: contests, rolesMap } = response;
    return contests.page.map(contest => (
      <ContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };

  private renderPagination = () => {
    const filter = this.state.filter || {};
    return <Pagination pageSize={ContestsPage.PAGE_SIZE} onChangePage={this.onChangePage} key={filter.name || ''} />;
  };

  private onChangePage = async (nextPage?: number) => {
    const filter = this.state.filter || {};
    this.setState({ isContestsListLoading: true });
    const response = await this.props.onGetContests(filter.name, nextPage);
    this.setState({ response, isSearchBoxLoading: false, isContestsListLoading: false });
    return response.data.totalCount;
  };

  private searchBoxUpdateQueries = (name: string, queries: any) => {
    this.setState(prevState => {
      const prevFilter = prevState.filter || {};
      const isSearchBoxLoading = prevFilter.name !== name;
      return {
        filter: {
          name,
        },
        isSearchBoxLoading,
        isContestsListLoading: isSearchBoxLoading,
      };
    });
    return { ...queries, page: undefined, name };
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
