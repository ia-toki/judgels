import { HTMLTable, Icon } from '@blueprintjs/core';

import './ArchivesTable.scss';

export function ArchivesTable({ archives, onEditArchive }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-id">ID</th>
          <th className="col-slug">Slug</th>
          <th>Name</th>
          <th>Category</th>
          <th className="col-actions" />
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = archives.map(archive => (
      <tr key={archive.jid}>
        <td>{archive.id}</td>
        <td>{archive.slug}</td>
        <td>{archive.name}</td>
        <td>{archive.category}</td>
        <td>
          <Icon className="action" icon="edit" intent="primary" onClick={() => onEditArchive(archive)} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed archives-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
