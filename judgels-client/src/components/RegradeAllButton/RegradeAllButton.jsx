import { Button, Intent } from '@blueprintjs/core';
import { Refresh } from '@blueprintjs/icons';

export function RegradeAllButton({ onRegradeAll, isRegradingAll }) {
  return (
    <Button intent={Intent.PRIMARY} icon={<Refresh />} onClick={onRegradeAll} loading={isRegradingAll}>
      Regrade all pages
    </Button>
  );
}
