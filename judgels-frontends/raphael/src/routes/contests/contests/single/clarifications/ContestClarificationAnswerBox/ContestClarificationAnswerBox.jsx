import { Button, Intent } from '@blueprintjs/core';
import { Comment } from '@blueprintjs/icons';

import ContestClarificationAnswerForm from '../ContestClarificationAnswerForm/ContestClarificationAnswerForm';

export function ContestClarificationAnswerBox({
  contest,
  clarification,
  isBoxOpen,
  isBoxLoading,
  onToggleBox,
  onAnswerClarification,
}) {
  const renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon={<Comment />} onClick={showBox}>
        Answer
      </Button>
    );
  };

  const showBox = () => {
    onToggleBox(clarification);
  };

  const hideBox = () => {
    onToggleBox();
  };

  const renderBox = () => {
    const props = {
      onSubmit: answerClarification,
      onCancel: hideBox,
      isLoading: isBoxLoading,
    };
    return <ContestClarificationAnswerForm {...props} />;
  };

  const answerClarification = data => {
    onAnswerClarification(contest.jid, clarification.jid, data.answer);
  };

  if (isBoxOpen) {
    return renderBox();
  } else {
    return renderButton();
  }
}
