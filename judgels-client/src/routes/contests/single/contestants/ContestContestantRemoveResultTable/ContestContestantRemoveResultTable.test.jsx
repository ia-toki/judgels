import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../test/RouterWrapper';
import { ContestContestantRemoveResultTable } from './ContestContestantRemoveResultTable';

describe('ContestContestantRemoveResultTable', () => {
  const renderComponent = async ({
    usernames = ['budi', 'andi', 'zoro'],
    removedContestantProfilesMap = {
      budi: { username: 'budi' },
      andi: { username: 'andi' },
    },
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <ContestContestantRemoveResultTable
            usernames={usernames}
            removedContestantProfilesMap={removedContestantProfilesMap}
          />
        </TestRouter>
      )
    );
  };

  test('renders the correct tables', async () => {
    await renderComponent();

    const tables = screen.getAllByRole('table');

    const removedContestantRows = within(tables[0]).getAllByRole('row');
    expect(removedContestantRows).toHaveLength(2);
    expect(removedContestantRows[0]).toHaveTextContent('andi');
    expect(removedContestantRows[1]).toHaveTextContent('budi');

    const unknownContestantRows = within(tables[1]).getAllByRole('row');
    expect(unknownContestantRows).toHaveLength(1);
    expect(unknownContestantRows[0]).toHaveTextContent('zoro');
  });
});
