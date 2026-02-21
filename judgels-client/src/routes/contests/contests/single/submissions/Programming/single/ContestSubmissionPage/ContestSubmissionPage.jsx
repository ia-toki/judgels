import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { contestSubmissionProgrammingAPI } from '../../../../../../../../modules/api/uriel/contestSubmissionProgramming';
import { contestBySlugQueryOptions } from '../../../../../../../../modules/queries/contest';
import { contestSubmissionWithSourceQueryOptions } from '../../../../../../../../modules/queries/contestSubmissionProgramming';
import { getToken } from '../../../../../../../../modules/session';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../utils/title';

export default function ContestSubmissionPage() {
  const { contestSlug, submissionId } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { statementLanguage } = useWebPrefs();

  const { data: response } = useQuery(
    contestSubmissionWithSourceQueryOptions(contest.jid, +submissionId, { language: statementLanguage })
  );

  if (response) {
    document.title = createDocumentTitle(`Submission #${response.data.submission.id}`);
  }

  const renderSubmission = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissionWithSource, profile, problemName, problemAlias } = response;

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile}
        problemName={problemName}
        problemAlias={problemAlias}
        problemUrl={`/contests/${contest.slug}/problems/${problemAlias}`}
        onDownload={downloadSubmission}
      />
    );
  };

  const downloadSubmission = () => {
    contestSubmissionProgrammingAPI.downloadSubmission(getToken(), response.data.submission.jid);
  };

  return (
    <ContentCard>
      <h3>Submission #{submissionId}</h3>
      <hr />
      {renderSubmission()}
    </ContentCard>
  );
}
