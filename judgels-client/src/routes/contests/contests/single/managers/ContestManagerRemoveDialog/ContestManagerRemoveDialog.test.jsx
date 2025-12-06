import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { ContestManagerRemoveDialog } from './ContestManagerRemoveDialog';

describe('ContestManagerRemoveDialog', () => {
  let onDeleteManagers;

  beforeEach(() => {
    onDeleteManagers = vi.fn().mockReturnValue(Promise.resolve({ deletedManagerProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteManagers,
    };
    render(
      <Provider store={store}>
        <ContestManagerRemoveDialog {...props} />
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

    expect(onDeleteManagers).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
