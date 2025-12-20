import { Button } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight, Document, Layers, ManuallyEnteredData } from '@blueprintjs/icons';
import { useSelector } from 'react-redux';
import { Route } from 'react-router';
import { Link, useHistory, useParams } from 'react-router-dom';

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

export default function SingleProblemSetProblemRoutes() {
  const { problemSetSlug, problemAlias } = useParams();
  const history = useHistory();
  const problemSet = useSelector(selectProblemSet);
  const problem = useSelector(selectProblemSetProblem);

  const clickBack = () => {
    history.push(`/problems/${problemSet.slug}`);
  };

  // Optimization:
  // We wait until we get the problem from the backend only if the current problem is different from the persisted one.
  if (!problemSet || !problem || problemSet.slug !== problemSetSlug || problem.alias !== problemAlias) {
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
