import { Component } from 'react';
import { connect } from 'react-redux';
import { parse } from 'query-string';

import Pagination from '../../../../components/Pagination/Pagination';
import { Card } from '../../../../components/Card/Card';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { ContestCard } from '../ContestCard/ContestCard';
import { ContestCreateDialog } from '../ContestCreateDialog/ContestCreateDialog';
import * as contestActions from '../modules/contestActions';

import './ContestsPage.scss';

class ContestsPage extends Component {
  static PAGE_SIZE = 20;

  state;

  constructor(props) {
    super(props);

    const queries = parse(props.location.search);
    const name = queries.name;

    this.state = {
      response: undefined,
      filter: { name },
      isFilterLoading: false,
    };
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

  renderHeader = () => {
    return (
      <>
        <div className="content-card__section create-contest-button-inline">{this.renderCreateDialog()}</div>
        <div className="content-card__section search-box-inline">{this.renderFilter()}</div>
        <div className="clearfix" />
        {this.renderFilterResultsBanner()}
      </>
    );
  };

  renderFilterResultsBanner = () => {
    const name = this.getNameFilter(this.state);
    if (!name) {
      return null;
    }

    return (
      <div className="content-card__section">
        Search results for: <b>{name}</b>
        <hr />
      </div>
    );
  };

  renderFilter = () => {
    const name = this.getNameFilter(this.state);
    return (
      <SearchBox
        onRouteChange={this.searchBoxUpdateQueries}
        initialValue={name || ''}
        isLoading={this.state.isFilterLoading}
      />
    );
  };

  renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const config = response.config;
    if (!config.canAdminister) {
      return null;
    }
    return <ContestCreateDialog onCreateContest={this.props.onCreateContest} />;
  };

  renderContests = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const rolesMap = response.rolesMap;
    const contests = response.data;
    if (!contests) {
      return <LoadingContentCard />;
    }

    if (contests.page.length === 0) {
      return (
        <p>
          <small>No contests.</small>
        </p>
      );
    }

    return contests.page.map(contest => (
      <ContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };

  renderPagination = () => {
    return (
      <Pagination
        pageSize={ContestsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
        key={this.getNameFilter(this.state) || ''}
      />
    );
  };

  onChangePage = async nextPage => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }
    const response = await this.props.onGetContests(this.getNameFilter(this.state), nextPage);
    this.setState({ response, isFilterLoading: false });
    return response.data.totalCount;
  };

  searchBoxUpdateQueries = (name, queries) => {
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

  getNameFilter = state => {
    if (!state.filter.name) {
      return '';
    }
    return state.filter.name;
  };
}

const mapDispatchToProps = {
  onGetContests: contestActions.getContests,
  onCreateContest: contestActions.createContest,
};
export default connect(undefined, mapDispatchToProps)(ContestsPage);
