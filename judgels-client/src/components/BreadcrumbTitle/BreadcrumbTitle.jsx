import { Breadcrumbs, Classes } from '@blueprintjs/core';
import { Link } from '@tanstack/react-router';
import classNames from 'classnames';

import './BreadcrumbTitle.scss';

// Without the <nav> that Blueprint wraps the list in: its aria-label would become the name of the heading around it.
const OVERFLOW_LIST_PROPS = { navigable: false };

// A Blueprint Breadcrumb renders a plain <a>, which would reload the whole app.
function renderBreadcrumb({ to, icon, text }, isCurrent) {
  return (
    <Link to={to} className={classNames(Classes.BREADCRUMB, { [Classes.BREADCRUMB_CURRENT]: isCurrent })}>
      {icon}
      {text}
    </Link>
  );
}

const renderParentBreadcrumb = item => renderBreadcrumb(item, false);
const renderCurrentBreadcrumb = item => renderBreadcrumb(item, true);

// Goes inside a heading, whose size it takes. Each of the items is a link: { to, text } or { to, icon }.
export function BreadcrumbTitle({ items }) {
  return (
    <Breadcrumbs
      className="breadcrumb-title"
      items={items}
      minVisibleItems={items.length}
      overflowListProps={OVERFLOW_LIST_PROPS}
      breadcrumbRenderer={renderParentBreadcrumb}
      currentBreadcrumbRenderer={renderCurrentBreadcrumb}
    />
  );
}
