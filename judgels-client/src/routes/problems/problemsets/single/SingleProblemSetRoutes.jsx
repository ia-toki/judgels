import { Button } from '@blueprintjs/core';
import { ChevronLeft, Manual } from '@blueprintjs/icons';
import { useSelector } from 'react-redux';
import { Outlet, useNavigate, useParams } from 'react-router';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { selectProblemSet } from '../modules/problemSetSelectors';
import SingleProblemSetDataLayout from './SingleProblemSetDataLayout';
import ProblemSetProblemsPage from './problems/ProblemSetProblemsPage/ProblemSetProblemsPage';

import './SingleProblemSetRoutes.scss';

export const singleProblemSetRoutes = [
  {
    index: true,
    element: <ProblemSetProblemsPage />,
  },
];

export function SingleProblemSetLayout() {
  return (
    <>
      <SingleProblemSetDataLayout />
      <MainSingleProblemSetLayout />
    </>
  );
}

function MainSingleProblemSetLayout() {
  const { problemSetSlug } = useParams();
  const navigate = useNavigate();
  const problemSet = useSelector(selectProblemSet);

  // Optimization:
  // We wait until we get the problemSet from the backend only if the current slug is different from the persisted one.
  if (!problemSet || problemSet.slug !== problemSetSlug) {
    return <LoadingState large />;
  }

  const onClickBack = () => {
    navigate('/problems/problemsets');
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
