import { HTMLTable, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { archivesQueryOptions } from '../../../../modules/queries/archive';

export default function ArchivesPage() {
  const { data: response } = useQuery(archivesQueryOptions());

  const renderArchives = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: archives } = response;
    if (archives.length === 0) {
      return (
        <p>
          <small>No archives.</small>
        </p>
      );
    }

    const rows = archives.map(archive => (
      <tr key={archive.jid}>
        <td className="col-fit">{archive.id}</td>
        <td>
          <Link to={`/admin/archives/${archive.slug}`}>{archive.slug}</Link>
        </td>
        <td>{archive.name}</td>
        <td>{archive.category}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th className="col-fit">ID</th>
            <th className="col-wide">Slug</th>
            <th>Name</th>
            <th>Category</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  const renderAction = () => {
    return (
      <ButtonLink small intent={Intent.PRIMARY} to="/admin/archives/-/new">
        <Plus />
        <span>New archive</span>
      </ButtonLink>
    );
  };

  return (
    <ContentCard title="Archives" action={renderAction()}>
      {renderArchives()}
    </ContentCard>
  );
}
