import classNames from 'classnames';

import './ContentCardList.scss';

export function ContentCardList({ rows, children }) {
  return <div className={classNames('content-card-list', { 'content-card-list--rows': rows })}>{children}</div>;
}
