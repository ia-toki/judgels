import { HTMLTable, Icon } from '@blueprintjs/core';
import prettyBytes from 'pretty-bytes';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestFile, contestFileAPI } from '../../../../../../modules/api/uriel/contestFile';

import './ContestFilesTable.css';

export interface ContestFilesTableProps {
  contest: Contest;
  files: ContestFile[];
}

export class ContestFilesTable extends React.PureComponent<ContestFilesTableProps> {
  render() {
    return (
      <HTMLTable striped className="table-list-condensed contest-files-table">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  }

  private renderHeader = () => {
    return (
      <thead>
        <tr>
          <th>Filename</th>
          <th className="col-size">Size</th>
          <th className="col-upload-time">Upload time</th>
          <th className="col-download" />
        </tr>
      </thead>
    );
  };

  private renderRows = () => {
    const rows = this.props.files.map(file => (
      <tr key={file.name}>
        <td>{file.name}</td>
        <td>{prettyBytes(file.size)}</td>
        <td>
          <FormattedRelative value={file.lastModifiedTime} />
        </td>
        <td className="col-download">
          <a href={contestFileAPI.renderDownloadFileUrl(this.props.contest.jid, file.name)}>
            <Icon icon="download" />
          </a>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
}
