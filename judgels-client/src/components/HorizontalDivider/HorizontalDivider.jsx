import classNames from 'classnames';

import './HorizontalDivider.scss';

export function HorizontalDivider({ inner }) {
  return <hr className={classNames('horizontal-divider', { 'horizontal-divider--inner': inner })} />;
}
