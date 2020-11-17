import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ContestClarification } from '../../../../../../modules/api/uriel/contestClarification';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import {
  default as ContestClarificationAnswerForm,
  ContestClarificationAnswerFormData,
} from '../ContestClarificationAnswerForm/ContestClarificationAnswerForm';

export interface ContestClarificationAnswerBoxProps {
  contest: Contest;
  clarification: ContestClarification;
  isBoxOpen: boolean;
  isBoxLoading: boolean;
  onToggleBox: (clarification?: ContestClarification) => void;
  onAnswerClarification: (contestJid: string, clarificationJid: string, answer: string) => void;
}

export class ContestClarificationAnswerBox extends React.Component<ContestClarificationAnswerBoxProps> {
  render() {
    if (this.props.isBoxOpen) {
      return this.renderBox();
    } else {
      return this.renderButton();
    }
  }

  private renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon="comment" onClick={this.showBox}>
        Answer
      </Button>
    );
  };

  private showBox = () => {
    this.props.onToggleBox(this.props.clarification);
  };

  private hideBox = () => {
    this.props.onToggleBox();
  };

  private renderBox = () => {
    const props = {
      onSubmit: this.answerClarification,
      onCancel: this.hideBox,
      isLoading: this.props.isBoxLoading,
    };
    return <ContestClarificationAnswerForm {...props} />;
  };

  private answerClarification = (data: ContestClarificationAnswerFormData) => {
    this.props.onAnswerClarification(this.props.contest.jid, this.props.clarification.jid, data.answer);
  };
}
