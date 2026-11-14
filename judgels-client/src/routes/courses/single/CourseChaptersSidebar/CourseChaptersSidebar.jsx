import { useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';
import classNames from 'classnames';

import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';
import { Sidebar } from '../../../../components/Sidebar/Sidebar';
import { courseBySlugQueryOptions } from '../../../../modules/queries/course';
import { courseChaptersQueryOptions } from '../../../../modules/queries/courseChapter';

import './CourseChaptersSidebar.scss';

export default function CourseChaptersSidebar() {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const location = useLocation();
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const {
    data: { data: courseChapters, chaptersMap, chapterProgressesMap },
  } = useSuspenseQuery(courseChaptersQueryOptions(course.jid));

  const isInChaptersPath = location.pathname.includes('/chapters/');
  const isInProblemPath = location.pathname.includes('/problems/');
  const firstUnsolvedChapterIndex = getFirstUnsolvedChapterIndex(courseChapters, chapterProgressesMap);

  const items = courseChapters.map((courseChapter, idx) => ({
    path: courseChapter.alias,
    title: `${courseChapter.alias}. ${chaptersMap[courseChapter.chapterJid].name}`,
    compactTitle: courseChapter.alias,
    trailing: renderProgress(chapterProgressesMap[courseChapter.chapterJid]),
    muted: idx > firstUnsolvedChapterIndex,
  }));

  return (
    <Sidebar
      className={classNames('course-chapters-sidebar', {
        'course-chapters-sidebar--compact': isInProblemPath,
        'course-chapters-sidebar--wide': !isInChaptersPath,
      })}
      title="Chapters Menu"
      activeItemPath={chapterAlias}
      items={items}
      compact={isInProblemPath}
      onResolveItemUrl={alias => `/courses/${course.slug}/chapters/${alias}`}
    />
  );
}

function renderProgress(progress) {
  if (!progress || progress.totalProblems === 0) {
    return null;
  }

  const { solvedProblems, totalProblems } = progress;
  return (
    <ProgressTag num={solvedProblems} denom={totalProblems}>
      {solvedProblems} / {totalProblems}
    </ProgressTag>
  );
}

function getFirstUnsolvedChapterIndex(courseChapters, chapterProgressesMap) {
  for (let i = courseChapters.length - 1; i >= 0; i--) {
    const progress = chapterProgressesMap[courseChapters[i].chapterJid];
    if (!progress) {
      continue;
    }
    if (progress.totalProblems === 0) {
      continue;
    }
    if (progress.solvedProblems > 0) {
      if (progress.solvedProblems < progress.totalProblems) {
        return i;
      } else {
        for (let j = i + 1; j < courseChapters.length; j++) {
          if (chapterProgressesMap[courseChapters[i].chapterJid]) {
            if (chapterProgressesMap[courseChapters[i].chapterJid].totalProblems > 0) {
              return j;
            }
          }
        }
        return i + 1;
      }
    }
  }
  return 0;
}
