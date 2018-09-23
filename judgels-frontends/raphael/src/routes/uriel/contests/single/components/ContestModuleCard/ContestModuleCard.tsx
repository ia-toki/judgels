import { Button, Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ContestModuleType, moduleDescriptionsMap, moduleTitlesMap } from 'modules/api/uriel/contestModule';

import './ContestModuleCard.css';

export interface ContestModuleCardProps {
  type: ContestModuleType;
  intent: Intent;
  buttonIntent: Intent;
  buttonText: string;
  buttonOnClick: (type: ContestModuleType) => void;
  buttonIsLoading: boolean;
  buttonIsDisabled: boolean;
}

export class ContestModuleCard extends React.Component<ContestModuleCardProps> {
  render() {
    return (
      <Callout className="contest-module-card" intent={this.props.intent} icon={null}>
        <div className="contest-module-card__content">
          <h5>{moduleTitlesMap[this.props.type]}</h5>
          <small>{moduleDescriptionsMap[this.props.type]}</small>
        </div>
        <div className="contest-module-card__button">
          <Button small intent={this.props.buttonIntent} onClick={this.buttonOnClick}>
            {this.props.buttonText}
          </Button>
        </div>
        <div className="clearfix" />
      </Callout>
    );
  }

  private buttonOnClick = () => {
    this.props.buttonOnClick(this.props.type);
  };
}
