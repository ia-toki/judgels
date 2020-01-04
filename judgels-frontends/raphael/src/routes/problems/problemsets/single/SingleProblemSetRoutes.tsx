import { push } from 'connected-react-router';
import * as React from 'react';
import { connect } from 'react-redux';
import { Route, RouteComponentProps, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';
import { AppState } from '../../../../modules/store';

import ProblemSetProblemsPage from './problems/ProblemSetProblemsPage/ProblemSetProblemsPage';

import { selectProblemSet } from '../modules/problemSetSelectors';

import './SingleProblemSetRoutes.css';
import { Button } from '@blueprintjs/core';

interface SingleProblemSetRoutesProps extends RouteComponentProps<{ problemSetSlug: string }> {
  problemSet?: ProblemSet;
  onClickBack: () => void;
}

const SingleProblemSetRoutes = (props: SingleProblemSetRoutesProps) => {
  const { problemSet, onClickBack } = props;

  // Optimization:
  // We wait until we get the problemSet from the backend only if the current slug is different from the persisted one.
  if (!problemSet || problemSet.slug !== props.match.params.problemSetSlug) {
    return <LoadingState large />;
  }

  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'manual',
      title: 'Problems',
      routeComponent: Route,
      component: ProblemSetProblemsPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
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
};

function createSingleProblemSetRoutes() {
  const mapStateToProps = (state: AppState) =>
    ({
      problemSet: selectProblemSet(state),
    } as Partial<SingleProblemSetRoutesProps>);

  const mapDispatchToProps = {
    onClickBack: () => push('/problems'),
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetRoutes));
}

export default createSingleProblemSetRoutes();
