import { Intent } from '@blueprintjs/core';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ButtonLink } from '../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemEditorialCard } from '../../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemEditorialCard/ProblemEditorialCard';
import { SubmissionDetails } from '../../../../../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { chapterBundleSubmissionSummaryQueryOptions } from '../../../../../../../../../../../modules/queries/chapterSubmissionBundle';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
} from '../../../../../../../../../../../modules/queries/course';
import { useSession } from '../../../../../../../../../../../modules/session';
import { useWebPrefs } from '../../../../../../../../../../../modules/webPrefs';

import './ChapterProblemSubmissionsPage.scss';

export default function ChapterProblemSubmissionsPage({ worksheet, renderNavigation }) {
  const { courseSlug, chapterAlias, problemAlias } = useParams({ strict: false });
  const { user } = useSession();
  const userJid = user?.jid;
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(course.jid, chapterAlias));
  const { statementLanguage: language } = useWebPrefs();

  const { data: summaryResponse } = useQuery({
    ...chapterBundleSubmissionSummaryQueryOptions(chapter.jid, { problemAlias, language }),
    enabled: !!userJid,
  });

  const problemSummaries = !userJid
    ? []
    : summaryResponse
      ? summaryResponse.config.problemJids.map(problemJid => ({
          name: summaryResponse.problemNamesMap[problemJid] || '-',
          alias: summaryResponse.problemAliasesMap[chapter.jid + '-' + problemJid] || '-',
          itemJids: summaryResponse.itemJidsByProblemJid[problemJid],
          submissionsByItemJid: summaryResponse.submissionsByItemJid,
          canViewGrading: true,
          canManage: false,
          itemTypesMap: summaryResponse.itemTypesMap,
        }))
      : undefined;

  const renderResults = () => {
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No quizzes.</small>;
    }
    return (
      <>
        {problemSummaries.map(props => (
          <SubmissionDetails key={props.alias} {...props} showTitle={false} />
        ))}
      </>
    );
  };

  const renderEditorial = () => {
    const { problem, editorial } = worksheet;
    if (!editorial) {
      return null;
    }
    return (
      <div className="chapter-problem-editorial">
        <hr />
        <ProblemEditorialCard
          alias={problem.alias}
          statement={worksheet.worksheet.statement}
          editorial={editorial}
          showTitle={false}
        />
      </div>
    );
  };

  const renderNavigationSection = () => {
    return <div className="chapter-problem-navigation">{renderNavigation({ hidePrev: true })}</div>;
  };

  return (
    <ContentCard className="chapter-bundle-problem-submissions-page">
      <h3 className="heading-with-button-action">Results</h3>
      <ButtonLink
        small
        intent={Intent.PRIMARY}
        to={`/courses/${course.slug}/chapters/${chapterAlias}/problems/${problemAlias}`}
      >
        Retake
      </ButtonLink>
      <hr />
      {renderResults()}
      {renderEditorial()}
      {renderNavigationSection()}
    </ContentCard>
  );
}
