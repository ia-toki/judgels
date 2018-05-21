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
  onChangePage: (nextPage: number) => void;
}

class Pagination extends React.PureComponent<PaginationProps, {}> {
  render() {
    const { totalData } = this.props;

    return (
      <div className={totalData > 0 ? 'pagination' : 'pagination--hide'}>
        {this.renderText()}
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

  private onChangePage = (nextPage: { selected: number }) => {
    this.props.onChangePage(nextPage.selected + 1);
  };

  private renderText = () => {
    const { totalData } = this.props;
    const { start, end } = this.getRange();

    if (totalData === 0) {
      return null;
    }

    return (
      <p className="pagination__helper-text" data-key="pagination-helper-text">
        Showing {start}..{Math.min(end, totalData)} of {totalData} results
      </p>
    );
  };

  private renderNavigation = () => {
    const { currentPage } = this.props;

    return (
      <ReactPaginate
        initialPage={currentPage - 1}
        pageCount={this.getTotalPages()}
        pageRangeDisplayed={3}
        marginPagesDisplayed={2}
        pageClassName="pt-button pagination__item"
        previousLabel="<"
        nextLabel=">"
        pageLinkClassName="pagination__link"
        nextLinkClassName="pagination__link"
        previousLinkClassName="pagination__link"
        breakClassName="pt-button pt-disabled"
        containerClassName="pt-button-group"
        activeClassName="pt-button pt-active pagination__item"
        previousClassName="pt-button pagination__item"
        nextClassName="pt-button pagination__item"
        onPageChange={this.onChangePage}
      />
    );
  };
}

interface PaginationContainerProps {
  pageSize: number;
  onChangePage: (nextPage: number) => Promise<number>;
}

interface PaginationContainerConnectedProps extends RouteComponentProps<{ page: string }> {
  onAppendRoute: (nextPage: number) => any;
}

interface PaginationContainerState {
  totalData: number;
}

class PaginationContainer extends React.PureComponent<
  PaginationContainerProps & PaginationContainerConnectedProps,
  PaginationContainerState
> {
  state: PaginationContainerState = { totalData: 0 };

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
      totalData: this.state.totalData,
      onChangePage: this.onChangePage,
    };
    return <Pagination {...props} />;
  }

  private onChangePage = async (nextPage: number) => {
    this.props.onAppendRoute(nextPage);
    const totalData = await this.props.onChangePage(nextPage);
    this.setState({ totalData });
  };
}

function createPagination() {
  const mapDispatchToProps = {
    onAppendRoute: (nextPage: number) => {
      let query = '';
      if (nextPage > 1) {
        query = stringify({ page: nextPage });
      }
      return push({ search: query });
    },
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(PaginationContainer));
}

export default createPagination();
export { PaginationContainerProps as PaginationProps };
