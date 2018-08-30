import { Callout, Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { UserRef } from 'components/UserRef/UserRef';
import { Profile } from 'modules/api/jophiel/profile';
import { ContestClarification } from 'modules/api/uriel/contestClarification';

import './ContestClarificationCard.css';

export interface ContestClarificationCardProps {
  clarification: ContestClarification;
  askerProfile?: Profile;
  answererProfile?: Profile;
  problemAlias?: string;
  problemName?: string;
}

export class ContestClarificationCard extends React.PureComponent<ContestClarificationCardProps> {
  render() {
    const { clarification, askerProfile, problemAlias, problemName } = this.props;

    const isSupervisor = !!askerProfile;

    const topic = problemAlias ? problemAlias + '. ' + problemName : 'General';
    const asker = isSupervisor && (
      <>
        &nbsp;<small>by</small> <UserRef profile={askerProfile!} />
      </>
    );

    let questionIntent: Intent = Intent.NONE;
    if (isSupervisor && !clarification.answer) {
      questionIntent = Intent.WARNING;
    }

    let answerIntent: Intent = Intent.NONE;
    if (!isSupervisor && clarification.answer) {
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
    const { clarification, answererProfile } = this.props;

    if (!clarification.answer) {
      return (
        <p>
          <small>Not answered yet.</small>
        </p>
      );
    }

    const isSupervisor = !!answererProfile;
    const answerer = isSupervisor && (
      <>
        &nbsp;<small>by</small> <UserRef profile={answererProfile!} />
      </>
    );

    return (
      <>
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
      </>
    );
  };
}
