import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { ContestAnnouncementCreateDialog } from './ContestAnnouncementCreateDialog';

describe('ContestAnnouncementCreateDialog', () => {
  let onCreateAnnouncement;

  beforeEach(() => {
    onCreateAnnouncement = vi.fn().mockReturnValue(Promise.resolve({}));

    const props = {
      contest: { jid: 'contestJid' },
      onCreateAnnouncement,
    };
    render(<ContestAnnouncementCreateDialog {...props} />);
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = screen.getByRole('button', { name: /status/i });
    // await user.click(status);

    const title = screen.getByRole('textbox', { name: /title/i });
    await user.type(title, 'Snack');

    const content = screen.getByRole('textbox', { name: /content/i });
    await user.type(content, 'Snack is provided.');

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    expect(onCreateAnnouncement).toHaveBeenCalledWith('contestJid', {
      title: 'Snack',
      content: 'Snack is provided.',
      status: ContestAnnouncementStatus.Published,
    });
  });
});
