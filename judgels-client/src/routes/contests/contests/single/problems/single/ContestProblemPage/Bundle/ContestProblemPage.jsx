import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { callAction } from '../../../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../../../modules/queries/contest';
import { useSession } from '../../../../../../../../modules/session';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../utils/title';

import * as contestSubmissionActions from '../../../../submissions/Bundle/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

export default function ContestProblemPage() {
  const { contestSlug, problemAlias } = useParams({ strict: false });
  const { token } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const { statementLanguage } = useWebPrefs();
  const [state, setState] = useState({
    defaultLanguage: undefined,
    languages: undefined,
    problem: undefined,
    latestSubmissions: undefined,
    worksheet: undefined,
  });

  const loadWorksheet = async () => {
    setState(prevState => ({
      ...prevState,
      worksheet: undefined,
    }));

    const { defaultLanguage, languages, problem, worksheet } = await callAction(
      contestProblemActions.getBundleProblemWorksheet(contest.jid, problemAlias, statementLanguage)
    );

    const latestSubmissions = await callAction(
      contestSubmissionActions.getLatestSubmissions(contest.jid, problem.alias)
    );

    setState({
      latestSubmissions,
      defaultLanguage,
      languages,
      problem,
      worksheet,
    });

    document.title = createDocumentTitle(`Problem ${problem.alias}`);
  };

  useEffect(() => {
    loadWorksheet();
  }, [statementLanguage]);

  const render = () => {
    return (
      <ContentCard>
        {renderStatementLanguageWidget()}
        {renderStatement()}
      </ContentCard>
    );
  };

  const onCreateSubmission = async (itemJid, answer) => {
    const problem = state.problem;
    return await callAction(
      contestSubmissionActions.createItemSubmission(contest.jid, problem.problemJid, itemJid, answer)
    );
  };

  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = state;
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
    const { problem, worksheet, latestSubmissions } = state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    if (!latestSubmissions) {
      return <LoadingState />;
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={latestSubmissions}
        onAnswerItem={onCreateSubmission}
        worksheet={worksheet}
      />
    );
  };

  return render();
}
