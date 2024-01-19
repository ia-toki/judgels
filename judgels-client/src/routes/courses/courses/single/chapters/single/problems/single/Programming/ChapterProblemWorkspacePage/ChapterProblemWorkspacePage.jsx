import { connect } from 'react-redux';

import { ProblemSubmissionCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionCard/ProblemSubmissionCard.jsx';
import { ProblemSubmissionEditor } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionEditor/ProblemSubmissionEditor';
import { sendGAEvent } from '../../../../../../../../../../ga';
import { isOutputOnly } from '../../../../../../../../../../modules/api/gabriel/engine.js';
import { getGradingLanguageFamily } from '../../../../../../../../../../modules/api/gabriel/language.js';
import { selectGradingLanguage } from '../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';
import { RefreshChapterProblem } from '../../modules/chapterProblemReducer.js';
import { selectChapterProblemShouldResetEditor } from '../../modules/chapterProblemSelectors.js';

import * as webPrefsActions from '../../../../../../../../../../modules/webPrefs/webPrefsActions';
import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';

function ChapterProblemWorkspacePage({
  worksheet,
  course,
  chapter,
  shouldResetEditor,
  gradingLanguage,
  onCreateSubmission,
  onUpdateGradingLanguage,
  onRefreshChapterProblem,
}) {
  const { submissionConfig, reasonNotAllowedToSubmit } = worksheet.worksheet;
  const { problem, skeletons, lastSubmission, lastSubmissionSource } = worksheet;

  const createSubmission = async data => {
    onUpdateGradingLanguage(data.gradingLanguage);

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

    return await onCreateSubmission(course.slug, chapter.jid, chapter.alias, problem.problemJid, problem.alias, data);
  };

  const resetEditor = () => {
    if (window.confirm('Are you sure to reset your code to the initial state?')) {
      onRefreshChapterProblem({ refreshKey: new Date(), shouldResetEditor: true });
    }
  };

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
      shouldReset={shouldResetEditor}
      onReset={resetEditor}
      skeletons={skeletons}
      lastSubmission={lastSubmission}
      lastSubmissionSource={lastSubmissionSource}
      config={submissionConfig}
      onSubmit={createSubmission}
      reasonNotAllowedToSubmit={reasonNotAllowedToSubmit}
      preferredGradingLanguage={gradingLanguage}
    />
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  shouldResetEditor: selectChapterProblemShouldResetEditor(state),
  gradingLanguage: selectGradingLanguage(state),
});

const mapDispatchToProps = {
  onCreateSubmission: chapterProblemSubmissionActions.createSubmission,
  onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
  onRefreshChapterProblem: RefreshChapterProblem,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChapterProblemWorkspacePage);
