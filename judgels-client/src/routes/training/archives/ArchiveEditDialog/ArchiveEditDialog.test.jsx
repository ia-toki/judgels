import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ArchiveEditDialog } from './ArchiveEditDialog';

const archive = {
  id: 1,
  jid: 'archiveJid',
  slug: 'archive',
  name: 'Archive',
  category: 'Category',
  description: 'This is a archive',
};

describe('ArchiveEditDialog', () => {
  let onUpdateArchive;

  beforeEach(() => {
    onUpdateArchive = vi.fn().mockReturnValue(Promise.resolve({}));

    const props = {
      isOpen: true,
      archive,
      onCloseDialog: vi.fn(),
      onUpdateArchive,
    };
    render(<ArchiveEditDialog {...props} />);
  });

  test('edit dialog form', async () => {
    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('archive');
    await user.clear(slug);
    await user.type(slug, 'new-archive');

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Archive');
    await user.clear(name);
    await user.type(name, 'New archive');

    const category = screen.getByRole('textbox', { name: /category/i });
    expect(category).toHaveValue('Category');
    await user.clear(category);
    await user.type(category, 'New category');

    const description = screen.getByRole('textbox', { name: /description/i });
    expect(description).toHaveValue('This is a archive');
    await user.clear(description);
    await user.type(description, 'New description');

    const submitButton = screen.getByRole('button', { name: /update/i });
    await user.click(submitButton);

    expect(onUpdateArchive).toHaveBeenCalledWith(archive.jid, {
      slug: 'new-archive',
      name: 'New archive',
      category: 'New category',
      description: 'New description',
    });
  });
});
