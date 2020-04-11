import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { Chapter } from '../../../../modules/api/jerahmeel/chapter';
import { ChapterLesson } from '../../../../modules/api/jerahmeel/chapterLesson';
import { ChapterLessonEditDialog, ChapterLessonEditDialogProps } from './ChapterLessonEditDialog';

const chapter = {
  jid: 'chapter-jid',
} as Chapter;

describe('ChapterLessonEditDialog', () => {
  let onGetLessons: jest.Mock<any>;
  let onSetLessons: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  const lessons: ChapterLesson[] = [
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

  beforeEach(() => {
    onGetLessons = jest.fn().mockReturnValue(Promise.resolve({ data: lessons, lessonsMap }));
    onSetLessons = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: ChapterLessonEditDialogProps = {
      isOpen: true,
      chapter,
      onCloseDialog: jest.fn(),
      onGetLessons,
      onSetLessons,
    };
    wrapper = mount(
      <Provider store={store}>
        <ChapterLessonEditDialog {...props} />
      </Provider>
    );
  });

  test('edit lessons dialog form', () => {
    wrapper.update();

    const button = wrapper.find('button[data-key="edit"]');
    button.simulate('click');

    wrapper.update();

    const lessonsField = wrapper.find('textarea[name="lessons"]');
    expect(lessonsField.prop('value')).toEqual('A,slug-1\nB,slug-2');

    lessonsField.simulate('change', { target: { value: 'P, slug-3\n  Q,slug-4  ' } });

    const form = wrapper.find('form');
    form.simulate('submit');

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
