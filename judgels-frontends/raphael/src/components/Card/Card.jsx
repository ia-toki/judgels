import { Card as BlueprintCard } from '@blueprintjs/core';
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
    <BlueprintCard className="card__title">
      <h3 className="card__title__text">{props.title}</h3>
      <div className={'card__title__action' + (props.actionRightJustified ? '_right' : '')}>{props.action}</div>
    </BlueprintCard>
    <BlueprintCard className="card__content">{props.children}</BlueprintCard>
  </div>
);
