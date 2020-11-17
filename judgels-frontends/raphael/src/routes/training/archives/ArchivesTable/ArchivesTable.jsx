import { HTMLTable, Icon } from '@blueprintjs/core';
import * as React from 'react';

import { Archive } from '../../../../modules/api/jerahmeel/archive';

import './ArchivesTable.css';

export interface ArchivesTableProps {
  archives: Archive[];
  onEditArchive: (archive: Archive) => any;
}

export class ArchivesTable extends React.PureComponent<ArchivesTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed archives-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
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

  private renderRows = () => {
    const { archives } = this.props;

    const rows = archives.map(archive => (
      <tr key={archive.jid}>
        <td>{archive.id}</td>
        <td>{archive.slug}</td>
        <td>{archive.name}</td>
        <td>{archive.category}</td>
        <td>
          <Icon className="action" icon="edit" intent="primary" onClick={() => this.props.onEditArchive(archive)} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
