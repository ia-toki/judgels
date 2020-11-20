import { Classes, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';
import { Link } from 'react-router-dom';

export function ButtonLink({ className, intent, ...linkProps }) {
  return (
    <Link
      {...linkProps}
      className={classNames(Classes.BUTTON, className, {
        [Classes.INTENT_PRIMARY]: intent === Intent.PRIMARY,
      })}
    />
  );
}
