import { Alert, Button, HTMLSelect, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { gradingEngineNamesMap } from '../../../../modules/api/gradingEngine';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
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
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

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
        <HTMLSelect
          aria-label="Grading engine"
          value={selectedEngine}
          disabled={!config.canEdit}
          onChange={e => setSelectedEngine(e.target.value)}
        >
          {Object.keys(gradingEngineNamesMap).map(e => (
            <option key={e} value={e}>
              {gradingEngineNamesMap[e]}
            </option>
          ))}
        </HTMLSelect>
        {config.canEdit && (
          <Button
            text="Save"
            intent={Intent.PRIMARY}
            disabled={selectedEngine === engine}
            onClick={() => setIsConfirming(true)}
          />
        )}
      </Flex>
      {config.canEdit && (
        <p>
          <small>Changing the grading engine resets the grading config.</small>
        </p>
      )}
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
