import { render, screen, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { ContestSupervisorRemoveResultTable } from './ContestSupervisorRemoveResultTable';

describe('ContestSupervisorRemoveResultTable', () => {
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'andi', 'zoro'],
      deletedSupervisorProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
    };
    render(
      <MemoryRouter>
        <ContestSupervisorRemoveResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = screen.getAllByRole('table');

    const deletedSupervisorRows = within(tables[0]).getAllByRole('row');
    expect(deletedSupervisorRows).toHaveLength(2);
    expect(deletedSupervisorRows[0].textContent).toEqual('andi');
    expect(deletedSupervisorRows[1].textContent).toEqual('budi');

    const unknownSupervisorRows = within(tables[1]).getAllByRole('row');
    expect(unknownSupervisorRows).toHaveLength(1);
    expect(unknownSupervisorRows[0].textContent).toEqual('zoro');
  });
});
