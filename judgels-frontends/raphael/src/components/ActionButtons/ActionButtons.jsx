import classNames from 'classnames';
import * as React from 'react';

import './ActionButtons.css';

export function ActionButtons({ leftAlign, children }) {
  return <div className={classNames('action-buttons', { 'right-action-buttons': !leftAlign })}>{children}</div>;
}
