import { Callout, Intent, Tag } from '@blueprintjs/core';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { ContestClarificationAnswerBox } from '../ContestClarificationAnswerBox/ContestClarificationAnswerBox';

import './ContestClarificationCard.scss';

export function ContestClarificationCard({
  contest,
  clarification,
  canSupervise,
  canManage,
  askerProfile,
  answererProfile,
  problemAlias,
  problemName,
  isAnswerBoxOpen,
  isAnswerBoxLoading,
  onToggleAnswerBox,
  onAnswerClarification,
}) {
  const topic = problemAlias ? problemAlias + '. ' + problemName : 'General';
  const asker = canSupervise && (
    <>
      by <UserRef profile={askerProfile} />
    </>
  );

  let questionIntent = Intent.NONE;
  if (canSupervise && !clarification.answer) {
    questionIntent = Intent.WARNING;
  }

  let answerIntent = Intent.NONE;
  if (!canSupervise && clarification.answer) {
    answerIntent = Intent.WARNING;
  }

  const renderAnswer = () => {
    if (!clarification.answer) {
      if (canManage) {
        return (
          <ContestClarificationAnswerBox
            contest={contest}
            clarification={clarification}
            isBoxOpen={isAnswerBoxOpen}
            isBoxLoading={isAnswerBoxLoading}
            onToggleBox={onToggleAnswerBox}
            onAnswerClarification={onAnswerClarification}
          />
        );
      }

      return (
        <p>
          <small>Not answered yet.</small>
        </p>
      );
    }

    const answerer = canSupervise && (
      <>
        by <UserRef profile={answererProfile} />
      </>
    );

    return (
      <>
        <h5>Answer:</h5>
        <p className="float-right">
          <small>
            answered <FormattedRelative value={clarification.answeredTime} /> {answerer}
          </small>
        </p>
        <div className="clearfix" />
        <hr />
        <div className="multiline-text">{clarification.answer}</div>
      </>
    );
  };

  return (
    <Callout className="contest-clarification-card" intent={questionIntent} icon={null}>
      <h5>
        {clarification.title} &nbsp; <Tag>{topic}</Tag>
      </h5>
      <p className="float-right">
        <small>
          asked <FormattedRelative value={clarification.time} /> {asker}
        </small>
      </p>
      <div className="clearfix" />
      <hr />
      <div className="multiline-text">{clarification.question}</div>
      <Callout
        className="contest-clarification-card contest-clarification-card__answer"
        intent={answerIntent}
        icon={null}
      >
        {renderAnswer()}
      </Callout>
    </Callout>
  );
}
