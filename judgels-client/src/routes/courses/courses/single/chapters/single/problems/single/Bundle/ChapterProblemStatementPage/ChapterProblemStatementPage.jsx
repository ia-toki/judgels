import { useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { VerdictCode } from '../../../../../../../../../../modules/api/gabriel/verdict';
import { callAction } from '../../../../../../../../../../modules/callAction';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
} from '../../../../../../../../../../modules/queries/course';
import { useSession } from '../../../../../../../../../../modules/session';

import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';

import './ChapterProblemStatementPage.scss';

export default function ChapterProblemStatementPage(props) {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const location = useLocation();
  const navigate = useNavigate();
  const { token } = useSession();
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(token, course.jid, chapterAlias));

  const [state, setState] = useState({
    latestSubmissions: undefined,
  });

  const loadLatestSubmissions = async () => {
    if (!isInSubmissionsPath()) {
      const { progress } = props.worksheet;
      if (progress && progress.verdict !== VerdictCode.PND) {
        const resultsUrl = (location.pathname + '/submissions').replace('//', '/');
        navigate(resultsUrl, { replace: true });
      }
    }

    const latestSubmissions = await callAction(
      chapterProblemSubmissionActions.getLatestSubmissions(chapter.jid, props.worksheet.problem.alias)
    );
    setState({
      latestSubmissions,
    });
  };

  useEffect(() => {
    loadLatestSubmissions();
  }, []);

  const render = () => {
    return (
      <ContentCard className="chapter-bundle-problem-statement-page">
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

    const reasonNotAllowedToSubmit = isInSubmissionsPath()
      ? 'Submission received.'
      : worksheet.reasonNotAllowedToSubmit;

    const resultsUrl = (location.pathname + '/submissions').replace('//', '/');

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={latestSubmissions}
        onAnswerItem={createSubmission}
        worksheet={{ ...worksheet, reasonNotAllowedToSubmit }}
        showTitle={false}
        resultsUrl={resultsUrl}
      />
    );
  };

  const createSubmission = async (itemJid, answer) => {
    const { problem } = props.worksheet;
    return await callAction(
      chapterProblemSubmissionActions.createItemSubmission(chapter.jid, problem.problemJid, itemJid, answer)
    );
  };

  const isInSubmissionsPath = () => {
    return (location.pathname + '/').includes('/submissions/');
  };

  return render();
}
