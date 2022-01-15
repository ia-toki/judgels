import { Classes, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import { Link } from 'react-router-dom';

export function ButtonLink({ className, active, intent, small, large, ...linkProps }) {
  return (
    <Link
      {...linkProps}
      className={classNames(Classes.BUTTON, className, {
        [Classes.ACTIVE]: active,
        [Classes.INTENT_PRIMARY]: intent === Intent.PRIMARY,
        [Classes.INTENT_WARNING]: intent === Intent.WARNING,
        [Classes.SMALL]: small,
        [Classes.LARGE]: large,
      })}
    />
  );
}
