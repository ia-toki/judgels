import { parse, stringify } from 'query-string';
import * as React from 'react';
import * as ReactPaginate from 'react-paginate';
import { connect } from 'react-redux';
import { push } from 'react-router-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import './Pagination.css';

interface PaginationProps {
  currentPage: number;
  pageSize: number;
  totalData: number;
  onChangePage: (nextPage: number) => Promise<void>;
}

class Pagination extends React.Component<PaginationProps, {}> {
  render() {
    const { totalData } = this.props;
    const { start, end } = this.getRange();

    return (
      <div className="pagination">
        <p className="pagination__helper-text" data-key="pagination-helper-text">
          Showing {start}..{Math.min(end, totalData)} of {totalData} results
        </p>
        {this.renderNavigation()}
      </div>
    );
  }

  private getTotalPages = () => {
    const { totalData, pageSize } = this.props;
    return Math.ceil(totalData / pageSize);
  };

  private getRange = () => {
    const { currentPage, pageSize } = this.props;
    return {
      start: (currentPage - 1) * pageSize + 1,
      end: currentPage * pageSize,
    };
  };

  private onChangePage = async (nextPage: { selected: number }) => {
    await this.props.onChangePage(nextPage.selected + 1);
  };

  private renderNavigation = () => {
    const { currentPage } = this.props;

    return (
      <ReactPaginate
        initialPage={currentPage - 1}
        pageCount={this.getTotalPages()}
        pageRangeDisplayed={3}
        marginPagesDisplayed={1}
        pageClassName="pt-button"
        previousLabel="<"
        nextLabel=">"
        pageLinkClassName="pagination__link"
        nextLinkClassName="pagination__link"
        previousLinkClassName="pagination__link"
        breakClassName="pt-button pt-disabled"
        containerClassName="pt-button-group"
        activeClassName="pt-button pt-active"
        previousClassName="pt-button"
        nextClassName="pt-button"
        onPageChange={this.onChangePage}
      />
    );
  };
}

interface PaginationContainerProps {
  pageSize: number;
  totalData: number;
  onChangePage: (nextPage: number) => Promise<void>;
}

interface PaginationContainerConnectedProps extends RouteComponentProps<{ page: string }> {
  onAppendRoute: (nextPage: number) => Promise<void>;
}

class PaginationContainer extends React.Component<PaginationContainerProps & PaginationContainerConnectedProps> {
  render() {
    const queries = parse(this.props.location.search);

    let currentPage = 1;
    const parsedCurrentPage = +queries.page;
    if (queries.page && !isNaN(parsedCurrentPage)) {
      currentPage = parsedCurrentPage;
    }

    const props: PaginationProps = {
      currentPage,
      pageSize: this.props.pageSize,
      totalData: this.props.totalData,
      onChangePage: this.onChangePage,
    };
    return <Pagination {...props} />;
  }

  private onChangePage = async (nextPage: number) => {
    await this.props.onAppendRoute(nextPage);
    await this.props.onChangePage(nextPage);
  };
}

function createPagination() {
  const mapDispatchToProps = dispatch => ({
    onAppendRoute: (nextPage: number) => {
      let query = '';
      if (nextPage > 1) {
        query = stringify({ page: nextPage });
      }
      return dispatch(push({ search: query }));
    },
  });
  return withRouter<any>(connect(undefined, mapDispatchToProps)(PaginationContainer));
}

export default createPagination();
export { PaginationContainerProps as PaginationProps };
