import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestSupervisorRemoveDialog } from './ContestSupervisorRemoveDialog';

describe('ContestSupervisorRemoveDialog', () => {
  let onDeleteSupervisors;

  beforeEach(() => {
    onDeleteSupervisors = jest.fn().mockReturnValue(Promise.resolve({ deletedSupervisorProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteSupervisors: onDeleteSupervisors,
    };
    render(
      <Provider store={store}>
        <ContestSupervisorRemoveDialog {...props} />
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

    expect(onDeleteSupervisors).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
