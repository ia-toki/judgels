import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ContestContestantRemoveDialog } from './ContestContestantRemoveDialog';

describe('ContestContestantRemoveDialog', () => {
  let onDeleteContestants;

  beforeEach(() => {
    onDeleteContestants = vi.fn().mockReturnValue(Promise.resolve({ deletedContestantProfilesMap: {} }));

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteContestants,
    };
    render(<ContestContestantRemoveDialog {...props} />);
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
