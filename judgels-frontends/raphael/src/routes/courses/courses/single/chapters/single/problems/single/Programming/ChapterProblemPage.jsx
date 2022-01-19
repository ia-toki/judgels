import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { sendGAEvent } from '../../../../../../../../../ga';
import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { getGradingLanguageFamily } from '../../../../../../../../../modules/api/gabriel/language.js';
import { selectCourse } from '../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../modules/courseChapterSelectors';
import { selectGradingLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import * as chapterSubmissionActions from '../../../submissions/modules/chapterSubmissionActions';
import * as webPrefsActions from '../../../../../../../../../modules/webPrefs/webPrefsActions';

export function ChapterProblemPage({
  match,
  course,
  chapter,
  worksheet,
  gradingLanguage,
  onCreateSubmission,
  onUpdateGradingLanguage,
}) {
  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
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
    const { problem } = worksheet;

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet.worksheet}
        onSubmit={createSubmission}
        gradingLanguage={gradingLanguage}
      />
    );
  };

  const createSubmission = async data => {
    const { problem } = worksheet;

    onUpdateGradingLanguage(data.gradingLanguage);

    sendGAEvent({ category: 'Courses', action: 'Submit course problem', label: course.name });
    sendGAEvent({ category: 'Courses', action: 'Submit chapter problem', label: chapter.name });
    sendGAEvent({
      category: 'Courses',
      action: 'Submit problem',
      label: chapter.name + ': ' + match.params.problemAlias,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Courses',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    return await onCreateSubmission(course.slug, chapter.jid, chapter.alias, problem.problemJid, data);
  };

  return (
    <ContentCard>
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  gradingLanguage: selectGradingLanguage(state),
});
const mapDispatchToProps = {
  onCreateSubmission: chapterSubmissionActions.createSubmission,
  onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ChapterProblemPage));
