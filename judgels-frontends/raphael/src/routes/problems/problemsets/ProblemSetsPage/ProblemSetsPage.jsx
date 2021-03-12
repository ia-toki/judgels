import { Component } from 'react';
import { connect } from 'react-redux';
import { parse } from 'query-string';

import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { Card } from '../../../../components/Card/Card';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { ProblemSetCard } from '../ProblemSetCard/ProblemSetCard';
import * as problemSetActions from '../modules/problemSetActions';

import './ProblemSetsPage.css';

class ProblemSetsPage extends Component {
  static PAGE_SIZE = 20;

  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const archiveSlug = queries.archive;
    const name = queries.name;

    this.state = {
      response: undefined,
      filter: { archiveSlug, name },
      isFilterLoading: false,
    };
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const archiveSlug = queries.archive;

    if (archiveSlug !== this.state.filter.archiveSlug) {
      this.setState({ filter: { archiveSlug }, isFilterLoading: false });
    }
  }

  render() {
    return (
      <Card title="Browse problemsets">
        {this.renderHeader()}
        {this.renderProblemSets()}
        {this.renderPagination()}
      </Card>
    );
  }

  renderHeader = () => {
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

  renderFilterResultsBanner = () => {
    const { archiveSlug, name } = this.state.filter;
    if (!archiveSlug && !name) {
      return <>Most recently added problemsets:</>;
    }

    const { response } = this.state;
    const archiveName = response && response.archiveName;

    if (archiveName && !name) {
      return (
        <>
          Problemsets in archive <b>{archiveName}</b>:
        </>
      );
    }
    if (!name) {
      return null;
    }

    const archiveNameResult = archiveName ? (
      <>
        {' '}
        in archive <b>{archiveName}:</b>
      </>
    ) : (
      ''
    );

    return (
      <>
        Search results for: <b>{name}</b>
        {archiveNameResult}
      </>
    );
  };

  renderFilter = () => {
    const { name } = this.state.filter;
    return (
      <SearchBox
        onRouteChange={this.searchBoxUpdateQueries}
        initialValue={name || ''}
        isLoading={this.state.isFilterLoading}
      />
    );
  };

  renderProblemSets = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data, archiveDescriptionsMap, problemSetProgressesMap, profilesMap } = response;
    if (!data) {
      return <LoadingContentCard />;
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
        progress={problemSetProgressesMap[problemSet.jid]}
        profilesMap={profilesMap}
      />
    ));
  };

  renderPagination = () => {
    const { filter } = this.state;
    return (
      <Pagination
        pageSize={ProblemSetsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
        key={'' + filter.archiveSlug + filter.name}
      />
    );
  };

  onChangePage = async nextPage => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }
    const { archiveSlug, name } = this.state.filter;
    const response = await this.props.onGetProblemSets(archiveSlug, name, nextPage);
    this.setState({ response, isFilterLoading: false });
    return response.data.totalCount;
  };

  searchBoxUpdateQueries = (name, queries) => {
    this.setState(prevState => {
      const prevFilter = prevState.filter || {};
      return {
        filter: {
          ...prevFilter,
          name,
        },
        isFilterLoading: prevFilter.name !== name,
      };
    });
    return { ...queries, page: undefined, name };
  };
}

const mapDispatchToProps = {
  onGetProblemSets: problemSetActions.getProblemSets,
};
export default connect(undefined, mapDispatchToProps)(ProblemSetsPage);
