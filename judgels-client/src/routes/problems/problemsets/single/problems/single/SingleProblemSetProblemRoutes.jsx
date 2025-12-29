import { Button } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight, Document, Layers, ManuallyEnteredData } from '@blueprintjs/icons';
import { useSelector } from 'react-redux';
import { Link, Outlet, useNavigate, useParams } from 'react-router';

import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import SingleProblemSetDataLayout from '../../SingleProblemSetDataLayout';
import { selectProblemSetProblem } from '../modules/problemSetProblemSelectors';
import ProblemReportWidget from './ProblemReportWidget/ProblemReportWidget';
import SingleProblemSetProblemDataLayout from './SingleProblemSetProblemDataLayout';
import ProblemItemSubmissionLayout, { problemItemSubmissionRoutes } from './results/ProblemItemSubmissionRoutes';
import ProblemStatementPage from './statement/ProblemStatementPage/ProblemStatementPage';
import ProblemSubmissionLayout, { problemSubmissionRoutes } from './submissions/ProblemSubmissionRoutes';

import './SingleProblemSetProblemRoutes.scss';

export const singleProblemSetProblemRoutes = [
  {
    index: true,
    element: <ProblemStatementPage />,
  },
  {
    path: 'submissions',
    element: <ProblemSubmissionLayout />,
    children: problemSubmissionRoutes,
  },
  {
    path: 'results',
    element: <ProblemItemSubmissionLayout />,
    children: problemItemSubmissionRoutes,
  },
];

export function SingleProblemSetProblemLayout() {
  return (
    <>
      <SingleProblemSetDataLayout />
      <SingleProblemSetProblemDataLayout />
      <MainSingleProblemSetProblemLayout />
    </>
  );
}

function MainSingleProblemSetProblemLayout() {
  const { problemSetSlug, problemAlias } = useParams();
  const navigate = useNavigate();
  const problemSet = useSelector(selectProblemSet);
  const problem = useSelector(selectProblemSetProblem);

  const clickBack = () => {
    navigate(`/problems/${problemSet.slug}`);
  };

  // Optimization:
  // We wait until we get the problem from the backend only if the current problem is different from the persisted one.
  if (!problemSet || !problem || problemSet.slug !== problemSetSlug || problem.alias !== problemAlias) {
    return <LoadingState large />;
  }

  const sidebarItems = [
    {
      path: '',
      titleIcon: <Document />,
      title: 'Statement',
    },
    ...(problem.type === ProblemType.Programming
      ? [
          {
            path: 'submissions',
            titleIcon: <Layers />,
            title: 'Submissions',
          },
        ]
      : [
          {
            path: 'results',
            titleIcon: <ManuallyEnteredData />,
            title: 'Results',
          },
        ]),
  ];

  const contentWithSidebarProps = {
    title: 'Problem Menu',
    items: sidebarItems,
    basePath: `/problems/${problemSetSlug}/${problemAlias}`,
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
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
