import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ContestContestantAddDialog } from './ContestContestantAddDialog';

describe('ContestContestantAddDialog', () => {
  let onUpsertContestants;

  beforeEach(() => {
    onUpsertContestants = vi
      .fn()
      .mockReturnValue(Promise.resolve({ insertedContestantProfilesMap: {}, alreadyContestantProfilesMap: {} }));

    const props = {
      contest: { jid: 'contestJid' },
      onUpsertContestants,
    };
    render(<ContestContestantAddDialog {...props} />);
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
