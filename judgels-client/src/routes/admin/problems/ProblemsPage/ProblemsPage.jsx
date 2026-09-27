import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { ProblemType } from '../../../../modules/api/problem';
import { problemsQueryOptions } from '../../../../modules/queries/problem';

const PAGE_SIZE = 20;

const problemTypeNames = {
  [ProblemType.Programming]: 'Programming',
  [ProblemType.Bundle]: 'Bundle',
};

export default function ProblemsPage() {
  const location = useLocation();
  const page = location.search.page;

  const { data: response } = useQuery(problemsQueryOptions({ page }));

  const renderProblems = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, profilesMap } = response;
    if (problems.page.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    const rows = problems.page.map(problem => (
      <tr key={problem.jid}>
        <td style={{ width: '60px' }}>{problem.id}</td>
        <td>{problem.slug}</td>
        <td style={{ width: '120px' }}>{problemTypeNames[problem.type]}</td>
        <td style={{ width: '200px' }}>
          <UserRef profile={profilesMap[problem.authorJid]} />
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th>Slug</th>
            <th style={{ width: '120px' }}>Type</th>
            <th style={{ width: '200px' }}>Author</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard title="Problems">
      {renderProblems()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
