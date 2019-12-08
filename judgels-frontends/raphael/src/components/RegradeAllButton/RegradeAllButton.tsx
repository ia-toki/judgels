import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';

import './RegradeAllButton.css';

export const RegradeAllButton = ({ onRegradeAll }) => (
  <Button className="regrade-all" intent={Intent.PRIMARY} icon="refresh" onClick={onRegradeAll}>
    Regrade all pages
  </Button>
);
