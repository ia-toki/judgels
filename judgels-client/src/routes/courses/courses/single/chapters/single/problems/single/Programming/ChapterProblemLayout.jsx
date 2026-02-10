import { Outlet, useParams } from '@tanstack/react-router';

import ContentWithTopbar from '../../../../../../../../../components/ContentWithTopbar/ContentWithTopbar';
import { ChapterProblemContext } from '../ChapterProblemContext';
import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';

import './ChapterProblemLayout.scss';
import './ChapterProblemStatementLayout.scss';

export default function ChapterProblemLayout({ worksheet, renderNavigation }) {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const problemAlias = worksheet?.problem?.alias;

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
    <div className="chapter-programming-problem-page">
      <ChapterProblemStatementPage worksheet={worksheet} />
      <ContentWithTopbar className="chapter-problem-statement-routes" items={topbarItems} basePath={basePath}>
        <ChapterProblemContext.Provider value={{ worksheet, renderNavigation }}>
          <Outlet />
        </ChapterProblemContext.Provider>
      </ContentWithTopbar>
    </div>
  );
}
