import { Callout, Intent } from '@blueprintjs/core';

import './ContestStateWidget.css';

export function LoadingContestStateWidget() {
  return (
    <Callout intent={Intent.PRIMARY} className="secondary-info">
      <div className="contest-state-widget__left">&nbsp;</div>
      <div className="contest-state-widget__right">&nbsp;</div>
      <div className="clearfix" />
    </Callout>
  );
}
