import { render, screen, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { ContestManagerAddResultTable } from './ContestManagerAddResultTable';

describe('ContestManagerAddResultTable', () => {
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'caca', 'andi', 'dudi', 'zoro'],
      insertedManagerProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
      alreadyManagerProfilesMap: {
        dudi: { username: 'dudi' },
        caca: { username: 'caca' },
      },
    };
    render(
      <MemoryRouter>
        <ContestManagerAddResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = screen.getAllByRole('table');

    const insertedManagerRows = within(tables[0]).getAllByRole('row');
    expect(insertedManagerRows).toHaveLength(2);
    expect(insertedManagerRows[0]).toHaveTextContent('andi');
    expect(insertedManagerRows[1]).toHaveTextContent('budi');

    const alreadyManagerRows = within(tables[1]).getAllByRole('row');
    expect(alreadyManagerRows).toHaveLength(2);
    expect(alreadyManagerRows[0]).toHaveTextContent('caca');
    expect(alreadyManagerRows[1]).toHaveTextContent('dudi');

    const unknownManagerRows = within(tables[2]).getAllByRole('row');
    expect(unknownManagerRows).toHaveLength(1);
    expect(unknownManagerRows[0]).toHaveTextContent('zoro');
  });
});
