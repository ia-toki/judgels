import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ChapterEditDialog } from './ChapterEditDialog';

const chapter = {
  id: 1,
  jid: 'chapterJid',
  name: 'Chapter',
};

describe('ChapterEditDialog', () => {
  let onUpdateChapter;

  beforeEach(() => {
    onUpdateChapter = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      chapter,
      onCloseDialog: jest.fn(),
      onUpdateChapter,
    };
    render(
      <Provider store={store}>
        <ChapterEditDialog {...props} />
      </Provider>
    );
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
