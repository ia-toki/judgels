import { Callout, Intent } from '@blueprintjs/core';
import { InfoSign } from '@blueprintjs/icons';

export function LoadingContestStateWidget() {
  return (
    <Callout intent={Intent.PRIMARY} className="secondary-info" icon={<InfoSign />}>
      <div className="float-left">&nbsp;</div>
      <div className="float-right">&nbsp;</div>
      <div className="clearfix" />
    </Callout>
  );
}
