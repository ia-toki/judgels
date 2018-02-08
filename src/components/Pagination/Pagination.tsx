import * as React from 'react';
import * as ReactPaginate from 'react-paginate';

import './Pagination.css';

export interface PaginationProps {
  totalItems: number;
  pageSize: number;
  currentPage: number;

  onPageChanged?: (nextPage: number) => void;
}

export class Pagination extends React.Component<PaginationProps, {}> {
  render() {
    const { totalItems } = this.props;
    const { start, end } = this.getRange();

    return (
      <div className="pagination-container">
        <p>
          Showing {start}..{end} of {totalItems} results
        </p>
        {this.renderNavigation()}
      </div>
    );
  }

  private getTotalPages = () => {
    const { totalItems, pageSize } = this.props;
    return Math.ceil(totalItems / pageSize);
  };

  private getRange = () => {
    const { currentPage, pageSize } = this.props;
    return {
      start: (currentPage - 1) * pageSize + 1,
      end: currentPage * pageSize,
    };
  };

  private actionPageChanged = (nextPage: { selected: number }) => {
    const { onPageChanged } = this.props;
    if (onPageChanged) {
      onPageChanged(nextPage.selected);
    }
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
        pageLinkClassName="pagination-link"
        nextLinkClassName="pagination-link"
        previousLinkClassName="pagination-link"
        breakClassName="pt-button pt-disabled"
        containerClassName="pt-button-group"
        activeClassName="pt-button pt-active"
        previousClassName="pt-button"
        nextClassName="pt-button"
        onPageChange={this.actionPageChanged}
      />
    );
  };
}
