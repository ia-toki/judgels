import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { contestBySlugQueryOptions } from '../../../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../utils/title';

import * as contestSubmissionActions from '../../../../submissions/Programming/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

import './ContestProblemPage.scss';

export default function ContestProblemPage() {
  const { contestSlug, problemAlias } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const { statementLanguage, gradingLanguage, setGradingLanguage } = useWebPrefs();

  const [state, setState] = useState({
    defaultLanguage: undefined,
    languages: undefined,
    problem: undefined,
    totalSubmissions: undefined,
    worksheet: undefined,
  });

  const loadWorksheet = async () => {
    setState(prevState => ({
      ...prevState,
      worksheet: undefined,
    }));

    const { defaultLanguage, languages, problem, totalSubmissions, worksheet } = await dispatch(
      contestProblemActions.getProgrammingProblemWorksheet(contest.jid, problemAlias, statementLanguage)
    );

    setState({
      defaultLanguage,
      languages,
      problem,
      totalSubmissions,
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

  const createSubmission = async data => {
    const problem = state.problem;
    setGradingLanguage(data.gradingLanguage);
    return await dispatch(
      contestSubmissionActions.createSubmission(contest.jid, contest.slug, problem.problemJid, data)
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
      <div className="contest-programming-problem-page__widget">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  const renderStatement = () => {
    const { problem, totalSubmissions, worksheet } = state;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    let submissionWarning;
    if (!!problem.submissionsLimit) {
      const submissionsLeft = problem.submissionsLimit - totalSubmissions;
      submissionWarning = '' + submissionsLeft + ' submissions left.';
    }

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        worksheet={worksheet}
        onSubmit={createSubmission}
        submissionWarning={submissionWarning}
        gradingLanguage={gradingLanguage}
      />
    );
  };

  return render();
}
