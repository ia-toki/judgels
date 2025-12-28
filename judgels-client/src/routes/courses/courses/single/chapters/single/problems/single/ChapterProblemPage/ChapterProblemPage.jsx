import { ChevronRight, Home } from '@blueprintjs/icons';
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useParams } from 'react-router';

import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ChapterProblemProgressTag } from '../../../../../../../../../components/VerdictProgressTag/ChapterProblemProgressTag';
import { sendGAEvent } from '../../../../../../../../../ga';
import { useBreadcrumbsPath } from '../../../../../../../../../hooks/useBreadcrumbsPath';
import { VerdictCode } from '../../../../../../../../../modules/api/gabriel/verdict';
import { ProblemType } from '../../../../../../../../../modules/api/sandalphon/problem';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { selectCourseChapters } from '../../../../modules/courseChaptersSelectors';
import { ChapterNavigation } from '../../../resources/ChapterNavigation/ChapterNavigation';
import ChapterProblemBundlePage from '../Bundle/ChapterProblemPage';
import ChapterProblemProgrammingPage from '../Programming/ChapterProblemPage';
import { selectChapterProblemReloadKey } from '../modules/chapterProblemSelectors';

import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as chapterProblemActions from '../modules/chapterProblemActions';

import './ChapterProblemPage.scss';

export default function ChapterProblemPage() {
  const { problemAlias } = useParams();
  const pathname = useBreadcrumbsPath();
  const dispatch = useDispatch();
  const course = useSelector(selectCourse);
  const chapter = useSelector(selectCourseChapter);
  const chapters = useSelector(selectCourseChapters);
  const reloadKey = useSelector(selectChapterProblemReloadKey);
  const statementLanguage = useSelector(selectStatementLanguage);

  const [state, setState] = useState({
    response: undefined,
  });

  const prevProgressRef = useRef(state.response?.progress);

  useEffect(() => {
    refreshProblem();

    return () => {
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
  }, [statementLanguage, reloadKey, problemAlias]);

  useEffect(() => {
    if (state.response) {
      checkEditorial(prevProgressRef.current, state.response.progress);
      prevProgressRef.current = state.response.progress;
    }
  }, [reloadKey, state.response]);

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

    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, chapter.alias + ' / ' + response.problem.alias));

    sendGAEvent({ category: 'Courses', action: 'View course problem', label: course.name });
    sendGAEvent({ category: 'Courses', action: 'View chapter problem', label: chapter.name });
    sendGAEvent({
      category: 'Courses',
      action: 'View problem',
      label: chapter.name + ': ' + problemAlias,
    });
  };

  const checkEditorial = (oldProgress, newProgress) => {
    if (oldProgress?.verdict !== VerdictCode.AC && newProgress?.verdict == VerdictCode.AC && state.response.editorial) {
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
          <Link className="chapter-problem-page__title--link" to={`/courses/${course.slug}/chapters/${chapter.alias}`}>
            {chapter.alias}
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
        chapterAlias={chapter.alias}
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

    const { problem } = response;
    if (problem.type === ProblemType.Programming) {
      return <ChapterProblemProgrammingPage worksheet={response} renderNavigation={renderNavigation} />;
    } else {
      return <ChapterProblemBundlePage worksheet={response} renderNavigation={renderNavigation} />;
    }
  };

  return render();
}
