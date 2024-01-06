import { Intent, Tag } from '@blueprintjs/core';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { ProblemEditorialCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemEditorialCard/ProblemEditorialCard';
import { ProblemWorksheetCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { selectCourse } from '../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';

import './ChapterProblemStatementPage.scss';

function ChapterProblemStatementPage({ worksheet, renderNavigation }) {
  const renderTimeLimit = timeLimit => {
    if (!timeLimit) {
      return '-';
    }
    if (timeLimit % 1000 === 0) {
      return timeLimit / 1000 + ' s';
    }
    return timeLimit + ' ms';
  };

  const renderMemoryLimit = memoryLimit => {
    if (!memoryLimit) {
      return '-';
    }
    if (memoryLimit % 1024 === 0) {
      return memoryLimit / 1024 + ' MB';
    }
    return memoryLimit + ' KB';
  };

  const renderLimits = () => {
    const { timeLimit, memoryLimit } = worksheet.worksheet.limits;
    return (
      <small className="statement-header__limits">
        Time limit:&nbsp;&nbsp;{renderTimeLimit(timeLimit)}
        &nbsp;&nbsp;&nbsp;&bull;&nbsp;&nbsp;&nbsp;Memory limit:&nbsp;&nbsp;{renderMemoryLimit(memoryLimit)}
      </small>
    );
  };

  const renderStatementHeader = () => {
    const { defaultLanguage, languages } = worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="statement-header">
        <StatementLanguageWidget {...props} />
        {renderLimits()}
      </div>
    );
  };

  const renderReview = () => {
    const { problem, editorial } = worksheet;
    if (!editorial) {
      return null;
    }
    return (
      <>
        <Tag intent={Intent.SUCCESS} style={{ width: '100%' }}></Tag>
        <ProblemEditorialCard
          alias={problem.alias}
          statement={worksheet.worksheet.statement}
          editorial={editorial}
          titleSuffix=" (Review)"
        />
      </>
    );
  };

  const renderStatement = () => {
    const { problem, editorial } = worksheet;

    if (editorial) {
      return (
        <details>
          <summary>
            <small>Problem</small>
          </summary>
          <ProblemWorksheetCard alias={problem.alias} worksheet={worksheet.worksheet} />
        </details>
      );
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet.worksheet}
        showTitle={false}
        showLimits={false}
      />
    );
  };

  return (
    <ContentCard className="chapter-programming-problem-statement-page">
      {renderStatementHeader()}
      {renderStatement()}
      <div className="chapter-programming-problem-statement-page__footer">
        {renderReview()}
        <div className="chapter-programming-problem-statement-page__navigation">{renderNavigation()}</div>
      </div>
    </ContentCard>
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
});

export default withRouter(connect(mapStateToProps)(ChapterProblemStatementPage));
