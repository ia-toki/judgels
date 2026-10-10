import { Callout, Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';
import { ProblemType } from '../../../../modules/api/problem';
import { problemQueryOptions } from '../../../../modules/queries/problem';

export default function ProblemLayout() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { data: problem, hasLocalChanges },
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
      path: 'grading',
      title: 'Grading',
      disabled: problem.type !== ProblemType.Programming,
    },
  ];

  return (
    <ContentCard title={`Problems › ${problem.slug}`}>
      {hasLocalChanges && (
        <Callout intent={Intent.WARNING} title="You have uncommitted changes">
          Contests and problemsets keep using the last committed version of this problem until you commit your changes.
        </Callout>
      )}
      <ContentWithTopbar items={topbarItems} basePath={`/admin/problems/${problemJid}`}>
        <Outlet />
      </ContentWithTopbar>
    </ContentCard>
  );
}
