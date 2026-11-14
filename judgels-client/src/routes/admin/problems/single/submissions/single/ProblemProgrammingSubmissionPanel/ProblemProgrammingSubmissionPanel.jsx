import { useQuery } from '@tanstack/react-query';

import { BreadcrumbTitle } from '../../../../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { problemSubmissionWithSourceByIdQueryOptions } from '../../../../../../../modules/queries/problemSubmission';

export default function ProblemProgrammingSubmissionPanel({ problem, submissionId }) {
  const { data: response } = useQuery(problemSubmissionWithSourceByIdQueryOptions(problem.jid, submissionId));

  const renderSubmission = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissionWithSource, profile } = response;

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile}
        showLoaderWhenPending
      />
    );
  };

  return (
    <div>
      <h4>
        <BreadcrumbTitle
          items={[
            { text: 'Submissions', to: `/admin/problems/${problem.slug}/submissions` },
            { text: `#${submissionId}` },
          ]}
        />
      </h4>
      {renderSubmission()}
    </div>
  );
}
