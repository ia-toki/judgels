import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { ContestContestantRemoveResultTable } from './ContestContestantRemoveResultTable';

describe('ContestContestantRemoveResultTable', () => {
  const renderComponent = async ({
    usernames = ['budi', 'andi', 'zoro'],
    deletedContestantProfilesMap = {
      budi: { username: 'budi' },
      andi: { username: 'andi' },
    },
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <ContestContestantRemoveResultTable
            usernames={usernames}
            deletedContestantProfilesMap={deletedContestantProfilesMap}
          />
        </TestRouter>
      )
    );
  };

  test('shows the correct tables', async () => {
    await renderComponent();

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
