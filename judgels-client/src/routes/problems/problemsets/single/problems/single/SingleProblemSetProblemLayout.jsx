import { Button } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight, Document, Layers, ManuallyEnteredData } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, Outlet, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../../../components/FullPageLayout/FullPageLayout';
import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../modules/queries/problemSet';
import { useSession } from '../../../../../../modules/session';
import { createDocumentTitle } from '../../../../../../utils/title';
import ProblemReportWidget from './ProblemReportWidget/ProblemReportWidget';

import './SingleProblemSetProblemLayout.scss';

export default function SingleProblemSetProblemLayout() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const navigate = useNavigate();
  const { token } = useSession();
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(token, problemSet.jid, problemAlias));

  useEffect(() => {
    document.title = createDocumentTitle(`${problemSet.name} / ${problemAlias}`);
  }, [problemSet.name, problemAlias]);

  const clickBack = () => {
    navigate({ to: `/problems/${problemSet.slug}` });
  };

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
