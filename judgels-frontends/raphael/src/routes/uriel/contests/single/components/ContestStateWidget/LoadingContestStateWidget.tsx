import { Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';

import './ContestStateWidget.css';

export const LoadingContestStateWidget = () => (
  <Callout intent={Intent.PRIMARY} className="secondary-info">
    <div className="contest-state-widget__left bp3-skeleton">Contest is loading</div>
    <div className="contest-state-widget__right">Ends in loading</div>
    <div className="clearfix" />
  </Callout>
);
