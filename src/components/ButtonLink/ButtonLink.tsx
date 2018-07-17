import { Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';
import { Link, LinkProps } from 'react-router-dom';

export interface ButtonLinkProps extends LinkProps {
  intent?: Intent;
}

export const ButtonLink = (props: ButtonLinkProps) => {
  const { intent, ...linkProps } = props;
  const className = classNames('pt-button', {
    'pt-intent-primary': intent === Intent.PRIMARY,
  });

  return <Link {...linkProps} as="button" className={className} />;
};
