import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

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
    onUpdateArchive = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      archive,
      onCloseDialog: jest.fn(),
      onUpdateArchive,
    };
    render(
      <Provider store={store}>
        <ArchiveEditDialog {...props} />
      </Provider>
    );
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
