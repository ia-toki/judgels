import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { lessonQueryOptions } from '../../../../modules/queries/lesson';
import {
  commitLessonVersionLocalChangesMutationOptions,
  discardLessonVersionLocalChangesMutationOptions,
  lessonVersionsQueryOptions,
  rebaseLessonVersionLocalChangesMutationOptions,
  restoreLessonVersionMutationOptions,
} from '../../../../modules/queries/lessonVersion';
import VersionsPanel from '../../catalog/VersionsPanel/VersionsPanel';

export default function LessonVersionsPage() {
  const { lessonJid } = useParams({ strict: false });

  const {
    data: { hasLocalChanges },
  } = useSuspenseQuery(lessonQueryOptions(lessonJid));

  const {
    data: { data: versions, profilesMap },
  } = useSuspenseQuery(lessonVersionsQueryOptions(lessonJid));

  const commitLocalChangesMutation = useMutation(commitLessonVersionLocalChangesMutationOptions(lessonJid));
  const rebaseLocalChangesMutation = useMutation(rebaseLessonVersionLocalChangesMutationOptions(lessonJid));
  const discardLocalChangesMutation = useMutation(discardLessonVersionLocalChangesMutationOptions(lessonJid));
  const restoreVersionMutation = useMutation(restoreLessonVersionMutationOptions(lessonJid));

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
