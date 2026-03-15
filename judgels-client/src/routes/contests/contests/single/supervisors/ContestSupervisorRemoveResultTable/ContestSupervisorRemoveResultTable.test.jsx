import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { ContestSupervisorRemoveResultTable } from './ContestSupervisorRemoveResultTable';

describe('ContestSupervisorRemoveResultTable', () => {
  const renderComponent = async ({
    usernames = ['budi', 'andi', 'zoro'],
    deletedSupervisorProfilesMap = {
      budi: { username: 'budi' },
      andi: { username: 'andi' },
    },
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <ContestSupervisorRemoveResultTable usernames={usernames} deletedSupervisorProfilesMap={deletedSupervisorProfilesMap} />
        </TestRouter>
      )
    );
  };

  test('shows the correct tables', async () => {
    await renderComponent();

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
