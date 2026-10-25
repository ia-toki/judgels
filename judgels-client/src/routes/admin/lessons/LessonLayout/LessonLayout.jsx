import { Callout, H5, Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, Outlet, useParams } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';
import { lessonBySlugQueryOptions, lessonQueryOptions } from '../../../../modules/queries/lesson';
import { lessonVersionLocalChangesExistQueryOptions } from '../../../../modules/queries/lessonVersion';

export default function LessonLayout() {
  const { lessonSlug } = useParams({ strict: false });

  const {
    data: { jid: lessonJid },
  } = useSuspenseQuery(lessonBySlugQueryOptions(lessonSlug));

  const { data: lesson } = useSuspenseQuery(lessonQueryOptions(lessonJid));

  const { data: hasLocalChanges } = useSuspenseQuery(lessonVersionLocalChangesExistQueryOptions(lessonJid));

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
    },
  ];

  return (
    <ContentCard title={<BreadcrumbTitle parent="Lessons" current={lesson.slug} />}>
      {hasLocalChanges && (
        <Callout intent={Intent.WARNING}>
          <H5>
            You have <Link to={`/admin/lessons/${lessonSlug}/versions`}>uncommitted changes</Link>.
          </H5>
        </Callout>
      )}
      <ContentWithTopbar items={topbarItems} basePath={`/admin/lessons/${lessonSlug}`}>
        <Outlet />
      </ContentWithTopbar>
    </ContentCard>
  );
}
