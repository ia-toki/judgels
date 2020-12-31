import { Button } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import ProblemSetProblemsPage from './problems/ProblemSetProblemsPage/ProblemSetProblemsPage';

import { selectProblemSet } from '../modules/problemSetSelectors';

import './SingleProblemSetRoutes.css';

function SingleProblemSetRoutes({ match, problemSet, onClickBack }) {
  // Optimization:
  // We wait until we get the problemSet from the backend only if the current slug is different from the persisted one.
  if (!problemSet || problemSet.slug !== match.params.problemSetSlug) {
    return <LoadingState large />;
  }

  const sidebarItems = [
    {
      id: '@',
      titleIcon: 'manual',
      title: 'Problems',
      routeComponent: Route,
      component: ProblemSetProblemsPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Problemset Menu',
    items: sidebarItems,
    action: (
      <Button small icon="chevron-left" onClick={onClickBack}>
        Back
      </Button>
    ),
    contentHeader: (
      <div className="single-problemset-routes__header">
        <h2 className="single-problemset-routes__title">{problemSet.name}</h2>
        <div className="clearfix" />
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

function createSingleProblemSetRoutes() {
  const mapStateToProps = state => ({
    problemSet: selectProblemSet(state),
  });

  const mapDispatchToProps = {
    onClickBack: () => push('/problems'),
  };

  return withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetRoutes));
}

export default createSingleProblemSetRoutes();
