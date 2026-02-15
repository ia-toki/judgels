import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { SupervisorManagementPermission } from '../../../../../../modules/api/uriel/contestSupervisor';
import { ContestSupervisorAddDialog } from './ContestSupervisorAddDialog';

describe('ContestSupervisorAddDialog', () => {
  let onUpsertSupervisors;

  beforeEach(() => {
    onUpsertSupervisors = vi
      .fn()
      .mockReturnValue(Promise.resolve({ upsertedSupervisorProfilesMap: {}, alreadySupervisorProfilesMap: {} }));

    const props = {
      contest: { jid: 'contestJid' },
      onUpsertSupervisors: onUpsertSupervisors,
    };
    render(<ContestSupervisorAddDialog {...props} />);
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const usernames = screen.getByRole('textbox');
    await user.type(usernames, 'andi\n\nbudi\n caca  \n');

    const announcementPermission = document.querySelector('input[name="managementPermissions.Announcements"]');
    await user.click(announcementPermission);

    const clarificationPermission = document.querySelector('input[name="managementPermissions.Clarifications"]');
    await user.click(clarificationPermission);

    const dialog = screen.getByRole('dialog');
    const submitButton = within(dialog).getByRole('button', { name: /add\/update/i });
    await user.click(submitButton);

    expect(onUpsertSupervisors).toHaveBeenCalledWith('contestJid', {
      managementPermissions: [
        SupervisorManagementPermission.Announcements,
        SupervisorManagementPermission.Clarifications,
      ],
      usernames: ['andi', 'budi', 'caca'],
    });
  });
});
