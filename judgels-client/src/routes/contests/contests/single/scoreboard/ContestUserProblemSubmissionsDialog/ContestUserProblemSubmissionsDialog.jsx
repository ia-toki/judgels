import { Classes, Dialog } from '@blueprintjs/core';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { GradingVerdictTag } from '../../../../../../components/GradingVerdictTag/GradingVerdictTag';
import { SubmissionDetails } from '../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import {
  contestSubmissionWithSourceQueryOptions,
  contestUserProblemSubmissionsQueryOptions,
} from '../../../../../../modules/queries/contestSubmissionProgramming';
import { queryClient } from '../../../../../../modules/queryClient';
import { useWebPrefs } from '../../../../../../modules/webPrefs';

import './ContestUserProblemSubmissionsDialog.scss';

export default function ContestUserProblemSubmissionsDialog({ userJid, problemJid, title, onClose }) {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { statementLanguage } = useWebPrefs();

  const { data: response } = useQuery(contestUserProblemSubmissionsQueryOptions(contest.jid, userJid, problemJid));

  const [submissionSourcesById, setSubmissionSourcesById] = useState({});

  const submissions = response?.data;
  const latestSubmissionSource = response?.latestSubmissionSource;

  const loadSubmissionSource = async submissionId => {
    setSubmissionSourcesById(prev => ({ ...prev, [submissionId]: null }));

    const submissionWithSource = await queryClient.fetchQuery(
      contestSubmissionWithSourceQueryOptions(contest.jid, submissionId, { language: statementLanguage })
    );

    setSubmissionSourcesById(prev => ({ ...prev, [submissionId]: submissionWithSource.data.source }));
  };

  const getSource = (submissionId, idx) => {
    if (submissionSourcesById[submissionId] !== undefined) {
      return submissionSourcesById[submissionId];
    }
    if (idx === 0 && latestSubmissionSource) {
      return latestSubmissionSource;
    }
    return undefined;
  };

  const renderContent = () => {
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
              source={getSource(submission.id, idx)}
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
