import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { ContestAnnouncementEditDialog } from './ContestAnnouncementEditDialog';

describe('ContestAnnouncementEditDialog', () => {
  let onUpdateAnnouncement;

  const announcement = {
    jid: 'announcementJid123',
    title: 'Snack',
    content: 'Snack is provided.',
    status: ContestAnnouncementStatus.Published,
  };

  beforeEach(async () => {
    onUpdateAnnouncement = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const onToggleEditDialog = () => {
      return;
    };

    const store = createMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      announcement,
      onToggleEditDialog,
      onUpdateAnnouncement,
    };
    await act(async () =>
      render(
        <Provider store={store}>
          <ContestAnnouncementEditDialog {...props} />
        </Provider>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = screen.getByRole('button', { name: /status/i });
    // await user.click(status);

    const title = screen.getByRole('textbox', { name: /title/i });
    expect(title).toHaveValue('Snack');
    await user.clear(title);
    await user.type(title, 'Snack edited');

    const content = screen.getByRole('textbox', { name: /content/i });
    expect(content).toHaveValue('Snack is provided.');
    await user.clear(content);
    await user.type(content, 'Snack is NOT provided.');

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(onUpdateAnnouncement).toHaveBeenCalledWith('contestJid', 'announcementJid123', {
      title: 'Snack edited',
      content: 'Snack is NOT provided.',
      status: ContestAnnouncementStatus.Published,
    });
  });
});
