import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestContestantAddDialog } from './ContestContestantAddDialog';

describe('ContestContestantAddDialog', () => {
  let onUpsertContestants;

  beforeEach(() => {
    onUpsertContestants = jest
      .fn()
      .mockReturnValue(Promise.resolve({ insertedContestantProfilesMap: {}, alreadyContestantProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onUpsertContestants,
    };
    render(
      <Provider store={store}>
        <ContestContestantAddDialog {...props} />
      </Provider>
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const usernames = screen.getByRole('textbox');
    await user.type(usernames, 'andi\n\nbudi\n caca  \n');

    const submitButton = screen.getByRole('button', { name: /add$/i });
    await user.click(submitButton);

    expect(onUpsertContestants).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
