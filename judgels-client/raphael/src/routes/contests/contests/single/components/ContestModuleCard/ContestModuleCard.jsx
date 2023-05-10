import { Button, Callout } from '@blueprintjs/core';

import { moduleDescriptionsMap, moduleTitlesMap } from '../../../../../../modules/api/uriel/contestModule';

import './ContestModuleCard.scss';

export function ContestModuleCard({ type, intent, buttonIntent, buttonText, buttonOnClick }) {
  const clickButton = () => {
    buttonOnClick(type);
  };

  return (
    <Callout className="contest-module-card" intent={intent} icon={null}>
      <div className="float-left">
        <h5>{moduleTitlesMap[type]}</h5>
        <small>{moduleDescriptionsMap[type]}</small>
      </div>
      <div className="float-right">
        <Button small intent={buttonIntent} onClick={clickButton}>
          {buttonText}
        </Button>
      </div>
      <div className="clearfix" />
    </Callout>
  );
}
