import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { selectCourse } from '../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { ProblemEditorialCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemEditorialCard/ProblemEditorialCard';

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

  const renderEditorial = () => {
    const { problem, editorial } = worksheet;
    if (!editorial) {
      return null;
    }
    return (
      <ProblemEditorialCard alias={problem.alias} statement={worksheet.worksheet.statement} editorial={editorial} />
    );
  };

  const renderStatement = () => {
    const { problem, editorial } = worksheet;

    if (editorial) {
      return (
        <details>
          <summary>
            <small>Click to view original problem statement</small>
          </summary>
          <ProblemWorksheetCard alias={problem.alias} worksheet={worksheet.worksheet} />
        </details>
      );
    }

    return <ProblemWorksheetCard alias={problem.alias} worksheet={worksheet.worksheet} />;
  };

  return (
    <ContentCard className="chapter-programming-problem-statement-page">
      {renderStatementLanguageWidget()}
      {renderEditorial()}
      {renderStatement()}
    </ContentCard>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
});

export default withRouter(connect(mapStateToProps)(ChapterProblemStatementPage));
