import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { curriculumsQueryOptions } from '../../../../modules/queries/curriculum';

export default function CurriculumsPage() {
  const { data: response } = useQuery(curriculumsQueryOptions());

  const renderCurriculums = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: curriculums } = response;
    if (curriculums.length === 0) {
      return (
        <p>
          <small>No curriculums.</small>
        </p>
      );
    }

    const rows = curriculums.map(curriculum => (
      <tr key={curriculum.jid}>
        <td>
          <Link to={`/admin/curriculums/${curriculum.jid}`}>{curriculum.name}</Link>
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return <ContentCard title="Curriculum">{renderCurriculums()}</ContentCard>;
}
