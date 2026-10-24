import { HTMLTable, Intent } from '@blueprintjs/core';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';

import './ContestsPendingRatingTable.scss';

export function ContestsPendingRatingTable({ contests }) {
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
              <ButtonLink small intent={Intent.PRIMARY} to={`/admin/ratings/${c.slug}`}>
                View rating changes
              </ButtonLink>
            </td>
          </tr>
        ))}
      </tbody>
    </HTMLTable>
  );
}
