import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { contestBySlugQueryOptions } from '../../../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { createDocumentTitle } from '../../../../../../../../utils/title';

import * as contestSubmissionActions from '../../modules/contestSubmissionActions';

export default function ContestSubmissionPage() {
  const { contestSlug, submissionId } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const statementLanguage = useSelector(selectStatementLanguage);

  const [state, setState] = useState({
    submissionWithSource: undefined,
    profile: undefined,
    problemName: undefined,
    problemAlias: undefined,
    containerName: undefined,
  });

  const loadSubmission = async () => {
    const { data, profile, problemName, problemAlias, containerName } = await dispatch(
      contestSubmissionActions.getSubmissionWithSource(contest.jid, +submissionId, statementLanguage)
    );

    document.title = createDocumentTitle(`Submission #${data.submission.id}`);

    setState({
      submissionWithSource: data,
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
    const { submissionWithSource, profile, problemName, problemAlias } = state;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

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
    const { submissionWithSource } = state;
    dispatch(contestSubmissionActions.downloadSubmission(submissionWithSource.submission.jid));
  };

  return render();
}
