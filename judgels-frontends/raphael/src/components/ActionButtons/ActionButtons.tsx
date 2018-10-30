import * as classNames from 'classnames';
import * as React from 'react';

import './ActionButtons.css';

export const ActionButtons = props => (
  <div className={classNames('action-buttons', { 'right-action-buttons': !props.leftAlign })}>{props.children}</div>
);
