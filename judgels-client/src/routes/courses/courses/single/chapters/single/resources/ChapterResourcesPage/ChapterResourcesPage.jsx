import { ChevronRight } from '@blueprintjs/icons';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router';

import { LoadingContentCard } from '../../../../../../../../components/LoadingContentCard/LoadingContentCard';
import { VerdictCode } from '../../../../../../../../modules/api/gabriel/verdict';
import { getLessonName } from '../../../../../../../../modules/api/sandalphon/lesson';
import { getProblemName } from '../../../../../../../../modules/api/sandalphon/problem';
import { selectCourse } from '../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../modules/courseChapterSelectors';
import { ChapterLessonCard } from '../ChapterLessonCard/ChapterLessonCard';
import { ChapterProblemCard } from '../ChapterProblemCard/ChapterProblemCard';

import * as chapterResourcesActions from '../modules/chapterResourceActions';

import './ChapterResourcesPage.scss';

export default function ChapterResourcesPage() {
  const dispatch = useDispatch();
  const course = useSelector(selectCourse);
  const chapter = useSelector(selectCourseChapter);

  const [state, setState] = useState({
    response: undefined,
  });

  const refreshResources = async () => {
    setState({
      response: undefined,
    });

    const response = await dispatch(chapterResourcesActions.getResources(chapter.jid));
    const [lessonsResponse, problemsResponse] = response;
    const { data: lessons, lessonsMap } = lessonsResponse;
    const { data: problems, problemsMap, problemSetProblemPathsMap, problemProgressesMap } = problemsResponse;

    const firstUnsolvedProblemIndex = getFirstUnsolvedProblemIndex(problems, problemProgressesMap);

    setState({
      response,
      lessons,
      lessonsMap,
      problems,
      problemsMap,
      problemSetProblemPathsMap,
      problemProgressesMap,
      firstUnsolvedProblemIndex,
    });
  };

  useEffect(() => {
    refreshResources();
  }, [chapter.jid]);

  const render = () => {
    return (
      <div className="chapter-resources-page">
        {renderHeader()}
        {renderResources()}
      </div>
    );
  };

  const renderHeader = () => {
    return (
      <h3 className="chapter-resources-page__title">
        <Link className="chapter-resources-page__title--link" to={`/courses/${course.slug}`}>
          {course.name}
        </Link>
        &nbsp;
        <ChevronRight className="chapter-resources-page__title--chevron" size={20} />
        &nbsp;
        {chapter.alias}. {chapter.name}
      </h3>
    );
  };

  const renderResources = () => {
    const { response, lessons, problems, problemSetProblemPathsMap } = state;
    if (!response) {
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
    const { lessonsMap } = state;

    const props = {
      course,
      chapter,
      lesson,
      lessonName: getLessonName(lessonsMap[lesson.lessonJid], undefined),
    };
    return <ChapterLessonCard key={lesson.lessonJid} {...props} />;
  };

  const renderProblem = (problem, idx) => {
    const { problemsMap, problemSetProblemPathsMap, problemProgressesMap, firstUnsolvedProblemIndex } = state;

    const props = {
      course,
      chapter,
      problem,
      problemName: getProblemName(problemsMap[problem.problemJid], undefined),
      problemSetProblemPaths: problemSetProblemPathsMap[problem.problemJid],
      progress: problemProgressesMap[problem.problemJid],
      isFuture: idx > firstUnsolvedProblemIndex,
    };
    return <ChapterProblemCard key={problem.problemJid} {...props} />;
  };

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

  return render();
}
