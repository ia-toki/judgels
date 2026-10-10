import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { gradingEngineNamesMap } from '../../../../modules/api/gradingEngine';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import {
  autoPopulateProblemGradingConfigMutationOptions,
  problemGradingConfigQueryOptions,
  problemGradingHelperFilesQueryOptions,
  problemGradingTestDataFilesQueryOptions,
  updateProblemGradingConfigMutationOptions,
} from '../../../../modules/queries/problemGrading';
import ProblemGradingConfigEditForm from '../ProblemGradingConfigEditForm/ProblemGradingConfigEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemGradingConfigPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { config: problemConfig },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { engine, config },
  } = useSuspenseQuery(problemGradingConfigQueryOptions(problemJid));

  const {
    data: { data: testDataFiles },
  } = useSuspenseQuery(problemGradingTestDataFilesQueryOptions(problemJid));

  const {
    data: { data: helperFiles },
  } = useSuspenseQuery(problemGradingHelperFilesQueryOptions(problemJid));

  const updateConfigMutation = useMutation(updateProblemGradingConfigMutationOptions(problemJid));
  const autoPopulateConfigMutation = useMutation(autoPopulateProblemGradingConfigMutationOptions(problemJid));

  const updateConfig = async newConfig => {
    await updateConfigMutation.mutateAsync(
      { engine, config: newConfig },
      {
        onSuccess: () => toastActions.showSuccessToast('Grading config updated.'),
      }
    );
  };

  const autoPopulateConfig = async () => {
    const proposed = await autoPopulateConfigMutation.mutateAsync();
    toastActions.showSuccessToast('Test data populated. Review it, then save.');
    return proposed.config;
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Config</span>
          <small>{gradingEngineNamesMap[engine] || engine}</small>
        </h4>
      </Flex>
      {/* The form holds its own edits, so it starts over whenever the saved config changes. */}
      <ProblemGradingConfigEditForm
        key={JSON.stringify([engine, config])}
        engine={engine}
        config={config}
        testDataFiles={testDataFiles}
        helperFiles={helperFiles}
        canEdit={problemConfig.canEdit}
        onSubmit={updateConfig}
        onAutoPopulate={autoPopulateConfig}
      />
    </Flex>
  );
}
