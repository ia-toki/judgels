import { Callout, Intent } from '@blueprintjs/core';
import { InfoSign } from '@blueprintjs/icons';

import './ContestStateWidget.scss';

export function LoadingContestStateWidget() {
  return (
    <Callout intent={Intent.PRIMARY} className="secondary-info" icon={<InfoSign />}>
      <div className="contest-state-widget__left">&nbsp;</div>
      <div className="contest-state-widget__right">&nbsp;</div>
      <div className="clearfix" />
    </Callout>
  );
}
