import { useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { callAction } from '../../../../../../../../modules/callAction';
import { problemSetBySlugQueryOptions } from '../../../../../../../../modules/queries/problemSet';

import * as problemSetSubmissionActions from '../../results/modules/problemSetSubmissionActions';

export default function ProblemStatementPage(props) {
  const { problemSetSlug } = useParams({ strict: false });
  const location = useLocation();
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));

  const [state, setState] = useState({
    latestSubmissions: undefined,
  });

  const refreshSubmissions = async () => {
    const latestSubmissions = await callAction(
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
    return await callAction(
      problemSetSubmissionActions.createItemSubmission(problemSet.jid, problem.problemJid, itemJid, answer)
    );
  };

  return render();
}
