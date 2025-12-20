import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import { parse, stringify } from 'query-string';
import { useEffect, useState } from 'react';
import ReactPaginate from 'react-paginate';
import { useHistory, useLocation } from 'react-router-dom';

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
        <p className="pagination__helper-text">
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

export default function PaginationContainer({ pageSize, onChangePage }) {
  const location = useLocation();
  const history = useHistory();

  const [state, setState] = useState({
    currentPage: undefined,
    totalCount: 0,
  });

  const queries = parse(location.search);

  useEffect(() => {
    refreshPagination();
  }, [queries.page]);

  const render = () => {
    const { currentPage, totalCount } = state;
    if (!currentPage) {
      return null;
    }

    const props = {
      currentPage,
      pageSize,
      totalCount,
      onChangePage: handleChangePage,
    };
    return <Pagination {...props} />;
  };

  const handleChangePage = async nextPage => {
    const queries = parse(location.search);

    let query = '';
    if (nextPage > 1) {
      query = stringify({ ...queries, page: nextPage });
    } else {
      query = stringify({ ...queries, page: undefined });
    }

    if (!queries.page && nextPage === 1) {
      history.replace({ search: query });
    } else {
      history.push({ search: query });
    }
  };

  const refreshPagination = async () => {
    const queries = parse(location.search);

    let currentPage = 1;
    const parsedCurrentPage = +queries.page;
    if (queries.page && !isNaN(parsedCurrentPage)) {
      currentPage = parsedCurrentPage;
    }

    const totalCount = await onChangePage(currentPage);
    setState({ currentPage, totalCount });
  };

  return render();
}
