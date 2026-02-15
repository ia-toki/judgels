import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ChapterEditDialog } from './ChapterEditDialog';

const chapter = {
  id: 1,
  jid: 'chapterJid',
  name: 'Chapter',
};

describe('ChapterEditDialog', () => {
  let onUpdateChapter;

  beforeEach(() => {
    onUpdateChapter = vi.fn().mockReturnValue(Promise.resolve({}));

    const props = {
      isOpen: true,
      chapter,
      onCloseDialog: vi.fn(),
      onUpdateChapter,
    };
    render(<ChapterEditDialog {...props} />);
  });

  test('edit dialog form', async () => {
    const user = userEvent.setup();

    const name = screen.getByRole('textbox');
    expect(name).toHaveValue('Chapter');
    await user.clear(name);
    await user.type(name, 'New chapter');

    const submitButton = screen.getByRole('button', { name: /update/i });
    await user.click(submitButton);

    expect(onUpdateChapter).toHaveBeenCalledWith(chapter.jid, {
      name: 'New chapter',
    });
  });
});
