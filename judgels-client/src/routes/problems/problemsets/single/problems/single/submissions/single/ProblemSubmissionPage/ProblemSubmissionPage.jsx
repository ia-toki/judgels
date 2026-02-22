import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { NotFoundError } from '../../../../../../../../../modules/api/error';
import { submissionProgrammingAPI } from '../../../../../../../../../modules/api/jerahmeel/submissionProgramming';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../../modules/queries/problemSet';
import { submissionWithSourceQueryOptions } from '../../../../../../../../../modules/queries/submissionProgramming';
import { useWebPrefs } from '../../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../../utils/title';

import * as toastActions from '../../../../../../../../../modules/toast/toastActions';

export default function ProblemSubmissionPage() {
  const { problemSetSlug, problemAlias, submissionId } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
  const { statementLanguage, isDarkMode } = useWebPrefs();

  const { data: response } = useQuery(submissionWithSourceQueryOptions(+submissionId, { language: statementLanguage }));

  const [sourceImageUrl, setSourceImageUrl] = useState(undefined);

  useEffect(() => {
    if (response) {
      const { data } = response;
      if (data.submission.problemJid !== problem.problemJid) {
        const error = new NotFoundError();
        toastActions.showErrorToast(error);
        throw error;
      }

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

    const { data: submissionWithSource, profile, problemName, problemAlias, containerName } = response;

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        sourceImageUrl={sourceImageUrl}
        profile={profile}
        problemName={problemName}
        problemAlias={problemAlias}
        problemUrl={`/problems/${problemSet.slug}/${problemAlias}`}
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
