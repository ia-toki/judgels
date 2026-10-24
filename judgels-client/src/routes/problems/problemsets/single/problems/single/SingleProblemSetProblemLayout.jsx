import { Button } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight, Document, Layers, ManuallyEnteredData } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, Outlet, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { ProblemType } from '../../../../../../modules/api/problem';
import { problemSetBySlugQueryOptions } from '../../../../../../modules/queries/problemSet';
import { problemSetProblemQueryOptions } from '../../../../../../modules/queries/problemSetProblem';
import { createDocumentTitle } from '../../../../../../utils/title';
import ProblemReportWidget from './ProblemReportWidget/ProblemReportWidget';

import './SingleProblemSetProblemLayout.scss';

export default function SingleProblemSetProblemLayout() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const navigate = useNavigate();
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));

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
      <Flex asChild alignItems="center" gap={1}>
        <h2 className="single-problemset-problem-routes__title">
          <Link className="single-problemset-problem-routes__title--link" to={`/problems/${problemSet.slug}`}>
            {problemSet.name}
          </Link>
          <ChevronRight className="single-problemset-problem-routes__title--chevron" size={20} />
          {problem.alias}
        </h2>
      </Flex>
    ),
    stickyWidget1: ProblemReportWidget,
  };

  return (
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
