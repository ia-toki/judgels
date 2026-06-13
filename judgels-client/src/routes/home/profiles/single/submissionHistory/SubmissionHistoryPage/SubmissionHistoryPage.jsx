import { useQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { Card } from '../../../../../../components/Card/Card';
import CursorPagination from '../../../../../../components/CursorPagination/CursorPagination';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { profileSubmissionsQueryOptions } from '../../../../../../modules/queries/profile';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';

export default function SubmissionHistoryPage() {
  const { username } = useParams({ strict: false });
  const location = useLocation();
  const beforeId = location.search.before;
  const afterId = location.search.after;

  const { data: response } = useQuery(profileSubmissionsQueryOptions(username, { beforeId, afterId }));

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
    if (submissions.page.length === 0) {
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
      {response && (
        <CursorPagination
          data={response.data.page}
          hasPreviousPage={response.data.hasPreviousPage}
          hasNextPage={response.data.hasNextPage}
        />
      )}
    </Card>
  );
}
