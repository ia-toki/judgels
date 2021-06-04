import classNames from 'classnames';

import './ActionButtons.scss';

export function ActionButtons({ leftAlign, children }) {
  return <div className={classNames('action-buttons', { 'right-action-buttons': !leftAlign })}>{children}</div>;
}
