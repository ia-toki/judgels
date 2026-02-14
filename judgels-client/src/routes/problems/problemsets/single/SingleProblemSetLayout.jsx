import { Button } from '@blueprintjs/core';
import { ChevronLeft, Manual } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { problemSetBySlugQueryOptions } from '../../../../modules/queries/problemSet';
import { createDocumentTitle } from '../../../../utils/title';

import './SingleProblemSetLayout.scss';

export default function SingleProblemSetLayout() {
  const { problemSetSlug } = useParams({ strict: false });
  const navigate = useNavigate();
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));

  useEffect(() => {
    document.title = createDocumentTitle(problemSet.name);
  }, [problemSetSlug, problemSet.name]);

  const onClickBack = () => {
    navigate({ to: '/problems/problemsets' });
  };

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
    action: (
      <Button small icon={<ChevronLeft />} onClick={onClickBack}>
        Back
      </Button>
    ),
    contentHeader: (
      <div className="single-problemset-routes__header">
        <h2>{problemSet.name}</h2>
      </div>
    ),
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
