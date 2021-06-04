import { Card } from '@blueprintjs/core';

import './ContentCard.scss';

export function ContentCard({ className, elevation, children }) {
  return (
    <div className={className}>
      <Card className="content-card" elevation={elevation}>
        {children}
      </Card>
    </div>
  );
}
