import classNames from 'classnames';
import { Link } from 'react-router-dom';

import './ContentCardLink.scss';

export function ContentCardLink({ secondary, className, to, elevation, children }) {
  // This is basically a <ContentCard /> but with div replaced with a.
  return (
    <Link
      className={classNames(`bp5-card bp-elevation-${elevation} content-card content-card-link`, {
        'content-card--is-secondary': secondary,
      })}
      to={to}
    >
      <div className={classNames('content-card-link__content', className)}>{children}</div>
    </Link>
  );
}
