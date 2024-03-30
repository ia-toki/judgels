import { Component } from 'react';
import { connect } from 'react-redux';

import { ProblemSubmissionCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionCard/ProblemSubmissionCard.jsx';
import { ProblemSubmissionEditor } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionEditor/ProblemSubmissionEditor';
import { sendGAEvent } from '../../../../../../../../../../ga';
import { isOutputOnly } from '../../../../../../../../../../modules/api/gabriel/engine.js';
import { getGradingLanguageFamily } from '../../../../../../../../../../modules/api/gabriel/language.js';
import { selectGradingLanguage } from '../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';

import * as webPrefsActions from '../../../../../../../../../../modules/webPrefs/webPrefsActions';
import * as chapterProblemActions from '../../modules/chapterProblemActions';
import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';

class ChapterProblemWorkspacePage extends Component {
  state = {
    shouldResetEditor: false,
  };

  createSubmission = async data => {
    const { worksheet, course, chapter, onCreateSubmission, onUpdateGradingLanguage } = this.props;
    const { problem } = worksheet;
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

    const submission = await onCreateSubmission(chapter.jid, problem.problemJid, data);
    return {
      submission,
      submissionUrl: `/courses/${course.slug}/chapters/${chapter.alias}/problems/${problem.alias}/submissions/${submission.id}`,
    };
  };

  resetEditor = () => {
    if (window.confirm('Are you sure to reset your code to the initial state?')) {
      this.setState({ shouldResetEditor: null }, () => {
        this.setState({ shouldResetEditor: true });
      });
    }
  };

  render() {
    const { gradingLanguage, worksheet, onGetSubmission, onReloadProblem, renderNavigation } = this.props;
    const { skeletons, lastSubmission, lastSubmissionSource } = worksheet;
    const { submissionConfig, reasonNotAllowedToSubmit } = worksheet.worksheet;
    const { shouldResetEditor } = this.state;

    if (shouldResetEditor === null) {
      return null;
    }

    if (isOutputOnly(submissionConfig.gradingEngine)) {
      return (
        <ProblemSubmissionCard
          config={submissionConfig}
          onSubmit={this.createSubmission}
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
        onSubmit={this.createSubmission}
        onReset={this.resetEditor}
        onGetSubmission={onGetSubmission}
        onReloadProblem={onReloadProblem}
        renderNavigation={renderNavigation}
      />
    );
  }
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  gradingLanguage: selectGradingLanguage(state),
});

const mapDispatchToProps = {
  onReloadProblem: chapterProblemActions.reloadProblem,
  onCreateSubmission: chapterProblemSubmissionActions.createSubmission,
  onGetSubmission: chapterProblemSubmissionActions.getSubmission,
  onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChapterProblemWorkspacePage);
