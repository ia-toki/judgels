import { render, screen, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { ContestContestantAddResultTable } from './ContestContestantAddResultTable';

describe('ContestContestantAddResultTable', () => {
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'caca', 'andi', 'dudi', 'zoro'],
      insertedContestantProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
      alreadyContestantProfilesMap: {
        dudi: { username: 'dudi' },
        caca: { username: 'caca' },
      },
    };
    render(
      <MemoryRouter>
        <ContestContestantAddResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = screen.getAllByRole('table');

    const insertedContestantRows = within(tables[0]).getAllByRole('row');
    expect(insertedContestantRows).toHaveLength(2);
    expect(insertedContestantRows[0]).toHaveTextContent('andi');
    expect(insertedContestantRows[1]).toHaveTextContent('budi');

    const alreadyContestantRows = within(tables[1]).getAllByRole('row');
    expect(alreadyContestantRows).toHaveLength(2); // 2 data rows
    expect(alreadyContestantRows[0]).toHaveTextContent('caca');
    expect(alreadyContestantRows[1]).toHaveTextContent('dudi');

    const unknownContestantRows = within(tables[2]).getAllByRole('row');
    expect(unknownContestantRows).toHaveLength(1);
    expect(unknownContestantRows[0]).toHaveTextContent('zoro');
  });
});
