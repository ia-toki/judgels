import { Classes } from '@blueprintjs/core';
import * as classNames from 'classnames';
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
  totalCount: number;
  onChangePage: (nextPage: number) => void;
}

class Pagination extends React.PureComponent<PaginationProps, {}> {
  render() {
    const { totalCount } = this.props;

    return (
      <div className={totalCount > 0 ? 'pagination' : 'pagination--hide'}>
        {this.renderText()}
        {this.renderNavigation()}
      </div>
    );
  }

  private getTotalPages = () => {
    const { totalCount, pageSize } = this.props;
    return Math.ceil(totalCount / pageSize);
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
    const { totalCount } = this.props;
    const { start, end } = this.getRange();

    if (totalCount === 0) {
      return null;
    }

    return (
      <p className="pagination__helper-text" data-key="pagination-helper-text">
        Showing {start}..{Math.min(end, totalCount)} of {totalCount} results
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
        onPageChange={this.onChangePage}
      />
    );
  };
}

interface PaginationContainerProps {
  pageSize: number;
  key?: any;
  onChangePage: (nextPage: number) => Promise<number>;
}

interface PaginationContainerConnectedProps extends RouteComponentProps<{ page: string }> {
  onAppendRoute: (nextPage: number, queries: any) => any;
}

interface PaginationContainerState {
  totalCount: number;
}

class PaginationContainer extends React.PureComponent<
  PaginationContainerProps & PaginationContainerConnectedProps,
  PaginationContainerState
> {
  state: PaginationContainerState = { totalCount: 0 };

  async componentDidUpdate(prevProps) {
    if (this.props.key !== prevProps.key) {
      const queries = parse(this.props.location.search);
      const totalCount = await this.props.onChangePage(queries.page);
      this.setState({ totalCount });
    }
  }

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
      totalCount: this.state.totalCount,
      onChangePage: this.onChangePage,
    };
    return <Pagination {...props} />;
  }

  private onChangePage = async (nextPage: number) => {
    const queries = parse(this.props.location.search);
    this.props.onAppendRoute(nextPage, queries);
    const totalCount = await this.props.onChangePage(nextPage);
    this.setState({ totalCount });
  };
}

function createPagination() {
  const mapDispatchToProps = {
    onAppendRoute: (nextPage: number, queries: any) => {
      let query = '';
      if (nextPage > 1) {
        query = stringify({ ...queries, page: nextPage });
      } else {
        query = stringify({ ...queries, page: undefined });
      }
      return push({ search: query });
    },
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(PaginationContainer));
}

export default createPagination();
export { PaginationContainerProps as PaginationProps };
