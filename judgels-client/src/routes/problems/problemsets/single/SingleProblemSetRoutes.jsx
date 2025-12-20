import { Button } from '@blueprintjs/core';
import { ChevronLeft, Manual } from '@blueprintjs/icons';
import { useSelector } from 'react-redux';
import { Route } from 'react-router';
import { useHistory, useParams } from 'react-router-dom';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { selectProblemSet } from '../modules/problemSetSelectors';
import ProblemSetProblemsPage from './problems/ProblemSetProblemsPage/ProblemSetProblemsPage';

import './SingleProblemSetRoutes.scss';

export default function SingleProblemSetRoutes() {
  const { problemSetSlug } = useParams();
  const history = useHistory();
  const problemSet = useSelector(selectProblemSet);

  // Optimization:
  // We wait until we get the problemSet from the backend only if the current slug is different from the persisted one.
  if (!problemSet || problemSet.slug !== problemSetSlug) {
    return <LoadingState large />;
  }

  const onClickBack = () => {
    history.push('/problems/problemsets');
  };

  const sidebarItems = [
    {
      id: '@',
      titleIcon: <Manual />,
      title: 'Problems',
      routeComponent: Route,
      component: ProblemSetProblemsPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Problemset Menu',
    items: sidebarItems,
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
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}
