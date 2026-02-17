import { Classes } from '@blueprintjs/core';
import { useLocation, useNavigate } from '@tanstack/react-router';
import classNames from 'classnames';
import ReactPaginate from 'react-paginate';

import '../Pagination/Pagination.scss';

export default function PaginationV2({ pageSize, totalCount }) {
  const location = useLocation();
  const navigate = useNavigate();

  const getCurrentPage = () => {
    const queries = location.search;
    const parsedCurrentPage = +queries.page;
    if (queries.page && !isNaN(parsedCurrentPage)) {
      return parsedCurrentPage;
    }
    return 1;
  };

  const currentPage = getCurrentPage();
  const totalPages = Math.ceil(totalCount / pageSize);
  const start = (currentPage - 1) * pageSize + 1;
  const end = currentPage * pageSize;

  const handleChangePage = nextPage => {
    const queries = location.search;
    const page = nextPage.selected + 1;

    let newSearch;
    if (page > 1) {
      newSearch = { ...queries, page };
    } else {
      newSearch = { ...queries, page: undefined };
    }

    if (!queries.page && page === 1) {
      navigate({ search: newSearch, replace: true });
    } else {
      navigate({ search: newSearch });
    }
  };

  return (
    <div className={totalCount > 0 ? 'pagination' : 'pagination--hide'}>
      {totalCount > 0 && (
        <small>
          <p className="pagination__helper-text">
            Showing {start}..{Math.min(end, totalCount)} of {totalCount} results
          </p>
        </small>
      )}
      <ReactPaginate
        forcePage={currentPage - 1}
        pageCount={totalPages}
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
        onPageChange={handleChangePage}
        disableInitialCallback
      />
    </div>
  );
}
