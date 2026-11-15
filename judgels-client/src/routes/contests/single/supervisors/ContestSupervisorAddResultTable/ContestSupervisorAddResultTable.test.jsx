import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../test/RouterWrapper';
import { ContestSupervisorAddResultTable } from './ContestSupervisorAddResultTable';

describe('ContestSupervisorAddResultTable', () => {
  const renderComponent = async ({
    usernames = ['budi', 'caca', 'andi', 'dudi', 'zoro'],
    addedSupervisorProfilesMap = {
      budi: { username: 'budi' },
      andi: { username: 'andi' },
      caca: { username: 'caca' },
      dudi: { username: 'dudi' },
    },
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <ContestSupervisorAddResultTable
            usernames={usernames}
            addedSupervisorProfilesMap={addedSupervisorProfilesMap}
          />
        </TestRouter>
      )
    );
  };

  test('renders the correct tables', async () => {
    await renderComponent();

    const tables = screen.getAllByRole('table');

    const addedSupervisorRows = within(tables[0]).getAllByRole('row');
    expect(addedSupervisorRows).toHaveLength(4);
    expect(addedSupervisorRows[0].textContent).toEqual('andi');
    expect(addedSupervisorRows[1].textContent).toEqual('budi');
    expect(addedSupervisorRows[2].textContent).toEqual('caca');
    expect(addedSupervisorRows[3].textContent).toEqual('dudi');

    const unknownSupervisorRows = within(tables[1]).getAllByRole('row');
    expect(unknownSupervisorRows).toHaveLength(1);
    expect(unknownSupervisorRows[0].textContent).toEqual('zoro');
  });
});
