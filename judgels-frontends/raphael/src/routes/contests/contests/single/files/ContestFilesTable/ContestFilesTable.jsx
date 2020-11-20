import { HTMLTable, Icon } from '@blueprintjs/core';
import prettyBytes from 'pretty-bytes';
import * as React from 'react';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { contestFileAPI } from '../../../../../../modules/api/uriel/contestFile';

import './ContestFilesTable.css';

export function ContestFilesTable({ contest, files }) {
  const renderHeader = () => {
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

  const renderRows = () => {
    const rows = files.map(file => (
      <tr key={file.name}>
        <td>{file.name}</td>
        <td>{prettyBytes(file.size)}</td>
        <td>
          <FormattedRelative value={file.lastModifiedTime} />
        </td>
        <td className="col-download">
          <a href={contestFileAPI.renderDownloadFileUrl(contest.jid, file.name)}>
            <Icon icon="download" />
          </a>
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };
  return (
    <HTMLTable striped className="table-list-condensed contest-files-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
