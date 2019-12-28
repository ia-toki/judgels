import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { parse } from 'query-string';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { Card } from '../../../../components/Card/Card';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { ProblemSetsResponse } from '../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetCard } from '../ProblemSetCard/ProblemSetCard';
import { problemSetActions as injectedProblemSetActions } from '../modules/problemSetActions';

import './ProblemSetsPage.css';

export interface ProblemSetsPageProps extends RouteComponentProps<{ name: string }> {
  onGetProblemSets: (name?: string, page?: number) => Promise<ProblemSetsResponse>;
}

interface ProblemSetsFilter {
  name?: string;
}

export interface ProblemSetsPageState {
  response?: Partial<ProblemSetsResponse>;
  filter?: ProblemSetsFilter;
  isFilterLoading?: boolean;
}

class ProblemSetsPage extends React.Component<ProblemSetsPageProps, ProblemSetsPageState> {
  private static PAGE_SIZE = 20;

  state: ProblemSetsPageState = {};

  componentDidMount() {
    const queries = parse(this.props.location.search);
    const name = queries.name as string;

    this.setState({ filter: { name }, isFilterLoading: false });
  }

  render() {
    return (
      <Card title="Problemsets">
        {this.renderHeader()}
        {this.renderProblemSets()}
        {this.renderPagination()}
      </Card>
    );
  }

  private renderHeader = () => {
    return (
      <>
        <div className="content-card__section search-box-inline">{this.renderFilter()}</div>
        <div className="clearfix" />
        <div className="content-card__section">
          {this.renderFilterResultsBanner()}
          <hr />
        </div>
      </>
    );
  };

  private renderFilterResultsBanner = () => {
    const name = this.getNameFilter(this.state);
    if (!name) {
      return <small>Most recently added problemsets:</small>;
    }

    return (
      <>
        Search results for: <b>{name}</b>
      </>
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

  private renderProblemSets = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data, archiveDescriptionsMap } = response;
    if (!data) {
      return <LoadingState />;
    }
    if (data.page.length === 0) {
      return (
        <p>
          <small>No problemsets.</small>
        </p>
      );
    }
    return data.page.map(problemSet => (
      <ProblemSetCard
        key={problemSet.jid}
        problemSet={problemSet}
        archiveDescription={archiveDescriptionsMap[problemSet.archiveJid]}
      />
    ));
  };

  private renderPagination = () => {
    if (!this.state.filter) {
      return null;
    }
    return (
      <Pagination
        pageSize={ProblemSetsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
        key={this.getNameFilter(this.state) || ''}
      />
    );
  };

  private onChangePage = async (nextPage?: number) => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }
    const response = await this.props.onGetProblemSets(this.getNameFilter(this.state), nextPage);
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
        isProblemSetsListLoading: isFilterLoading,
      };
    });
    return { ...queries, page: undefined, name };
  };

  private getNameFilter = (state: ProblemSetsPageState) => {
    if (!state.filter || !state.filter.name) {
      return '';
    }
    return state.filter.name;
  };
}

export function createProblemSetsPage(problemSetActions) {
  const mapDispatchToProps = {
    onGetProblemSets: problemSetActions.getProblemSets,
  };
  return connect(undefined, mapDispatchToProps)(ProblemSetsPage);
}

export default createProblemSetsPage(injectedProblemSetActions);
