import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { constructProblemUrl } from '../../../../modules/api/jerahmeel/submission';
import { callAction } from '../../../../modules/callAction';
import { useWebPrefs } from '../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../utils/title';

import * as submissionActions from '../../modules/submissionActions';

export default function SubmissionPage() {
  const { submissionId } = useParams({ strict: false });
  const { statementLanguage } = useWebPrefs();

  const [state, setState] = useState({
    submissionWithSource: undefined,
    profile: undefined,
    problemName: undefined,
    problemAlias: undefined,
    containerPath: undefined,
    containerName: undefined,
    sourceImageUrl: undefined,
  });

  const loadSubmission = async () => {
    const { data, profile, problemName, problemAlias, containerPath, containerName } = await callAction(
      submissionActions.getSubmissionWithSource(+submissionId, statementLanguage)
    );
    const sourceImageUrl = data.source
      ? undefined
      : await callAction(submissionActions.getSubmissionSourceImage(data.submission.jid));

    document.title = createDocumentTitle(`Submission #${data.submission.id}`);

    setState({
      submissionWithSource: data,
      profile,
      problemName,
      problemAlias,
      containerPath,
      containerName,
      sourceImageUrl,
    });
  };

  useEffect(() => {
    loadSubmission();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3>Submission #{submissionId}</h3>
        <hr />
        {renderSubmission()}
      </ContentCard>
    );
  };

  const renderSubmission = () => {
    const { submissionWithSource, profile, problemAlias, problemName, containerPath, containerName, sourceImageUrl } =
      state;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

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

  return render();
}
