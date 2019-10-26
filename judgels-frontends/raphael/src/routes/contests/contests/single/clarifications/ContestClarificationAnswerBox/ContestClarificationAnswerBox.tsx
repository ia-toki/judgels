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
  isEditing: boolean;
  onToggleBox: (clarification?: ContestClarification) => void;
  onAnswerClarification: (contestJid: string, clarificationJid: string, answer: string, isEdit?: boolean) => void;
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
        {this.props.isEditing ? 'Edit' : 'Answer'}
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
      isEditing: this.props.isEditing,
      initialValues: {
        answer: this.props.isEditing ? this.props.clarification.answer : '',
      },
    };
    return <ContestClarificationAnswerForm {...props} />;
  };

  private answerClarification = (data: ContestClarificationAnswerFormData) => {
    this.props.onAnswerClarification(
      this.props.contest.jid,
      this.props.clarification.jid,
      data.answer,
      this.props.isEditing
    );
  };
}
