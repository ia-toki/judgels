import { Document, Layers, ManuallyEnteredData } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { BreadcrumbTitle } from '../../../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import ContentWithSidebar from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { ProblemType } from '../../../../../../modules/api/problem';
import { problemSetBySlugQueryOptions } from '../../../../../../modules/queries/problemSet';
import { problemSetProblemQueryOptions } from '../../../../../../modules/queries/problemSetProblem';
import { createDocumentTitle } from '../../../../../../utils/title';
import ProblemReportWidget from './ProblemReportWidget/ProblemReportWidget';

export default function SingleProblemSetProblemLayout() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));

  useEffect(() => {
    document.title = createDocumentTitle(`${problemSet.name} / ${problemAlias}`);
  }, [problemSet.name, problemAlias]);

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
    contentHeader: (
      <h2>
        <BreadcrumbTitle
          items={[
            { text: problemSet.name, to: `/problems/${problemSet.slug}` },
            { text: problem.alias, to: `/problems/${problemSet.slug}/${problem.alias}` },
          ]}
        />
      </h2>
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
