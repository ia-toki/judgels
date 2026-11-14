import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { VerdictCode } from '../../../../../../../../../modules/api/gradingVerdict';
import { courseBySlugQueryOptions } from '../../../../../../../../../modules/queries/course';
import { courseChapterQueryOptions } from '../../../../../../../../../modules/queries/courseChapter';
import {
  chapterLatestItemSubmissionsQueryOptions,
  createChapterItemSubmissionMutationOptions,
} from '../../../../../../../../../modules/queries/trainingItemSubmission';

export default function ChapterProblemStatementPage(props) {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const pathname = useLocation({ select: location => location.pathname });
  const navigate = useNavigate();
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(course.jid, chapterAlias));

  const isInSubmissionsPath = (pathname + '/').includes('/submissions/');
  const resultsUrl = (pathname + '/submissions').replace('//', '/');

  useEffect(() => {
    const { progress } = props.worksheet;
    if (!isInSubmissionsPath && progress && progress.verdict !== VerdictCode.PND) {
      navigate({ to: resultsUrl, replace: true });
    }
  }, [props.worksheet.problem.alias]);

  const { data: latestSubmissions } = useQuery(
    chapterLatestItemSubmissionsQueryOptions(chapter.jid, props.worksheet.problem.alias)
  );

  const createItemSubmissionMutation = useMutation(
    createChapterItemSubmissionMutationOptions(chapter.jid, props.worksheet.problem.alias)
  );

  const createSubmission = (itemJid, answer) => {
    const { problem } = props.worksheet;
    createItemSubmissionMutation.mutate({ problemJid: problem.problemJid, itemJid, answer });
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

    if (!latestSubmissions) {
      return <LoadingState />;
    }

    const reasonNotAllowedToSubmit = isInSubmissionsPath ? 'Submission received.' : worksheet.reasonNotAllowedToSubmit;

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

  return (
    <ContentCard className="chapter-problem-pane chapter-problem-pane--statement">
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}
