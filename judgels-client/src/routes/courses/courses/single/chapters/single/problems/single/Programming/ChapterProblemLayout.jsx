import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useSelector } from 'react-redux';

import ContentWithTopbar from '../../../../../../../../../components/ContentWithTopbar/ContentWithTopbar';
import { courseBySlugQueryOptions, courseChapterQueryOptions } from '../../../../../../../../../modules/queries/course';
import { selectToken } from '../../../../../../../../../modules/session/sessionSelectors';
import { ChapterProblemContext } from '../ChapterProblemContext';
import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';

import './ChapterProblemLayout.scss';
import './ChapterProblemStatementLayout.scss';

export default function ChapterProblemLayout({ worksheet, renderNavigation }) {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(token, course.jid, chapterAlias));
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

  const basePath = `/courses/${courseSlug}/chapters/${chapter?.alias}/problems/${problemAlias}`;

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
