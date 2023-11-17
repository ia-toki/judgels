import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import ReactPaginate from 'react-paginate';
import { connect } from 'react-redux';
import { push, replace } from 'connected-react-router';
import { withRouter } from 'react-router';

import './Pagination.scss';

function Pagination({ currentPage, pageSize, totalCount, onChangePage }) {
  const getTotalPages = () => {
    return Math.ceil(totalCount / pageSize);
  };

  const getRange = () => {
    return {
      start: (currentPage - 1) * pageSize + 1,
      end: currentPage * pageSize,
    };
  };

  const changePage = nextPage => {
    onChangePage(nextPage.selected + 1);
  };

  const renderText = () => {
    const { start, end } = getRange();

    if (totalCount === 0) {
      return null;
    }

    return (
      <small>
        <p className="pagination__helper-text" data-key="pagination-helper-text">
          Showing {start}..{Math.min(end, totalCount)} of {totalCount} results
        </p>
      </small>
    );
  };

  const renderNavigation = () => {
    return (
      <ReactPaginate
        forcePage={currentPage - 1}
        pageCount={getTotalPages()}
        pageRangeDisplayed={3}
        marginPagesDisplayed={2}
        pageClassName={classNames(Classes.BUTTON, 'pagination__item')}
        previousLabel="<"
        nextLabel=">"
        pageLinkClassName="pagination__link"
        nextLinkClassName="pagination__link"
        previousLinkClassName="pagination__link"
        breakClassName={classNames(Classes.BUTTON, Classes.DISABLED)}
        containerClassName={Classes.BUTTON_GROUP}
        activeClassName={classNames(Classes.BUTTON, Classes.ACTIVE, 'pagination__item')}
        previousClassName={classNames(Classes.BUTTON, 'pagination__item')}
        nextClassName={classNames(Classes.BUTTON, 'pagination__item')}
        onPageChange={changePage}
        disableInitialCallback
      />
    );
  };

  return (
    <div className={totalCount > 0 ? 'pagination' : 'pagination--hide'}>
      {renderText()}
      {renderNavigation()}
    </div>
  );
}

class PaginationContainer extends Component {
  state = {
    currentPage: undefined,
    totalCount: 0,
  };

  componentDidMount() {
    this.refreshPagination();
  }

  componentDidUpdate(prevProps) {
    const queries = parse(this.props.location.search);
    const prevQueries = parse(prevProps.location.search);

    if (queries.page !== prevQueries.page) {
      this.refreshPagination();
    }
  }

  render() {
    const { currentPage, totalCount } = this.state;
    if (!currentPage) {
      return null;
    }

    const { pageSize } = this.props;

    const props = {
      currentPage,
      pageSize,
      totalCount,
      onChangePage: this.onChangePage,
    };
    return <Pagination {...props} />;
  }

  onChangePage = async nextPage => {
    const { location, onPush, onReplace } = this.props;
    const queries = parse(location.search);

    let query = '';
    if (nextPage > 1) {
      query = stringify({ ...queries, page: nextPage });
    } else {
      query = stringify({ ...queries, page: undefined });
    }

    if (!queries.page && nextPage === 1) {
      return onReplace({ search: query });
    } else {
      return onPush({ search: query });
    }
  };

  refreshPagination = async () => {
    const { location, onChangePage } = this.props;
    const queries = parse(location.search);

    let currentPage = 1;
    const parsedCurrentPage = +queries.page;
    if (queries.page && !isNaN(parsedCurrentPage)) {
      currentPage = parsedCurrentPage;
    }

    const totalCount = await onChangePage(currentPage);
    this.setState({ currentPage, totalCount });
  };
}

const mapDispatchToProps = {
  onPush: push,
  onReplace: replace,
};
export default withRouter(connect(undefined, mapDispatchToProps)(PaginationContainer));
