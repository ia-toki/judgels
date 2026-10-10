import { Callout, Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, Outlet, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';
import { ProblemType } from '../../../../modules/api/problem';
import { problemQueryOptions } from '../../../../modules/queries/problem';

export default function ProblemLayout() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { data: problem, hasLocalChanges, config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

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
      path: 'editorial',
      title: 'Editorial',
    },
    {
      path: 'items',
      title: 'Items',
      disabled: problem.type !== ProblemType.Bundle,
    },
    {
      path: 'grading',
      title: 'Grading',
      disabled: problem.type !== ProblemType.Programming,
    },
    {
      path: 'versions',
      title: 'Versions',
      disabled: !config.canEdit,
    },
  ];

  return (
    <ContentCard title={`Problems › ${problem.slug}`}>
      {hasLocalChanges && (
        <Callout intent={Intent.WARNING} title="You have uncommitted changes">
          Contests and problemsets keep using the last committed version of this problem until you{' '}
          <Link to={`/admin/problems/${problemJid}/versions`}>commit your changes</Link>.
        </Callout>
      )}
      <ContentWithTopbar items={topbarItems} basePath={`/admin/problems/${problemJid}`}>
        <Outlet />
      </ContentWithTopbar>
    </ContentCard>
  );
}
