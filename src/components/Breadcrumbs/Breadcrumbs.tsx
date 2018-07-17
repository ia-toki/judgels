import * as classNames from 'classnames';
import * as React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

import { Breadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';
import { selectSortedBreadcrumbs } from '../../modules/breadcrumbs/breadcrumbsSelectors';
import { AppState } from '../../modules/store';

import './Breadcrumbs.css';

export interface BreadcrumbsProps {
  breadcrumbs: Breadcrumb[];
}

export const Breadcrumbs = (props: BreadcrumbsProps) => {
  const items = props.breadcrumbs.map((item, idx) => (
    <li key={item.link}>
      <Link
        to={item.link}
        className={classNames('pt-breadcrumb', { 'pt-breadcrumb-current': idx === props.breadcrumbs.length - 1 })}
      >
        {item.title}
      </Link>
    </li>
  ));
  return (
    <div className="breadcrumbs">
      <div className="breadcrumbs__content">
        <ul className="pt-breadcrumbs">{items}</ul>
      </div>
    </div>
  );
};

export function createBreadcrumbsContainer() {
  const mapStateToProps = (state: AppState) => ({
    breadcrumbs: selectSortedBreadcrumbs(state),
  });
  return connect(mapStateToProps)(Breadcrumbs);
}

const BreadcrumbsContainer = createBreadcrumbsContainer();
export default BreadcrumbsContainer;
