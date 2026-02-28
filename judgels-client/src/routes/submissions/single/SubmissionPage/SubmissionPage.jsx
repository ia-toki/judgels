import { useQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { constructProblemUrl } from '../../../../modules/api/jerahmeel/submission';
import { submissionProgrammingAPI } from '../../../../modules/api/jerahmeel/submissionProgramming';
import { submissionWithSourceQueryOptions } from '../../../../modules/queries/submissionProgramming';
import { useWebPrefs } from '../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../utils/title';

export default function SubmissionPage() {
  const { submissionId } = useParams({ strict: false });
  const { statementLanguage, isDarkMode } = useWebPrefs();

  const { data: response } = useQuery(submissionWithSourceQueryOptions(+submissionId, { language: statementLanguage }));

  const [sourceImageUrl, setSourceImageUrl] = useState(undefined);

  useEffect(() => {
    if (response) {
      const { data } = response;
      document.title = createDocumentTitle(`Submission #${data.submission.id}`);

      if (!data.source) {
        submissionProgrammingAPI.getSubmissionSourceImage(data.submission.jid, isDarkMode).then(setSourceImageUrl);
      }
    }
  }, [response]);

  const renderSubmission = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissionWithSource, profile, problemName, problemAlias, containerPath, containerName } = response;

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        sourceImageUrl={sourceImageUrl}
        profile={profile}
        problemName={problemName}
        problemAlias={problemAlias}
        problemUrl={`${constructProblemUrl(containerPath, problemAlias)}`}
        containerName={containerName}
      />
    );
  };

  return (
    <ContentCard>
      <h3>Submission #{submissionId}</h3>
      <hr />
      {renderSubmission()}
    </ContentCard>
  );
}
