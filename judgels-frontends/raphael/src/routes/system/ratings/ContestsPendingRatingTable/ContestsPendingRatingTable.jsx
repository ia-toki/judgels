import { Button, HTMLTable } from '@blueprintjs/core';

import './ContestsPendingRatingTable.scss';

export function ContestsPendingRatingTable({ contests, onClickView, isContestViewed }) {
  return (
    <HTMLTable striped className="table-list-condensed contests-pending-rating-table">
      <thead>
        <tr>
          <th>Name</th>
          <th className="col-actions"></th>
        </tr>
      </thead>
      <tbody>
        {contests.map(c => (
          <tr key={c.jid}>
            <td>{c.name}</td>
            <td className="col-actions">
              <Button small intent="primary" onClick={() => onClickView(c)} disabled={isContestViewed}>
                View rating changes
              </Button>
            </td>
          </tr>
        ))}
      </tbody>
    </HTMLTable>
  );
}
