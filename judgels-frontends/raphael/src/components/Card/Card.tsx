import * as React from 'react';

import './Card.css';

export interface CardProps {
  className?: string;
  title: string;
  action?: JSX.Element;
  actionRightJustified?: boolean;
  children?: any;
}

export const Card = (props: CardProps) => (
  <div className={props.className}>
    <div className="bp3-card card__title">
      <h3 className="card__title__text">{props.title}</h3>
      <div className={'card__title__action' + (props.actionRightJustified ? '_right' : '')}>{props.action}</div>
    </div>
    <div className="bp3-card card__content">{props.children}</div>
  </div>
);
