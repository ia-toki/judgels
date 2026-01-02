import { render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { ContestContestantRemoveResultTable } from './ContestContestantRemoveResultTable';

describe('ContestContestantRemoveResultTable', () => {
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'andi', 'zoro'],
      deletedContestantProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
    };
    render(
      <TestRouter>
        <ContestContestantRemoveResultTable {...props} />
      </TestRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = screen.getAllByRole('table');

    const deletedContestantRows = within(tables[0]).getAllByRole('row');
    expect(deletedContestantRows).toHaveLength(2);
    expect(deletedContestantRows[0]).toHaveTextContent('andi');
    expect(deletedContestantRows[1]).toHaveTextContent('budi');

    const unknownContestantRows = within(tables[1]).getAllByRole('row');
    expect(unknownContestantRows).toHaveLength(1);
    expect(unknownContestantRows[0]).toHaveTextContent('zoro');
  });
});
