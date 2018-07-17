import * as React from 'react';

import './Card.css';

export interface CardProps {
  className?: string;
  title: string;
  action?: JSX.Element;
  children?: any;
}

export const Card = (props: CardProps) => (
  <div className={props.className}>
    <div className="pt-card card__title">
      <h3 className="card__title__text">{props.title}</h3>
      <div className="card__title__action">{props.action}</div>
    </div>
    <div className="pt-card card__content">{props.children}</div>
  </div>
);
