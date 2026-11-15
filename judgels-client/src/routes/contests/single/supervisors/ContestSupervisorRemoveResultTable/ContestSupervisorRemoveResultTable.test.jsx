import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../test/RouterWrapper';
import { ContestSupervisorRemoveResultTable } from './ContestSupervisorRemoveResultTable';

describe('ContestSupervisorRemoveResultTable', () => {
  const renderComponent = async ({
    usernames = ['budi', 'andi', 'zoro'],
    removedSupervisorProfilesMap = {
      budi: { username: 'budi' },
      andi: { username: 'andi' },
    },
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <ContestSupervisorRemoveResultTable
            usernames={usernames}
            removedSupervisorProfilesMap={removedSupervisorProfilesMap}
          />
        </TestRouter>
      )
    );
  };

  test('renders the correct tables', async () => {
    await renderComponent();

    const tables = screen.getAllByRole('table');

    const removedSupervisorRows = within(tables[0]).getAllByRole('row');
    expect(removedSupervisorRows).toHaveLength(2);
    expect(removedSupervisorRows[0].textContent).toEqual('andi');
    expect(removedSupervisorRows[1].textContent).toEqual('budi');

    const unknownSupervisorRows = within(tables[1]).getAllByRole('row');
    expect(unknownSupervisorRows).toHaveLength(1);
    expect(unknownSupervisorRows[0].textContent).toEqual('zoro');
  });
});
