import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { CourseChapterEditDialog } from './CourseChapterEditDialog';

const course = {
  jid: 'course-jid',
};

describe('CourseChapterEditDialog', () => {
  let onGetChapters;
  let onSetChapters;
  const chapters = [
    {
      alias: 'A',
      chapterJid: 'jid-1',
    },
    {
      alias: 'B',
      chapterJid: 'jid-2',
    },
  ];

  beforeEach(async () => {
    onGetChapters = jest.fn().mockReturnValue(Promise.resolve({ data: chapters, chaptersMap: {} }));
    onSetChapters = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      course,
      onCloseDialog: jest.fn(),
      onGetChapters,
      onSetChapters,
    };
    await act(async () =>
      render(
        <Provider store={store}>
          <CourseChapterEditDialog {...props} />
        </Provider>
      )
    );
  });

  test('edit chapters dialog form', async () => {
    const user = userEvent.setup();

    const editButton = screen.getByRole('button', { name: /edit/i });
    await user.click(editButton);

    const chaptersField = screen.getByRole('textbox', { name: /chapters/i });
    expect(chaptersField).toHaveValue('A,jid-1\nB,jid-2');

    await user.clear(chaptersField);
    await user.type(chaptersField, 'P, jid-3\n  Q,jid-4  ');

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(onSetChapters).toHaveBeenCalledWith(course.jid, [
      {
        alias: 'P',
        chapterJid: 'jid-3',
      },
      {
        alias: 'Q',
        chapterJid: 'jid-4',
      },
    ]);
  });
});
