import { Alert, Button, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { OptionSelect } from '../../../../components/OptionSelect/OptionSelect';
import { gradingEngineNamesMap } from '../../../../modules/api/gradingEngine';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
import {
  problemGradingConfigQueryOptions,
  updateProblemGradingEngineMutationOptions,
} from '../../../../modules/queries/problemGrading';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemGradingEnginePage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { engine },
  } = useSuspenseQuery(problemGradingConfigQueryOptions(problemJid));

  const updateEngineMutation = useMutation(updateProblemGradingEngineMutationOptions(problemJid));

  const [selectedEngine, setSelectedEngine] = useState(engine);
  const [isConfirming, setIsConfirming] = useState(false);

  const updateEngine = async () => {
    try {
      await updateEngineMutation.mutateAsync(selectedEngine, {
        onSuccess: () => toastActions.showSuccessToast('Grading engine updated.'),
      });
    } finally {
      setIsConfirming(false);
    }
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <h4>Engine</h4>
      <Flex gap={2} alignItems="center">
        <OptionSelect
          options={Object.keys(gradingEngineNamesMap)}
          value={selectedEngine}
          getOptionName={e => gradingEngineNamesMap[e]}
          label="Grading engine"
          onChange={setSelectedEngine}
        />
        <Button
          text="Save"
          intent={Intent.PRIMARY}
          disabled={selectedEngine === engine}
          onClick={() => setIsConfirming(true)}
        />
      </Flex>
      <p>
        <small>Changing the grading engine will reset the grading config.</small>
      </p>
      <Alert
        isOpen={isConfirming}
        intent={Intent.DANGER}
        confirmButtonText="Change"
        cancelButtonText="Cancel"
        loading={updateEngineMutation.isPending}
        onConfirm={updateEngine}
        onCancel={() => setIsConfirming(false)}
      >
        Change the grading engine to {gradingEngineNamesMap[selectedEngine]}? This resets the grading config.
      </Alert>
    </Flex>
  );
}
