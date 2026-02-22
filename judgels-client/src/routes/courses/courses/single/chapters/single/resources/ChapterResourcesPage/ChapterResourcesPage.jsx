import { ChevronRight } from '@blueprintjs/icons';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useParams } from '@tanstack/react-router';

import { LoadingContentCard } from '../../../../../../../../components/LoadingContentCard/LoadingContentCard';
import { VerdictCode } from '../../../../../../../../modules/api/gabriel/verdict';
import { getLessonName } from '../../../../../../../../modules/api/sandalphon/lesson';
import { getProblemName } from '../../../../../../../../modules/api/sandalphon/problem';
import { chapterLessonsQueryOptions } from '../../../../../../../../modules/queries/chapterLesson';
import { chapterProblemsQueryOptions } from '../../../../../../../../modules/queries/chapterProblem';
import { courseBySlugQueryOptions, courseChapterQueryOptions } from '../../../../../../../../modules/queries/course';
import { ChapterLessonCard } from '../ChapterLessonCard/ChapterLessonCard';
import { ChapterProblemCard } from '../ChapterProblemCard/ChapterProblemCard';

import './ChapterResourcesPage.scss';

export default function ChapterResourcesPage() {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(course.jid, chapterAlias));

  const { data: lessonsResponse } = useQuery(chapterLessonsQueryOptions(chapter.jid));
  const { data: problemsResponse } = useQuery(chapterProblemsQueryOptions(chapter.jid));

  const lessons = lessonsResponse?.data;
  const lessonsMap = lessonsResponse?.lessonsMap;
  const problems = problemsResponse?.data;
  const problemsMap = problemsResponse?.problemsMap;
  const problemSetProblemPathsMap = problemsResponse?.problemSetProblemPathsMap;
  const problemProgressesMap = problemsResponse?.problemProgressesMap;

  const getFirstUnsolvedProblemIndex = (problems, problemProgressesMap) => {
    for (let i = problems.length - 1; i >= 0; i--) {
      const progress = problemProgressesMap[problems[i].problemJid];
      if (!progress) {
        continue;
      }
      if (progress.verdict !== VerdictCode.PND) {
        return i + 1;
      }
    }
    return 0;
  };

  const firstUnsolvedProblemIndex =
    problems && problemProgressesMap ? getFirstUnsolvedProblemIndex(problems, problemProgressesMap) : 0;

  const renderHeader = () => {
    return (
      <h3 className="chapter-resources-page__title">
        <Link className="chapter-resources-page__title--link" to={`/courses/${course.slug}`}>
          {course.name}
        </Link>
        &nbsp;
        <ChevronRight className="chapter-resources-page__title--chevron" size={20} />
        &nbsp;
        {chapterAlias}. {chapter.name}
      </h3>
    );
  };

  const renderResources = () => {
    if (!lessonsResponse || !problemsResponse) {
      return <LoadingContentCard />;
    }

    if (lessons.length === 0 && problems.length === 0) {
      return (
        <p>
          <small>No resources.</small>
        </p>
      );
    }

    const chapterProblems = problems.filter(p => !problemSetProblemPathsMap[p.problemJid]);
    const problemSetProblems = problems.filter(p => !!problemSetProblemPathsMap[p.problemJid]);

    let chapterResources = null;
    if (lessons.length > 0 || chapterProblems.length > 0) {
      chapterResources = (
        <div className="chapter-resources-page__resources">
          {lessons.map(renderLesson)}
          {chapterProblems.map(renderProblem)}
        </div>
      );
    }

    let problemSetResources = null;
    if (problemSetProblems.length > 0) {
      problemSetResources = (
        <div className="chapter-resources-page__problem-set-problems">
          <h4>Practice Problems</h4>
          <div className="chapter-resources-page__resources">
            {problemSetProblems.map((p, idx) => renderProblem(p, idx + chapterProblems.length))}
          </div>
        </div>
      );
    }

    return (
      <div className="chapter-resources-page__sections">
        {chapterResources}
        {problemSetResources}
      </div>
    );
  };

  const renderLesson = lesson => {
    const props = {
      course,
      chapterAlias,
      lesson,
      lessonName: getLessonName(lessonsMap[lesson.lessonJid], undefined),
    };
    return <ChapterLessonCard key={lesson.lessonJid} {...props} />;
  };

  const renderProblem = (problem, idx) => {
    const props = {
      course,
      chapterAlias,
      problem,
      problemName: getProblemName(problemsMap[problem.problemJid], undefined),
      problemSetProblemPaths: problemSetProblemPathsMap[problem.problemJid],
      progress: problemProgressesMap[problem.problemJid],
      isFuture: idx > firstUnsolvedProblemIndex,
    };
    return <ChapterProblemCard key={problem.problemJid} {...props} />;
  };

  return (
    <div className="chapter-resources-page">
      {renderHeader()}
      {renderResources()}
    </div>
  );
}
