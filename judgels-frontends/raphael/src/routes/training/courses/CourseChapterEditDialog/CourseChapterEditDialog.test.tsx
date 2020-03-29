import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { Course } from '../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../modules/api/jerahmeel/courseChapter';
import { CourseChapterEditDialog, CourseChapterEditDialogProps } from './CourseChapterEditDialog';

const course = {
  jid: 'course-jid',
} as Course;

describe('CourseChapterEditDialog', () => {
  let onGetChapters: jest.Mock<any>;
  let onSetChapters: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  const chapters: CourseChapter[] = [
    {
      alias: 'A',
      chapterJid: 'jid-1',
    },
    {
      alias: 'B',
      chapterJid: 'jid-2',
    },
  ];

  beforeEach(() => {
    onGetChapters = jest.fn().mockReturnValue(Promise.resolve({ data: chapters, chaptersMap: {} }));
    onSetChapters = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: CourseChapterEditDialogProps = {
      isOpen: true,
      course,
      onCloseDialog: jest.fn(),
      onGetChapters,
      onSetChapters,
    };
    wrapper = mount(
      <Provider store={store}>
        <CourseChapterEditDialog {...props} />
      </Provider>
    );
  });

  test('edit chapters dialog form', () => {
    wrapper.update();

    const button = wrapper.find('button[data-key="edit"]');
    button.simulate('click');

    wrapper.update();

    const chaptersField = wrapper.find('textarea[name="chapters"]');
    expect(chaptersField.prop('value')).toEqual('A,jid-1\nB,jid-2');

    chaptersField.simulate('change', { target: { value: 'P, jid-3\n  Q,jid-4  ' } });

    const form = wrapper.find('form');
    form.simulate('submit');

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
