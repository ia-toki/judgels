import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { problemSetsQueryOptions } from '../../../../modules/queries/problemSet';
import { ProblemSetCreateDialog } from '../ProblemSetCreateDialog/ProblemSetCreateDialog';

const PAGE_SIZE = 20;

export default function ProblemSetsPage() {
  const location = useLocation();
  const page = location.search.page;

  const { data: response } = useQuery(problemSetsQueryOptions({ page }));

  const renderProblemSets = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problemSets, archiveSlugsMap } = response;
    if (problemSets.page.length === 0) {
      return (
        <p>
          <small>No problem sets.</small>
        </p>
      );
    }

    const rows = problemSets.page.map(problemSet => (
      <tr key={problemSet.jid}>
        <td style={{ width: '60px' }}>{problemSet.id}</td>
        <td style={{ width: '200px' }}>
          <Link to={`/admin/problemsets/${problemSet.slug}`}>{problemSet.slug}</Link>
        </td>
        <td>{problemSet.name}</td>
        <td>{archiveSlugsMap[problemSet.archiveJid]}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th style={{ width: '200px' }}>Slug</th>
            <th>Name</th>
            <th>Archive</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  const renderAction = () => {
    return (
      <ActionButtons>
        <ProblemSetCreateDialog />
      </ActionButtons>
    );
  };

  return (
    <ContentCard title="Problemsets">
      {renderAction()}
      {renderProblemSets()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
