import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import { parse, stringify } from 'query-string';
import { PureComponent } from 'react';
import ReactPaginate from 'react-paginate';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
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
        initialPage={currentPage - 1}
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

class PaginationContainer extends PureComponent {
  state = { totalCount: 0 };

  render() {
    const { location, pageSize } = this.props;

    const queries = parse(location.search);

    let currentPage = 1;
    const parsedCurrentPage = +queries.page;
    if (queries.page && !isNaN(parsedCurrentPage)) {
      currentPage = parsedCurrentPage;
    }

    const props = {
      currentPage,
      pageSize: pageSize,
      totalCount: this.state.totalCount,
      onChangePage: this.onChangePage,
    };
    return <Pagination {...props} />;
  }

  onChangePage = async nextPage => {
    const { location, onAppendRoute, onChangePage } = this.props;

    const queries = parse(location.search);
    onAppendRoute(nextPage, queries);
    const totalCount = await onChangePage(nextPage);
    this.setState({ totalCount });
  };
}

const mapDispatchToProps = {
  onAppendRoute: (nextPage, queries) => {
    let query = '';
    if (nextPage > 1) {
      query = stringify({ ...queries, page: nextPage });
    } else {
      query = stringify({ ...queries, page: undefined });
    }
    return push({ search: query });
  },
};
export default withRouter(connect(undefined, mapDispatchToProps)(PaginationContainer));
