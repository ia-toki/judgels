import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useRouteMatch } from 'react-router-dom';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../../../modules/contestSelectors';

import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestSubmissionActions from '../../modules/contestSubmissionActions';

export default function ContestSubmissionPage() {
  const { submissionId } = useParams();
  const match = useRouteMatch();
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);
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

    dispatch(breadcrumbsActions.pushBreadcrumb(match.url, 'Submission #' + data.submission.id));

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

    return () => {
      dispatch(breadcrumbsActions.popBreadcrumb(match.url));
    };
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
