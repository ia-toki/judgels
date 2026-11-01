import classNames from 'classnames';

import './FullPageLayout.scss';

export function FullPageLayout({ narrow, children }) {
  return <div className={classNames('full-page-layout', { 'full-page-layout--narrow': narrow })}>{children}</div>;
}
