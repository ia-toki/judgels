import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { CourseChapterEditDialog } from './CourseChapterEditDialog';

const course = {
  jid: 'course-jid',
};

describe('CourseChapterEditDialog', () => {
  let onGetChapters;
  let onSetChapters;
  let wrapper;

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

  beforeEach(() => {
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

    const chaptersField = wrapper.find('textarea[name="chapters"]');
    expect(chaptersField.prop('value')).toEqual('A,jid-1\nB,jid-2');

    chaptersField.prop('onChange')({ target: { value: 'P, jid-3\n  Q,jid-4  ' } });

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
