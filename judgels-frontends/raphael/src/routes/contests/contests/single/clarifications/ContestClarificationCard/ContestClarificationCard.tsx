import { Callout, Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { Profile } from '../../../../../../modules/api/jophiel/profile';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestClarification } from '../../../../../../modules/api/uriel/contestClarification';

import { ContestClarificationAnswerBox } from '../ContestClarificationAnswerBox/ContestClarificationAnswerBox';

import './ContestClarificationCard.css';

export interface ContestClarificationCardProps {
  contest: Contest;
  clarification: ContestClarification;
  canSupervise: boolean;
  canManage: boolean;
  askerProfile?: Profile;
  answererProfile?: Profile;
  problemAlias?: string;
  problemName?: string;
  isAnswerBoxOpen: boolean;
  isAnswerBoxLoading: boolean;
  onToggleAnswerBox: (clarification?: ContestClarification) => void;
  onAnswerClarification: (contestJid: string, clarificationJid: string, answer: string, isEdit?: boolean) => void;
}

export class ContestClarificationCard extends React.PureComponent<ContestClarificationCardProps> {
  render() {
    const { clarification, canSupervise, askerProfile, problemAlias, problemName } = this.props;

    const topic = problemAlias ? problemAlias + '. ' + problemName : 'General';
    const asker = canSupervise && (
      <>
        &nbsp;<small>by</small> <UserRef profile={askerProfile!} />
      </>
    );

    let questionIntent: Intent = Intent.NONE;
    if (canSupervise && !clarification.answer) {
      questionIntent = Intent.WARNING;
    }

    let answerIntent: Intent = Intent.NONE;
    if (!canSupervise && clarification.answer) {
      answerIntent = Intent.WARNING;
    }

    return (
      <Callout className="contest-clarification-card" intent={questionIntent} icon={null}>
        <h4>
          {clarification.title} &nbsp; <Tag>{topic}</Tag>
        </h4>
        <p className="contest-clarification-card__info">
          <small>
            asked <FormattedRelative value={clarification.time} />
          </small>
          {asker}
        </p>
        <div className="clearfix" />
        <hr />
        <div className="multiline-text">{clarification.question}</div>
        <Callout
          className="contest-clarification-card contest-clarification-card__answer"
          intent={answerIntent}
          icon={null}
        >
          {this.renderAnswer()}
        </Callout>
      </Callout>
    );
  }

  private renderAnswer = () => {
    const {
      contest,
      clarification,
      canSupervise,
      canManage,
      answererProfile,
      isAnswerBoxOpen,
      isAnswerBoxLoading,
      onToggleAnswerBox,
      onAnswerClarification,
    } = this.props;

    if (!canManage && !clarification.answer) {
      return (
        <p>
          <small>Not answered yet.</small>
        </p>
      );
    }

    const answerer = canSupervise && (
      <>
        &nbsp;<small>by</small> <UserRef profile={answererProfile!} />
      </>
    );

    return (
      <>
        {clarification.answer && !isAnswerBoxOpen && <>
          <h4>Answer:</h4>
          <p className="contest-clarification-card__info">
            <small>
              answered <FormattedRelative value={clarification.answeredTime!} />
            </small>
            {answerer}
          </p>
          <div className="clearfix" />
          <hr />
          <div className="multiline-text">{clarification.answer}</div>
          <br />
        </>}
        {canManage && <ContestClarificationAnswerBox
          contest={contest}
          clarification={clarification}
          isBoxOpen={isAnswerBoxOpen}
          isBoxLoading={isAnswerBoxLoading}
          onToggleBox={onToggleAnswerBox}
          onAnswerClarification={onAnswerClarification}
          isEditing={!!clarification.answer}
        />}
      </>
    );
  };
}
