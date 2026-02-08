import { ChevronRight, Home } from '@blueprintjs/icons';
import { useQueryClient, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useParams } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ChapterProblemProgressTag } from '../../../../../../../../components/VerdictProgressTag/ChapterProblemProgressTag';
import { sendGAEvent } from '../../../../../../../../ga';
import { VerdictCode } from '../../../../../../../../modules/api/gabriel/verdict';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
  courseChaptersQueryOptions,
} from '../../../../../../../../modules/queries/course';
import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { createDocumentTitle } from '../../../../../../../../utils/title';
import { ChapterNavigation } from '../../resources/ChapterNavigation/ChapterNavigation';
import BundleChapterProblemPage from './Bundle/ChapterProblemPage';
import ChapterProblemProgrammingLayout from './Programming/ChapterProblemLayout';

import * as chapterProblemActions from './modules/chapterProblemActions';

import './ChapterProblemLayout.scss';

export default function ChapterProblemLayout() {
  const { courseSlug, chapterAlias, problemAlias } = useParams({ strict: false });
  const dispatch = useDispatch();
  const queryClient = useQueryClient();
  const token = useSelector(selectToken);
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(token, course.jid, chapterAlias));
  const {
    data: { data: chapters },
  } = useSuspenseQuery(courseChaptersQueryOptions(token, course.jid));
  const statementLanguage = useSelector(selectStatementLanguage);

  const [reloadKey, setReloadKey] = useState(0);
  const [state, setState] = useState({
    response: undefined,
  });

  const prevProgressRef = useRef(state.response?.progress);

  useEffect(() => {
    refreshProblem();
  }, [statementLanguage, reloadKey, problemAlias]);

  useEffect(() => {
    if (state.response) {
      checkEditorial(prevProgressRef.current, state.response.progress);
      prevProgressRef.current = state.response.progress;
    }
  }, [reloadKey, state.response]);

  const reloadProblem = () => {
    setReloadKey(k => k + 1);
    queryClient.invalidateQueries({ queryKey: courseChaptersQueryOptions(token, course.jid).queryKey });
  };

  const render = () => {
    return (
      <div className="chapter-problem-page">
        {renderHeader()}
        {renderContent()}
      </div>
    );
  };

  const refreshProblem = async () => {
    setState({
      response: undefined,
    });

    const response = await dispatch(
      chapterProblemActions.getProblemWorksheet(chapter.jid, problemAlias, statementLanguage)
    );

    setState({
      response,
    });

    document.title = createDocumentTitle(`${chapterAlias} / ${response.problem.alias}`);

    sendGAEvent({ category: 'Courses', action: 'View course problem', label: course.name });
    sendGAEvent({ category: 'Courses', action: 'View chapter problem', label: chapter.name });
    sendGAEvent({
      category: 'Courses',
      action: 'View problem',
      label: chapter.name + ': ' + problemAlias,
    });
  };

  const checkEditorial = (oldProgress, newProgress) => {
    if (
      oldProgress &&
      oldProgress.verdict !== VerdictCode.AC &&
      newProgress?.verdict == VerdictCode.AC &&
      state.response.editorial
    ) {
      const problemEditorialEl = document.querySelector('.chapter-problem-editorial');
      if (problemEditorialEl) {
        problemEditorialEl.scrollIntoView({ behavior: 'smooth' });
      }
    }
  };

  const renderHeader = () => {
    const { response } = state;
    const problemTitle = response && response.worksheet.statement.title;

    return (
      <div className="chapter-problem-page__title">
        <h3>
          <Link className="chapter-problem-page__title--link" to={`/courses/${course.slug}`}>
            <Home />
          </Link>
          &nbsp;
          <ChevronRight className="chapter-problem-page__title--chevron" size={20} />
          &nbsp;
          <Link className="chapter-problem-page__title--link" to={`/courses/${course.slug}/chapters/${chapterAlias}`}>
            {chapterAlias}
          </Link>
          &nbsp;
          <ChevronRight className="chapter-problem-page__title--chevron" size={20} />
          &nbsp;
          {problemAlias}. {problemTitle}
        </h3>

        {renderProgress()}
        {renderNavigation()}
      </div>
    );
  };

  const renderProgress = () => {
    const { response } = state;
    if (!response) {
      return null;
    }

    const { progress } = response;
    if (!progress) {
      return null;
    }

    return <ChapterProblemProgressTag verdict={progress.verdict} />;
  };

  const renderNavigation = ({ hidePrev } = { hidePrev: false }) => {
    const { response } = state;
    if (!response) {
      return null;
    }

    const { progress, previousResourcePath, nextResourcePath } = response;
    return (
      <ChapterNavigation
        courseSlug={course.slug}
        chapterAlias={chapterAlias}
        previousResourcePath={hidePrev ? null : previousResourcePath}
        nextResourcePath={nextResourcePath}
        chapters={chapters}
        disableNext={progress?.verdict !== VerdictCode.AC}
      />
    );
  };

  const renderContent = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const worksheet = response;
    const problemType = worksheet.problem?.type;

    if (problemType === ProblemType.Bundle) {
      return <BundleChapterProblemPage worksheet={worksheet} renderNavigation={renderNavigation} />;
    }

    return (
      <ChapterProblemProgrammingLayout
        worksheet={worksheet}
        renderNavigation={renderNavigation}
        reloadProblem={reloadProblem}
      />
    );
  };

  return render();
}
