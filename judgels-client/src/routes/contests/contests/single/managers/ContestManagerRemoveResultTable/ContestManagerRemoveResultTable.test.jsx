import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { ContestManagerRemoveResultTable } from './ContestManagerRemoveResultTable';

describe('ContestManagerRemoveResultTable', () => {
  const renderComponent = async ({
    usernames = ['budi', 'andi', 'zoro'],
    deletedManagerProfilesMap = {
      budi: { username: 'budi' },
      andi: { username: 'andi' },
    },
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <ContestManagerRemoveResultTable
            usernames={usernames}
            deletedManagerProfilesMap={deletedManagerProfilesMap}
          />
        </TestRouter>
      )
    );
  };

  test('shows the correct tables', async () => {
    await renderComponent();

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
