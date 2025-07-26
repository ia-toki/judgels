import { mount } from 'enzyme';
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
  let wrapper;

  beforeEach(() => {
    onUpdateChapter = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      chapter,
      onCloseDialog: jest.fn(),
      onUpdateChapter,
    };
    wrapper = mount(
      <Provider store={store}>
        <ChapterEditDialog {...props} />
      </Provider>
    );
  });

  test('edit dialog form', () => {
    const name = wrapper.find('input[name="name"]');
    expect(name.prop('value')).toEqual('Chapter');
    name.prop('onChange')({ target: { value: 'New chapter' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateChapter).toHaveBeenCalledWith(chapter.jid, {
      name: 'New chapter',
    });
  });
});
