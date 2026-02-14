import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { NotFoundError } from '../../../../../../../../../modules/api/error';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../../modules/queries/problemSet';
import { selectToken } from '../../../../../../../../../modules/session/sessionSelectors';
import { useWebPrefs } from '../../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../../utils/title';

import * as toastActions from '../../../../../../../../../modules/toast/toastActions';
import * as problemSetSubmissionActions from '../../modules/problemSetSubmissionActions';

export default function ProblemSubmissionPage() {
  const { problemSetSlug, problemAlias, submissionId } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(token, problemSet.jid, problemAlias));
  const { statementLanguage } = useWebPrefs();

  const [state, setState] = useState({
    submissionWithSource: undefined,
    sourceImageUrl: undefined,
    profile: undefined,
    problemName: undefined,
    problemAlias: undefined,
    containerName: undefined,
  });

  const loadSubmission = async () => {
    const { data, profile, problemName, problemAlias, containerName } = await dispatch(
      problemSetSubmissionActions.getSubmissionWithSource(+submissionId, statementLanguage)
    );
    if (data.submission.problemJid !== problem.problemJid) {
      const error = new NotFoundError();
      toastActions.showErrorToast(error);
      throw error;
    }

    const sourceImageUrl = data.source
      ? undefined
      : await dispatch(problemSetSubmissionActions.getSubmissionSourceImage(data.submission.jid));

    document.title = createDocumentTitle(`Submission #${data.submission.id}`);

    setState({
      submissionWithSource: data,
      sourceImageUrl,
      profile,
      problemName,
      problemAlias,
      containerName,
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
    const { submissionWithSource, profile, problemName, problemAlias, containerName, sourceImageUrl } = state;
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
        problemUrl={`/problems/${problemSet.slug}/${problemAlias}`}
        containerName={containerName}
      />
    );
  };

  return render();
}
