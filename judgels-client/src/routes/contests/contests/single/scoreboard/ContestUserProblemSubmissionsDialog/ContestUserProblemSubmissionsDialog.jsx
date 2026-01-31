import { Classes, Dialog } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { GradingVerdictTag } from '../../../../../../components/GradingVerdictTag/GradingVerdictTag';
import { SubmissionDetails } from '../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';

import * as contestScoreboardActions from '../modules/contestScoreboardActions';

import './ContestUserProblemSubmissionsDialog.scss';

export default function ContestUserProblemSubmissionsDialog({ userJid, problemJid, title, onClose }) {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();
  const statementLanguage = useSelector(selectStatementLanguage);

  const [state, setState] = useState({
    submissions: undefined,
    submissionSourcesById: {},
  });

  const refreshUserProblemSubmissions = async () => {
    const response = await dispatch(
      contestScoreboardActions.getUserProblemSubmissions(contest.jid, userJid, problemJid)
    );

    const { data: submissions, latestSubmissionSource } = response;

    setState({
      submissions,
    });

    if (submissions.length > 0) {
      setState({
        submissions,
        submissionSourcesById: {
          [submissions[0].id]: latestSubmissionSource,
        },
      });
    }
  };

  useEffect(() => {
    refreshUserProblemSubmissions();
  }, []);

  const loadSubmissionSource = async submissionId => {
    setState(prevState => ({
      submissionSourcesById: {
        ...prevState.submissionSourcesById,
        [submissionId]: null,
      },
    }));

    const submissionWithSource = await dispatch(
      contestScoreboardActions.getSubmissionWithSource(contest.jid, submissionId, statementLanguage)
    );

    setState(prevState => ({
      submissionSourcesById: {
        ...prevState.submissionSourcesById,
        [submissionId]: submissionWithSource.data.source,
      },
    }));
  };

  const renderContent = () => {
    const { submissions } = state;
    if (!submissions) {
      return null;
    }

    return submissions.map((submission, idx) => (
      <ContentCard key={submission.id}>
        <details open={idx === 0}>
          <summary>
            <h5>
              <span className="details-heading">Submission #{submission.id}</span>
              <GradingVerdictTag grading={submission.latestGrading} />
            </h5>
          </summary>

          <div className="details-content">
            <SubmissionDetails
              submission={submission}
              source={state.submissionSourcesById[submission.id]}
              onClickViewSource={() => loadSubmissionSource(submission.id)}
            />
          </div>
        </details>
      </ContentCard>
    ));
  };

  return (
    <Dialog
      className="contest-user-problem-submissions-dialog"
      isOpen
      onClose={onClose}
      title={title}
      canOutsideClickClose={true}
      enforceFocus={true}
    >
      <div className={Classes.DIALOG_BODY}>{renderContent()}</div>
    </Dialog>
  );
}
