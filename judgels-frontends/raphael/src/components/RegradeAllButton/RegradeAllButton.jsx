import { Button, Intent } from '@blueprintjs/core';
import { Refresh } from '@blueprintjs/icons';

export function RegradeAllButton({ onRegradeAll }) {
  return (
    <Button intent={Intent.PRIMARY} icon={<Refresh />} onClick={onRegradeAll}>
      Regrade all pages
    </Button>
  );
}
