import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ProblemSubmissionCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionCard/ProblemSubmissionCard.jsx';
import { ProblemSubmissionEditor } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionEditor/ProblemSubmissionEditor';
import { sendGAEvent } from '../../../../../../../../../../ga';
import { isOutputOnly } from '../../../../../../../../../../modules/api/gabriel/engine.js';
import { getGradingLanguageFamily } from '../../../../../../../../../../modules/api/gabriel/language.js';
import { callAction } from '../../../../../../../../../../modules/callAction';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
} from '../../../../../../../../../../modules/queries/course';
import { useWebPrefs } from '../../../../../../../../../../modules/webPrefs';
import { useChapterProblemContext } from '../../ChapterProblemContext';

import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';

export default function ChapterProblemWorkspacePage() {
  const { worksheet, renderNavigation, reloadProblem } = useChapterProblemContext();
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(course.jid, chapterAlias));
  const { gradingLanguage, setGradingLanguage } = useWebPrefs();

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
    setGradingLanguage(data.gradingLanguage);

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

    const submission = await callAction(
      chapterProblemSubmissionActions.createSubmission(chapter.jid, problem.problemJid, data)
    );
    return {
      submission,
      submissionUrl: `/courses/${course.slug}/chapters/${chapterAlias}/problems/${problem.alias}/submissions/${submission.id}`,
    };
  };

  const getSubmission = submissionJid => callAction(chapterProblemSubmissionActions.getSubmission(submissionJid));

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
