import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { selectCourse } from '../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';

import './ChapterProblemStatementPage.scss';

export function ChapterProblemStatementPage({ worksheet }) {
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

    return <ProblemWorksheetCard alias={problem.alias} worksheet={worksheet.worksheet} />;
  };

  return (
    <ContentCard className="chapter-programming-problem-statement-page">
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
});

export default withRouter(connect(mapStateToProps)(ChapterProblemStatementPage));
