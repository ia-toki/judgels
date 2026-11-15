import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../test/RouterWrapper';
import { ContestContestantAddResultTable } from './ContestContestantAddResultTable';

describe('ContestContestantAddResultTable', () => {
  const renderComponent = async ({
    usernames = ['budi', 'caca', 'andi', 'dudi', 'zoro'],
    addedContestantProfilesMap = {
      budi: { username: 'budi' },
      andi: { username: 'andi' },
    },
    alreadyContestantProfilesMap = {
      dudi: { username: 'dudi' },
      caca: { username: 'caca' },
    },
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <ContestContestantAddResultTable
            usernames={usernames}
            addedContestantProfilesMap={addedContestantProfilesMap}
            alreadyContestantProfilesMap={alreadyContestantProfilesMap}
          />
        </TestRouter>
      )
    );
  };

  test('renders the correct tables', async () => {
    await renderComponent();

    const tables = screen.getAllByRole('table');

    const addedContestantRows = within(tables[0]).getAllByRole('row');
    expect(addedContestantRows).toHaveLength(2);
    expect(addedContestantRows[0]).toHaveTextContent('andi');
    expect(addedContestantRows[1]).toHaveTextContent('budi');

    const alreadyContestantRows = within(tables[1]).getAllByRole('row');
    expect(alreadyContestantRows).toHaveLength(2); // 2 data rows
    expect(alreadyContestantRows[0]).toHaveTextContent('caca');
    expect(alreadyContestantRows[1]).toHaveTextContent('dudi');

    const unknownContestantRows = within(tables[2]).getAllByRole('row');
    expect(unknownContestantRows).toHaveLength(1);
    expect(unknownContestantRows[0]).toHaveTextContent('zoro');
  });
});
