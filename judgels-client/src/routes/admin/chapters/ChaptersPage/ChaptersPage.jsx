import { HTMLTable, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { chaptersQueryOptions } from '../../../../modules/queries/chapter';

export default function ChaptersPage() {
  const { data: response } = useQuery(chaptersQueryOptions());

  const renderChapters = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: chapters } = response;
    if (chapters.length === 0) {
      return (
        <p>
          <small>No chapters.</small>
        </p>
      );
    }

    const rows = chapters.map(chapter => (
      <tr key={chapter.jid}>
        <td className="col-fit">{chapter.id}</td>
        <td>
          <Link to={`/admin/chapters/${chapter.jid}`}>{chapter.jid}</Link>
        </td>
        <td>{chapter.name}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th className="col-fit">ID</th>
            <th className="col-wide">JID</th>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  const renderAction = () => {
    return (
      <ButtonLink small intent={Intent.PRIMARY} to="/admin/chapters/-/new">
        <Plus />
        <span>New chapter</span>
      </ButtonLink>
    );
  };

  return (
    <ContentCard title="Chapters" action={renderAction()}>
      {renderChapters()}
    </ContentCard>
  );
}
