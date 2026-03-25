import { Callout, Intent } from '@blueprintjs/core';
import { InfoSign } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';

export function LoadingContestStateWidget() {
  return (
    <Callout intent={Intent.PRIMARY} className="secondary-info" icon={<InfoSign />}>
      <Flex justifyContent="space-between">
        <div>&nbsp;</div>
        <div>&nbsp;</div>
      </Flex>
    </Callout>
  );
}
