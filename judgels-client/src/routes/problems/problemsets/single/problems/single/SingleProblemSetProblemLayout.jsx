import { Button } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight, Document, Layers, ManuallyEnteredData } from '@blueprintjs/icons';
import { Link, Outlet, useLocation, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { selectProblemSet } from '../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../modules/problemSetProblemSelectors';
import ProblemReportWidget from './ProblemReportWidget/ProblemReportWidget';

import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as problemSetActions from '../../../modules/problemSetActions';
import * as problemSetProblemActions from '../modules/problemSetProblemActions';

import './SingleProblemSetProblemLayout.scss';

export default function SingleProblemSetProblemLayout() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const { pathname } = useLocation();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const problemSet = useSelector(selectProblemSet);
  const problem = useSelector(selectProblemSetProblem);

  // Load problem set
  useEffect(() => {
    const loadProblemSet = async () => {
      const loadedProblemSet = await dispatch(problemSetActions.getProblemSetBySlug(problemSetSlug));
      dispatch(breadcrumbsActions.pushBreadcrumb(pathname, loadedProblemSet.name));
    };
    loadProblemSet();

    return () => {
      dispatch(problemSetActions.clearProblemSet());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
  }, [problemSetSlug]);

  // Load problem (depends on problemSet being loaded)
  useEffect(() => {
    if (!problemSet || problemSet.slug !== problemSetSlug) {
      return;
    }

    const loadProblem = async () => {
      await dispatch(problemSetProblemActions.getProblem(problemSet.jid, problemAlias));
      dispatch(breadcrumbsActions.pushBreadcrumb(pathname + '/' + problemAlias, problemAlias));
    };
    loadProblem();

    return () => {
      dispatch(problemSetProblemActions.clearProblem());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname + '/' + problemAlias));
    };
  }, [problemSet?.jid, problemAlias]);

  const clickBack = () => {
    navigate({ to: `/problems/${problemSet.slug}` });
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
