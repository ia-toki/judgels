import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation } from 'react-router-dom';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';

import * as problemSetSubmissionActions from '../../results/modules/problemSetSubmissionActions';

export default function ProblemStatementPage(props) {
  const location = useLocation();
  const dispatch = useDispatch();
  const problemSet = useSelector(selectProblemSet);

  const [state, setState] = useState({
    latestSubmissions: undefined,
  });

  const refreshSubmissions = async () => {
    const latestSubmissions = await dispatch(
      problemSetSubmissionActions.getLatestSubmissions(problemSet.jid, props.worksheet.problem.alias)
    );
    setState({ latestSubmissions });
  };

  useEffect(() => {
    refreshSubmissions();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        {renderStatementLanguageWidget()}
        {renderStatement()}
      </ContentCard>
    );
  };

  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = props.worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const widgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <StatementLanguageWidget {...widgetProps} />
      </div>
    );
  };

  const renderStatement = () => {
    const { problem, worksheet } = props.worksheet;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    const { latestSubmissions } = state;
    if (!latestSubmissions) {
      return <LoadingState />;
    }
    const resultsUrl = (location.pathname + '/results').replace('//', '/');

    return (
      <ProblemWorksheetCard
        latestSubmissions={latestSubmissions}
        onAnswerItem={createSubmission}
        worksheet={worksheet}
        resultsUrl={resultsUrl}
      />
    );
  };

  const createSubmission = async (itemJid, answer) => {
    const { problem } = props.worksheet;
    return await dispatch(
      problemSetSubmissionActions.createItemSubmission(problemSet.jid, problem.problemJid, itemJid, answer)
    );
  };

  return render();
}
