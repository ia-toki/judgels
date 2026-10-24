import { Flex } from '@blueprintjs/labs';
import { Outlet, useParams } from '@tanstack/react-router';

import ContentWithTopbar from '../../../../../../../../../components/ContentWithTopbar/ContentWithTopbar';
import { ChapterProblemContext } from '../ChapterProblemContext';
import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';

import './ChapterProblemLayout.scss';
import './ChapterProblemStatementLayout.scss';

export default function ChapterProblemLayout({ worksheet, renderNavigation, reloadProblem }) {
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
      <Flex asChild flex={1} flexDirection="column">
        <ContentWithTopbar className="chapter-problem-statement-routes" items={topbarItems} basePath={basePath} gap={2}>
          <ChapterProblemContext.Provider value={{ worksheet, renderNavigation, reloadProblem }}>
            <Outlet />
          </ChapterProblemContext.Provider>
        </ContentWithTopbar>
      </Flex>
    </div>
  );
}
