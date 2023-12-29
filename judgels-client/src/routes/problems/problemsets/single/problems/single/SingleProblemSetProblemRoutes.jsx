import { Button } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight, Document, Layers, ManuallyEnteredData } from '@blueprintjs/icons';
import { push } from 'connected-react-router';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';
import { Link } from 'react-router-dom';

import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../modules/problemSetProblemSelectors';
import ProblemReportWidget from './ProblemReportWidget/ProblemReportWidget';
import ProblemItemSubmissionRoutes from './results/ProblemItemSubmissionRoutes';
import ProblemStatementPage from './statement/ProblemStatementPage/ProblemStatementPage';
import ProblemSubmissionRoutes from './submissions/ProblemSubmissionRoutes';

import './SingleProblemSetProblemRoutes.scss';

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
      titleIcon: <Document />,
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
        titleIcon: <Layers />,
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
        titleIcon: <ManuallyEnteredData />,
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
      <Button small icon={<ChevronLeft />} onClick={clickBack}>
        Back
      </Button>
    ),
    contentHeader: (
      <h3 className="single-problemset-problem-routes__title">
        <Link className="single-problemset-problem-routes__title--link" to={`/problems/${problemSet.slug}`}>
          {problemSet.name}
        </Link>
        &nbsp;
        <ChevronRight className="single-problemset-problem-routes__title--chevron" size={20} />
        &nbsp;
        {problem.alias}
      </h3>
    ),
    stickyWidget1: ProblemReportWidget,
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
