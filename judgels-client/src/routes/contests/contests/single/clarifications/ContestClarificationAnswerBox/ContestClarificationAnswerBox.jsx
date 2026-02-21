import { Button, Intent } from '@blueprintjs/core';
import { Comment } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';

import { answerContestClarificationMutationOptions } from '../../../../../../modules/queries/contestClarification';
import ContestClarificationAnswerForm from '../ContestClarificationAnswerForm/ContestClarificationAnswerForm';

export function ContestClarificationAnswerBox({ contest, clarification, isBoxOpen, onToggleBox }) {
  const answerMutation = useMutation(answerContestClarificationMutationOptions(contest.jid, clarification.jid));

  const showBox = () => {
    onToggleBox(clarification);
  };

  const hideBox = () => {
    onToggleBox();
  };

  const answerClarification = async data => {
    await answerMutation.mutateAsync(data.answer, {
      onSuccess: () => {
        onToggleBox();
      },
    });
  };

  if (isBoxOpen) {
    return (
      <ContestClarificationAnswerForm
        onSubmit={answerClarification}
        onCancel={hideBox}
        isLoading={answerMutation.isPending}
      />
    );
  } else {
    return (
      <Button intent={Intent.PRIMARY} icon={<Comment />} onClick={showBox}>
        Answer
      </Button>
    );
  }
}
