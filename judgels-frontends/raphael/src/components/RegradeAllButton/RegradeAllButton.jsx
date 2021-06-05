import { Button, Intent } from '@blueprintjs/core';
import { Refresh } from '@blueprintjs/icons';

import './RegradeAllButton.scss';

export function RegradeAllButton({ onRegradeAll }) {
  return (
    <Button className="regrade-all" intent={Intent.PRIMARY} icon={<Refresh />} onClick={onRegradeAll}>
      Regrade all pages
    </Button>
  );
}
