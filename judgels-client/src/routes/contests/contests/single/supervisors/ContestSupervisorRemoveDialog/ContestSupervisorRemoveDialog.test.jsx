import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ContestSupervisorRemoveDialog } from './ContestSupervisorRemoveDialog';

describe('ContestSupervisorRemoveDialog', () => {
  let onDeleteSupervisors;

  beforeEach(() => {
    onDeleteSupervisors = vi.fn().mockReturnValue(Promise.resolve({ deletedSupervisorProfilesMap: {} }));

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteSupervisors: onDeleteSupervisors,
    };
    render(<ContestSupervisorRemoveDialog {...props} />);
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
