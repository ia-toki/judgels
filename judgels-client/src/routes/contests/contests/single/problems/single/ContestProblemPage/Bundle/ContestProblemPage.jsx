import { useLocation, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../../../modules/contestSelectors';

import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestSubmissionActions from '../../../../submissions/Bundle/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

export default function ContestProblemPage() {
  const { problemAlias } = useParams({ strict: false });
  const { pathname } = useLocation();
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);
  const statementLanguage = useSelector(selectStatementLanguage);
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

    const { defaultLanguage, languages, problem, worksheet } = await dispatch(
      contestProblemActions.getBundleProblemWorksheet(contest.jid, problemAlias, statementLanguage)
    );

    const latestSubmissions = await dispatch(contestSubmissionActions.getLatestSubmissions(contest.jid, problem.alias));

    setState({
      latestSubmissions,
      defaultLanguage,
      languages,
      problem,
      worksheet,
    });

    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, 'Problem ' + problem.alias));
  };

  useEffect(() => {
    loadWorksheet();

    return () => {
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
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
    return await dispatch(
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
