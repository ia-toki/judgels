import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { VerdictCode } from '../../../../../../../../../../modules/api/gabriel/verdict';
import {
  chapterBundleLatestSubmissionsQueryOptions,
  createChapterBundleItemSubmissionMutationOptions,
} from '../../../../../../../../../../modules/queries/chapterSubmissionBundle';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
} from '../../../../../../../../../../modules/queries/course';

import './ChapterProblemStatementPage.scss';

export default function ChapterProblemStatementPage(props) {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const location = useLocation();
  const navigate = useNavigate();
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(course.jid, chapterAlias));

  const isInSubmissionsPath = () => {
    return (location.pathname + '/').includes('/submissions/');
  };

  if (!isInSubmissionsPath()) {
    const { progress } = props.worksheet;
    if (progress && progress.verdict !== VerdictCode.PND) {
      const resultsUrl = (location.pathname + '/submissions').replace('//', '/');
      navigate(resultsUrl, { replace: true });
    }
  }

  const { data: latestSubmissions } = useQuery(
    chapterBundleLatestSubmissionsQueryOptions(chapter.jid, props.worksheet.problem.alias)
  );

  const createItemSubmissionMutation = useMutation(
    createChapterBundleItemSubmissionMutationOptions(chapter.jid, props.worksheet.problem.alias)
  );

  const createSubmission = async (itemJid, answer) => {
    const { problem } = props.worksheet;
    await createItemSubmissionMutation.mutateAsync({ problemJid: problem.problemJid, itemJid, answer });
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

  return (
    <ContentCard className="chapter-bundle-problem-statement-page">
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}
