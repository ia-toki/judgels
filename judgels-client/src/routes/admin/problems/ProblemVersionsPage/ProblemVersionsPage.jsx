import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  commitProblemVersionLocalChangesMutationOptions,
  discardProblemVersionLocalChangesMutationOptions,
  problemVersionsQueryOptions,
  rebaseProblemVersionLocalChangesMutationOptions,
  restoreProblemVersionMutationOptions,
} from '../../../../modules/queries/problemVersion';
import VersionsPanel from '../../catalog/VersionsPanel/VersionsPanel';

export default function ProblemVersionsPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { hasLocalChanges },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { data: versions, profilesMap },
  } = useSuspenseQuery(problemVersionsQueryOptions(problemJid));

  const commitLocalChangesMutation = useMutation(commitProblemVersionLocalChangesMutationOptions(problemJid));
  const rebaseLocalChangesMutation = useMutation(rebaseProblemVersionLocalChangesMutationOptions(problemJid));
  const discardLocalChangesMutation = useMutation(discardProblemVersionLocalChangesMutationOptions(problemJid));
  const restoreVersionMutation = useMutation(restoreProblemVersionMutationOptions(problemJid));

  return (
    <VersionsPanel
      versions={versions}
      profilesMap={profilesMap}
      hasLocalChanges={hasLocalChanges}
      onCommitLocalChanges={commitLocalChangesMutation.mutateAsync}
      onRebaseLocalChanges={rebaseLocalChangesMutation.mutateAsync}
      onDiscardLocalChanges={discardLocalChangesMutation.mutateAsync}
      onRestoreVersion={restoreVersionMutation.mutateAsync}
    />
  );
}
