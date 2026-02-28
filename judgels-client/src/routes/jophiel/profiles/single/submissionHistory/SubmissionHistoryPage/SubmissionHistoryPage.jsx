import { useQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { Card } from '../../../../../../components/Card/Card';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { profileSubmissionsQueryOptions } from '../../../../../../modules/queries/profile';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';

export default function SubmissionHistoryPage() {
  const { username } = useParams({ strict: false });
  const location = useLocation();
  const page = +(location.search.page || 1);

  const { data: response } = useQuery(profileSubmissionsQueryOptions(username, { page }));

  const renderSubmissions = () => {
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
      containerPathsMap,
    } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <SubmissionsTable
        submissions={submissions.page}
        canManage={config.canManage}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
      />
    );
  };

  return (
    <Card title="Submission history">
      {renderSubmissions()}
      {response && <Pagination pageSize={20} totalCount={response.data.totalCount} />}
    </Card>
  );
}
