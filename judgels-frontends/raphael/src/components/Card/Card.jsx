import { Card as BlueprintCard } from '@blueprintjs/core';

import './Card.css';

export function Card({ className, title, action, actionRightJustified, children }) {
  return (
    <div className={className}>
      <BlueprintCard className="card__title">
        <h3 className="card__title__text">{title}</h3>
        <div className={'card__title__action' + (actionRightJustified ? '_right' : '')}>{action}</div>
      </BlueprintCard>
      <BlueprintCard className="card__content">{children}</BlueprintCard>
    </div>
  );
}
