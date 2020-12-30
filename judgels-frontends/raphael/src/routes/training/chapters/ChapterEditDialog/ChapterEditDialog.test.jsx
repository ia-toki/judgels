import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

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

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      isOpen: true,
      chapter,
      onCloseDialog: jest.fn(),
      onUpdateChapter,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ChapterEditDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'New chapter' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateChapter).toHaveBeenCalledWith(chapter.jid, {
      name: 'New chapter',
    });
  });
});
