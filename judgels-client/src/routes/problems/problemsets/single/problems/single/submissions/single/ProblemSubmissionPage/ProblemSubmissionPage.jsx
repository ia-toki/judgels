import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router';

import { ContentCard } from '../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { useBreadcrumbsPath } from '../../../../../../../../../hooks/useBreadcrumbsPath';
import { NotFoundError } from '../../../../../../../../../modules/api/error';
import { selectStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectProblemSet } from '../../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../../modules/problemSetProblemSelectors';

import * as breadcrumbsActions from '../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as toastActions from '../../../../../../../../../modules/toast/toastActions';
import * as problemSetSubmissionActions from '../../modules/problemSetSubmissionActions';

export default function ProblemSubmissionPage() {
  const { submissionId } = useParams();
  const pathname = useBreadcrumbsPath();
  const dispatch = useDispatch();
  const problemSet = useSelector(selectProblemSet);
  const problem = useSelector(selectProblemSetProblem);
  const statementLanguage = useSelector(selectStatementLanguage);

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

    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, '#' + data.submission.id));

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

    return () => {
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
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
