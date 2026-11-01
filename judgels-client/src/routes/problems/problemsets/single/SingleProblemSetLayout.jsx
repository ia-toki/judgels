import { Manual } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { problemSetBySlugQueryOptions } from '../../../../modules/queries/problemSet';
import { createDocumentTitle } from '../../../../utils/title';

export default function SingleProblemSetLayout() {
  const { problemSetSlug } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));

  useEffect(() => {
    document.title = createDocumentTitle(problemSet.name);
  }, [problemSetSlug, problemSet.name]);

  const sidebarItems = [
    {
      path: '',
      titleIcon: <Manual />,
      title: 'Problems',
    },
  ];

  const contentWithSidebarProps = {
    title: 'Problemset Menu',
    items: sidebarItems,
    basePath: `/problems/${problemSet.slug}`,
    contentHeader: <h2>{problemSet.name}</h2>,
  };

  return (
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
