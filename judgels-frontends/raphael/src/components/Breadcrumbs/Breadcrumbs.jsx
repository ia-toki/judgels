import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

import { selectSortedBreadcrumbs } from '../../modules/breadcrumbs/breadcrumbsSelectors';

import './Breadcrumbs.css';

function Breadcrumbs({ breadcrumbs }) {
  const items = breadcrumbs.map((item, idx) => (
    <li key={item.link}>
      <Link
        to={item.link}
        className={classNames(Classes.BREADCRUMB, {
          [Classes.BREADCRUMB_CURRENT]: idx === breadcrumbs.length - 1,
        })}
      >
        {item.title}
      </Link>
    </li>
  ));
  return (
    <div className="breadcrumbs">
      <div className="breadcrumbs__content">
        <ul className={Classes.BREADCRUMBS}>{items}</ul>
      </div>
    </div>
  );
}

const mapStateToProps = state => ({
  breadcrumbs: selectSortedBreadcrumbs(state),
});
export default connect(mapStateToProps)(Breadcrumbs);
