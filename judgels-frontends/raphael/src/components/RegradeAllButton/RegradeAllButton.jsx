import { Button, Intent } from '@blueprintjs/core';

import './RegradeAllButton.css';

export function RegradeAllButton({ onRegradeAll }) {
  return (
    <Button className="regrade-all" intent={Intent.PRIMARY} icon="refresh" onClick={onRegradeAll}>
      Regrade all pages
    </Button>
  );
}
