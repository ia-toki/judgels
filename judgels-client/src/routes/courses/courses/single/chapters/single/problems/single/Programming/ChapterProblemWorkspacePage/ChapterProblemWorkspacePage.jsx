import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ProblemSubmissionCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionCard/ProblemSubmissionCard.jsx';
import { ProblemSubmissionEditor } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionEditor/ProblemSubmissionEditor';
import { sendGAEvent } from '../../../../../../../../../../ga';
import { isOutputOnly } from '../../../../../../../../../../modules/api/gabriel/engine.js';
import { getGradingLanguageFamily } from '../../../../../../../../../../modules/api/gabriel/language.js';
import { courseBySlugQueryOptions } from '../../../../../../../../../../modules/queries/course';
import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';
import { selectGradingLanguage } from '../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';
import { useChapterProblemContext } from '../../ChapterProblemContext';

import * as webPrefsActions from '../../../../../../../../../../modules/webPrefs/webPrefsActions';
import * as chapterProblemActions from '../../modules/chapterProblemActions';
import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';

export default function ChapterProblemWorkspacePage() {
  const { worksheet, renderNavigation } = useChapterProblemContext();
  const { courseSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const chapter = useSelector(selectCourseChapter);
  const gradingLanguage = useSelector(selectGradingLanguage);
  const dispatch = useDispatch();

  const [state, setState] = useState({
    shouldResetEditor: false,
  });

  useEffect(() => {
    if (state.shouldResetEditor === null) {
      setState(prevState => ({ ...prevState, shouldResetEditor: true }));
    }
  }, [state.shouldResetEditor]);

  const createSubmission = async data => {
    const { problem } = worksheet;
    dispatch(webPrefsActions.updateGradingLanguage(data.gradingLanguage));

    sendGAEvent({ category: 'Courses', action: 'Submit course problem', label: course.name });
    sendGAEvent({ category: 'Courses', action: 'Submit chapter problem', label: chapter.name });
    sendGAEvent({
      category: 'Courses',
      action: 'Submit problem',
      label: chapter.name + ': ' + problem.alias,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Courses',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    const submission = await dispatch(
      chapterProblemSubmissionActions.createSubmission(chapter.jid, problem.problemJid, data)
    );
    return {
      submission,
      submissionUrl: `/courses/${course.slug}/chapters/${chapter.alias}/problems/${problem.alias}/submissions/${submission.id}`,
    };
  };

  const getSubmission = submissionJid => dispatch(chapterProblemSubmissionActions.getSubmission(submissionJid));

  const reloadProblem = () => dispatch(chapterProblemActions.reloadProblem());

  const resetEditor = () => {
    if (window.confirm('Are you sure to reset your code to the initial state?')) {
      setState(prevState => ({ ...prevState, shouldResetEditor: null }));
    }
  };

  const render = () => {
    const { skeletons, lastSubmission, lastSubmissionSource } = worksheet;
    const { submissionConfig, reasonNotAllowedToSubmit } = worksheet.worksheet;
    const { shouldResetEditor } = state;

    if (shouldResetEditor === null) {
      return null;
    }

    if (isOutputOnly(submissionConfig.gradingEngine)) {
      return (
        <ProblemSubmissionCard
          config={submissionConfig}
          onSubmit={createSubmission}
          reasonNotAllowedToSubmit={reasonNotAllowedToSubmit}
          preferredGradingLanguage={gradingLanguage}
        />
      );
    }

    return (
      <ProblemSubmissionEditor
        skeletons={skeletons}
        lastSubmission={lastSubmission}
        lastSubmissionSource={lastSubmissionSource}
        config={submissionConfig}
        reasonNotAllowedToSubmit={reasonNotAllowedToSubmit}
        preferredGradingLanguage={gradingLanguage}
        shouldReset={shouldResetEditor}
        onSubmit={createSubmission}
        onReset={resetEditor}
        onGetSubmission={getSubmission}
        onReloadProblem={reloadProblem}
        renderNavigation={renderNavigation}
      />
    );
  };

  return render();
}
