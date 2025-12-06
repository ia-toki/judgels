import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { ChapterLessonEditDialog } from './ChapterLessonEditDialog';

const chapter = {
  jid: 'chapter-jid',
};

describe('ChapterLessonEditDialog', () => {
  let onGetLessons;
  let onSetLessons;
  const lessons = [
    {
      alias: 'A',
      lessonJid: 'jid-1',
    },
    {
      alias: 'B',
      lessonJid: 'jid-2',
    },
  ];

  const lessonsMap = {
    'jid-1': { slug: 'slug-1' },
    'jid-2': { slug: 'slug-2' },
  };

  beforeEach(async () => {
    onGetLessons = vi.fn().mockReturnValue(Promise.resolve({ data: lessons, lessonsMap }));
    onSetLessons = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      chapter,
      onCloseDialog: vi.fn(),
      onGetLessons,
      onSetLessons,
    };
    await act(async () =>
      render(
        <Provider store={store}>
          <ChapterLessonEditDialog {...props} />
        </Provider>
      )
    );
  });

  test('edit lessons dialog form', async () => {
    const user = userEvent.setup();

    const editButton = screen.getByRole('button', { name: /edit/i });
    await user.click(editButton);

    const lessonsField = screen.getByRole('textbox', { name: /lessons/i });
    expect(lessonsField).toHaveValue('A,slug-1\nB,slug-2');

    await user.clear(lessonsField);
    await user.type(lessonsField, 'P, slug-3\n  Q,slug-4  ');

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(onSetLessons).toHaveBeenCalledWith(chapter.jid, [
      {
        alias: 'P',
        slug: 'slug-3',
      },
      {
        alias: 'Q',
        slug: 'slug-4',
      },
    ]);
  });
});
