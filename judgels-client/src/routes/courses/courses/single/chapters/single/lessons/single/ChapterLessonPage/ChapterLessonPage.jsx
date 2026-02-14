import { ChevronRight } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LessonStatementCard } from '../../../../../../../../../components/LessonStatementCard/LessonStatementCard';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
  courseChaptersQueryOptions,
} from '../../../../../../../../../modules/queries/course';
import { selectToken } from '../../../../../../../../../modules/session/sessionSelectors';
import { useWebPrefs } from '../../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../../utils/title';
import { ChapterNavigation } from '../../../resources/ChapterNavigation/ChapterNavigation';

import * as chapterLessonActions from '../modules/chapterLessonActions';

import './ChapterLessonPage.scss';

export default function ChapterLessonPage() {
  const { courseSlug, chapterAlias, lessonAlias } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(token, course.jid, chapterAlias));
  const {
    data: { data: chapters },
  } = useSuspenseQuery(courseChaptersQueryOptions(token, course.jid));
  const { statementLanguage } = useWebPrefs();

  const [state, setState] = useState({
    response: undefined,
  });

  const refreshLesson = async () => {
    const response = await dispatch(
      chapterLessonActions.getLessonStatement(chapter.jid, lessonAlias, statementLanguage)
    );

    setState({
      response,
    });

    document.title = createDocumentTitle(`${chapterAlias} / ${response.lesson.alias}`);
  };

  useEffect(() => {
    refreshLesson();
  }, [statementLanguage, lessonAlias]);

  const render = () => {
    return (
      <div className="chapter-lesson-page">
        {renderHeader()}
        <ContentCard>
          {renderStatementLanguageWidget()}
          {renderStatement()}
        </ContentCard>
      </div>
    );
  };

  const renderHeader = () => {
    return (
      <div className="chapter-lesson-page__title">
        <h3>
          <Link className="chapter-lesson-page__title--link" to={`/courses/${course.slug}`}>
            {course.name}
          </Link>
          &nbsp;
          <ChevronRight className="chapter-lesson-page__title--chevron" size={20} />
          &nbsp;
          <Link className="chapter-lesson-page__title--link" to={`/courses/${course.slug}/chapters/${chapterAlias}`}>
            {chapterAlias}. {chapter.name}
          </Link>
          &nbsp;
          <ChevronRight className="chapter-lesson-page__title--chevron" size={20} />
          &nbsp;
          {lessonAlias}
        </h3>

        {renderPrevAndNextResourcePaths()}
      </div>
    );
  };

  const renderStatementLanguageWidget = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    const { defaultLanguage, languages } = response;
    const props = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  const renderStatement = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    return <LessonStatementCard alias={response.lesson.alias} statement={response.statement} />;
  };

  const renderPrevAndNextResourcePaths = () => {
    const { response } = state;
    if (!response) {
      return null;
    }

    const { previousResourcePath, nextResourcePath } = response;
    return (
      <ChapterNavigation
        courseSlug={course.slug}
        chapterAlias={chapterAlias}
        previousResourcePath={previousResourcePath}
        nextResourcePath={nextResourcePath}
        chapters={chapters}
      />
    );
  };

  return render();
}
