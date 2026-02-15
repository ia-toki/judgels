import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ContestManagerAddDialog } from './ContestManagerAddDialog';

describe('ContestManagerAddDialog', () => {
  let onUpsertManagers;

  beforeEach(() => {
    onUpsertManagers = vi
      .fn()
      .mockReturnValue(Promise.resolve({ insertedManagerProfilesMap: {}, alreadyManagerProfilesMap: {} }));

    const props = {
      contest: { jid: 'contestJid' },
      onUpsertManagers,
    };
    render(<ContestManagerAddDialog {...props} />);
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const usernames = screen.getByRole('textbox');
    await user.type(usernames, 'andi\n\nbudi\n caca  \n');

    const submitButton = screen.getByRole('button', { name: /^add$/i });
    await user.click(submitButton);

    expect(onUpsertManagers).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
