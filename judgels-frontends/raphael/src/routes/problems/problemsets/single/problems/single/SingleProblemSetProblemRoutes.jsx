import { Button } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import ProblemStatsWidget from './ProblemStatsWidget/ProblemStatsWidget';
import ProblemStatementPage from './statement/ProblemStatementPage/ProblemStatementPage';
import ProblemSubmissionRoutes from './submissions/ProblemSubmissionRoutes';
import ProblemItemSubmissionRoutes from './results/ProblemItemSubmissionRoutes';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../modules/problemSetProblemSelectors';

import './SingleProblemSetProblemRoutes.css';

function SingleProblemSetProblemRoutes({ match, problemSet, problem, onClickBack }) {
  const clickBack = () => {
    return onClickBack(problemSet.slug);
  };

  // Optimization:
  // We wait until we get the problem from the backend only if the current problem is different from the persisted one.
  if (
    !problemSet ||
    !problem ||
    problemSet.slug !== match.params.problemSetSlug ||
    problem.alias !== match.params.problemAlias
  ) {
    return <LoadingState large />;
  }

  let sidebarItems = [
    {
      id: '@',
      titleIcon: 'document',
      title: 'Statement',
      routeComponent: Route,
      component: ProblemStatementPage,
    },
  ];

  if (problem.type === ProblemType.Programming) {
    sidebarItems = [
      ...sidebarItems,
      {
        id: 'submissions',
        titleIcon: 'layers',
        title: 'Submissions',
        routeComponent: Route,
        component: ProblemSubmissionRoutes,
      },
    ];
  } else {
    sidebarItems = [
      ...sidebarItems,
      {
        id: 'results',
        titleIcon: 'manually-entered-data',
        title: 'Results',
        routeComponent: Route,
        component: ProblemItemSubmissionRoutes,
      },
    ];
  }

  const contentWithSidebarProps = {
    title: 'Problem Menu',
    items: sidebarItems,
    action: (
      <Button small icon="chevron-left" onClick={clickBack}>
        Back
      </Button>
    ),
    contentHeader: (
      <h3 className="single-problemset-problem-routes__title">
        {problemSet.name}
        <>&nbsp;&mdash;&nbsp;</>
        Problem {problem.alias}
      </h3>
    ),
    stickyWidget: problem.type === ProblemType.Programming ? ProblemStatsWidget : undefined,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
});

const mapDispatchToProps = {
  onClickBack: problemSetSlug => push(`/problems/${problemSetSlug}`),
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleProblemSetProblemRoutes));
