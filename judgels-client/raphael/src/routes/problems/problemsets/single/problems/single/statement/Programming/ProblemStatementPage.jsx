import { connect } from 'react-redux';

import { sendGAEvent } from '../../../../../../../../ga';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { getGradingLanguageFamily } from '../../../../../../../../modules/api/gabriel/language.js';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import { selectGradingLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import * as problemSetSubmissionActions from '../../submissions/modules/problemSetSubmissionActions';
import * as webPrefsActions from '../../../../../../../../modules/webPrefs/webPrefsActions';

export function ProblemStatementPage({
  problemSet,
  problem,
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
    return (
      <ProblemWorksheetCard
        worksheet={worksheet.worksheet}
        onSubmit={createSubmission}
        gradingLanguage={gradingLanguage}
      />
    );
  };

  const createSubmission = async data => {
    onUpdateGradingLanguage(data.gradingLanguage);

    sendGAEvent({ category: 'Problems', action: 'Submit problemset problem', label: problemSet.name });
    sendGAEvent({
      category: 'Problems',
      action: 'Submit problem',
      label: problemSet.name + ': ' + problem.alias,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Problems',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    return await onCreateSubmission(problemSet.slug, problemSet.jid, problem.alias, worksheet.problem.problemJid, data);
  };

  return (
    <ContentCard>
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
  gradingLanguage: selectGradingLanguage(state),
});
const mapDispatchToProps = {
  onCreateSubmission: problemSetSubmissionActions.createSubmission,
  onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
};

export default connect(mapStateToProps, mapDispatchToProps)(ProblemStatementPage);
