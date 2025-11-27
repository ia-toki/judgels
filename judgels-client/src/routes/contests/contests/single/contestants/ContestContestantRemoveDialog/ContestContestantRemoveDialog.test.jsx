import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestContestantRemoveDialog } from './ContestContestantRemoveDialog';

describe('ContestContestantRemoveDialog', () => {
  let onDeleteContestants;

  beforeEach(() => {
    onDeleteContestants = jest.fn().mockReturnValue(Promise.resolve({ deletedContestantProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteContestants,
    };
    render(
      <Provider store={store}>
        <ContestContestantRemoveDialog {...props} />
      </Provider>
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const usernames = screen.getByRole('textbox');
    await user.type(usernames, 'andi\n\nbudi\n caca  \n');

    const submitButton = screen.getByRole('button', { name: /remove$/i });
    await user.click(submitButton);

    expect(onDeleteContestants).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
