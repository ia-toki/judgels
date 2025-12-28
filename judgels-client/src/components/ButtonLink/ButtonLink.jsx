import { Button, Classes, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import { Link } from 'react-router';

export function ButtonLink({ className, disabled, active, intent, small, large, ...linkProps }) {
  if (disabled) {
    return <Button {...linkProps} small={small} disabled />;
  }
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
