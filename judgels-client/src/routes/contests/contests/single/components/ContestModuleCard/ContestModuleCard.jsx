import { Button, Callout } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { moduleDescriptionsMap, moduleTitlesMap } from '../../../../../../modules/api/uriel/contestModule';

import './ContestModuleCard.scss';

export function ContestModuleCard({ type, intent, buttonIntent, buttonText, buttonOnClick }) {
  const clickButton = () => {
    buttonOnClick(type);
  };

  return (
    <Callout className="contest-module-card" intent={intent} icon={null}>
      <Flex justifyContent="space-between">
        <div>
          <h5>{moduleTitlesMap[type]}</h5>
          <small>{moduleDescriptionsMap[type]}</small>
        </div>
        <div>
          <Button small intent={buttonIntent} onClick={clickButton}>
            {buttonText}
          </Button>
        </div>
      </Flex>
    </Callout>
  );
}
