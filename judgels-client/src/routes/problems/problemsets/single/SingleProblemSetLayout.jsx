import { Button } from '@blueprintjs/core';
import { ChevronLeft, Manual } from '@blueprintjs/icons';
import { Outlet, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { createDocumentTitle } from '../../../../utils/title';
import { selectProblemSet } from '../modules/problemSetSelectors';

import * as problemSetActions from '../modules/problemSetActions';

import './SingleProblemSetLayout.scss';

export default function SingleProblemSetLayout() {
  const { problemSetSlug } = useParams({ strict: false });
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const problemSet = useSelector(selectProblemSet);

  useEffect(() => {
    const loadProblemSet = async () => {
      const loadedProblemSet = await dispatch(problemSetActions.getProblemSetBySlug(problemSetSlug));
      document.title = createDocumentTitle(loadedProblemSet.name);
    };
    loadProblemSet();

    return () => {
      dispatch(problemSetActions.clearProblemSet());
    };
  }, [problemSetSlug]);

  // Optimization:
  // We wait until we get the problemSet from the backend only if the current slug is different from the persisted one.
  if (!problemSet || problemSet.slug !== problemSetSlug) {
    return <LoadingState large />;
  }

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
