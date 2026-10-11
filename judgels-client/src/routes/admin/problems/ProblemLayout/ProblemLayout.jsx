import { Callout, Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, Outlet, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';
import { ProblemType } from '../../../../modules/api/problem';
import {
  problemBySlugQueryOptions,
  problemPermissionsQueryOptions,
  problemQueryOptions,
} from '../../../../modules/queries/problem';
import { problemVersionLocalChangesExistQueryOptions } from '../../../../modules/queries/problemVersion';

export default function ProblemLayout() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const { data: problem } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { canEdit, canManage },
  } = useSuspenseQuery(problemPermissionsQueryOptions(problemJid));

  const { data: hasLocalChanges } = useSuspenseQuery(problemVersionLocalChangesExistQueryOptions(problemJid));

  const topbarItems = [
    {
      path: 'general',
      title: 'General',
    },
    {
      path: 'statements',
      title: 'Statements',
    },
    {
      path: 'languages',
      title: 'Languages',
    },
    {
      path: 'media',
      title: 'Media',
    },
    {
      path: 'grading',
      title: 'Grading',
      disabled: problem.type !== ProblemType.Programming,
    },
    {
      path: 'items',
      title: 'Items',
      disabled: problem.type !== ProblemType.Bundle,
    },
    {
      path: 'submissions',
      title: 'Submissions',
    },
    {
      path: 'partners',
      title: 'Partners',
      disabled: !canManage,
    },
    {
      path: 'editorial',
      title: 'Editorial',
    },
    {
      path: 'versions',
      title: 'Versions',
      disabled: !canEdit,
    },
  ];

  return (
    <ContentCard title={`Problems › ${problem.slug}`}>
      {hasLocalChanges && (
        <Callout intent={Intent.WARNING} title="You have uncommitted changes">
          Contests and problemsets keep using the last committed version of this problem until you{' '}
          <Link to={`/admin/problems/${problemSlug}/versions`}>commit your changes</Link>.
        </Callout>
      )}
      <ContentWithTopbar items={topbarItems} basePath={`/admin/problems/${problemSlug}`}>
        <Outlet />
      </ContentWithTopbar>
    </ContentCard>
  );
}
