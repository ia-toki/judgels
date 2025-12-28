import { render, screen, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { ContestManagerRemoveResultTable } from './ContestManagerRemoveResultTable';

describe('ContestManagerRemoveResultTable', () => {
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'andi', 'zoro'],
      deletedManagerProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
    };
    render(
      <MemoryRouter>
        <ContestManagerRemoveResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = screen.getAllByRole('table');

    const deletedManagerRows = within(tables[0]).getAllByRole('row');
    expect(deletedManagerRows).toHaveLength(2);
    expect(deletedManagerRows[0]).toHaveTextContent('andi');
    expect(deletedManagerRows[1]).toHaveTextContent('budi');

    const unknownManagerRows = within(tables[1]).getAllByRole('row');
    expect(unknownManagerRows).toHaveLength(1);
    expect(unknownManagerRows[0]).toHaveTextContent('zoro');
  });
});
