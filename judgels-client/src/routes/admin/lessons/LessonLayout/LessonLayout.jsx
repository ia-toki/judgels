import { Callout, Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, Outlet, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';
import { lessonQueryOptions } from '../../../../modules/queries/lesson';

export default function LessonLayout() {
  const { lessonJid } = useParams({ strict: false });

  const {
    data: { data: lesson, hasLocalChanges, config },
  } = useSuspenseQuery(lessonQueryOptions(lessonJid));

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
      path: 'versions',
      title: 'Versions',
      disabled: !config.canEdit,
    },
  ];

  return (
    <ContentCard title={`Lessons › ${lesson.slug}`}>
      {hasLocalChanges && (
        <Callout intent={Intent.WARNING} title="You have uncommitted changes">
          Chapters keep using the last committed version of this lesson until you{' '}
          <Link to={`/admin/lessons/${lessonJid}/versions`}>commit your changes</Link>.
        </Callout>
      )}
      <ContentWithTopbar items={topbarItems} basePath={`/admin/lessons/${lessonJid}`}>
        <Outlet />
      </ContentWithTopbar>
    </ContentCard>
  );
}
