import { Outlet, useParams } from 'react-router';

import ContentWithTopbar from '../../../../../../../../../components/ContentWithTopbar/ContentWithTopbar';

import './ChapterProblemStatementRoutes.scss';

export default function ChapterProblemStatementRoutes({ worksheet, renderNavigation }) {
  const { courseSlug, chapterAlias, problemAlias } = useParams();

  const topbarItems = [
    {
      path: '',
      title: 'Code',
    },
    {
      path: 'submissions',
      title: 'Submissions',
    },
  ];

  const basePath = `/courses/${courseSlug}/chapters/${chapterAlias}/problems/${problemAlias}`;

  return (
    <ContentWithTopbar className="chapter-problem-statement-routes" items={topbarItems} basePath={basePath}>
      <Outlet context={{ worksheet, renderNavigation }} />
    </ContentWithTopbar>
  );
}
