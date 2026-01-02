import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { ContestSupervisorAddResultTable } from './ContestSupervisorAddResultTable';

describe('ContestSupervisorAddResultTable', () => {
  beforeEach(async () => {
    const props = {
      usernames: ['budi', 'caca', 'andi', 'dudi', 'zoro'],
      insertedSupervisorProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
        caca: { username: 'caca' },
        dudi: { username: 'dudi' },
      },
    };
    await act(async () =>
      render(
        <TestRouter>
          <ContestSupervisorAddResultTable {...props} />
        </TestRouter>
      )
    );
  });

  it('shows the correct tables', () => {
    const tables = screen.getAllByRole('table');

    const insertedSupervisorRows = within(tables[0]).getAllByRole('row');
    expect(insertedSupervisorRows).toHaveLength(4);
    expect(insertedSupervisorRows[0].textContent).toEqual('andi');
    expect(insertedSupervisorRows[1].textContent).toEqual('budi');
    expect(insertedSupervisorRows[2].textContent).toEqual('caca');
    expect(insertedSupervisorRows[3].textContent).toEqual('dudi');

    const unknownSupervisorRows = within(tables[1]).getAllByRole('row');
    expect(unknownSupervisorRows).toHaveLength(1);
    expect(unknownSupervisorRows[0].textContent).toEqual('zoro');
  });
});
