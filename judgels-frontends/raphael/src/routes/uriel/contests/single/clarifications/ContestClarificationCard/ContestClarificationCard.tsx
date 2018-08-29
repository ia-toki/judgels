import { Callout, Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { UserRef } from 'components/UserRef/UserRef';
import { Profile } from 'modules/api/jophiel/profile';
import { ContestClarification } from 'modules/api/uriel/contestClarification';

import './ContestClarificationCard.css';

export interface ContestClarificationCardProps {
  clarification: ContestClarification;
  profile?: Profile;
  problemAlias?: string;
  problemName?: string;
}

export class ContestClarificationCard extends React.PureComponent<ContestClarificationCardProps> {
  render() {
    const { clarification, profile, problemAlias, problemName } = this.props;
    const topic = problemAlias ? problemAlias + '. ' + problemName : 'General';
    const by = profile ? (
      <>
        &nbsp;<small>by</small> <UserRef profile={profile} />
      </>
    ) : (
      ''
    );

    return (
      <Callout className="contest-clarification-card">
        <h4>
          {clarification.title} &nbsp; <Tag>{topic}</Tag>
        </h4>
        <p className="contest-clarification-card__info">
          <small>
            asked <FormattedRelative value={clarification.time} />
          </small>
          {by}
        </p>
        <div className="clearfix" />
        <hr />
        <div className="multiline-text">{clarification.question}</div>
        <Callout
          className="contest-clarification-card contest-clarification-card__answer"
          intent={Intent.WARNING}
          icon={null}
        >
          {this.renderAnswer()}
        </Callout>
      </Callout>
    );
  }

  private renderAnswer = () => {
    const { clarification } = this.props;
    if (!clarification.answer) {
      return (
        <p>
          <small>Not answered yet.</small>
        </p>
      );
    }
    return (
      <>
        <h4>Answer:</h4>
        <p className="contest-clarification-card__info">
          <small>
            answered <FormattedRelative value={clarification.answeredTime!} />
          </small>
        </p>
        <div className="clearfix" />
        <hr />
        <div className="multiline-text">{clarification.answer}</div>
      </>
    );
  };
}
