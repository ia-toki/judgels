import { Classes, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';
import { Link, LinkProps } from 'react-router-dom';

export interface ButtonLinkProps extends LinkProps {
  className?: string;
  intent?: Intent;
}

export const ButtonLink = (props: ButtonLinkProps) => {
  const { intent, ...linkProps } = props;
  const className = classNames(Classes.BUTTON, props.className, {
    [Classes.INTENT_PRIMARY]: intent === Intent.PRIMARY,
  });

  return <Link {...linkProps} className={className} />;
};
