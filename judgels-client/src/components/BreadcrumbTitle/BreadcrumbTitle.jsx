import { Breadcrumb, Breadcrumbs, Classes } from '@blueprintjs/core';
import { Link } from '@tanstack/react-router';

import './BreadcrumbTitle.scss';

// Without the <nav> that Blueprint wraps the list in: its aria-label would become the name of the heading around it.
const OVERFLOW_LIST_PROPS = { navigable: false };

function renderParentBreadcrumb({ to, icon, text }) {
  return (
    <Link to={to} className={Classes.BREADCRUMB}>
      {icon}
      {text}
    </Link>
  );
}

function renderCurrentBreadcrumb({ text }) {
  return <Breadcrumb current text={text} />;
}

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
