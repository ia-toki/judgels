import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

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
  let wrapper;

  beforeEach(() => {
    onUpdateArchive = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      isOpen: true,
      archive,
      onCloseDialog: jest.fn(),
      onUpdateArchive,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ArchiveEditDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const slug = wrapper.find('input[name="slug"]');
    slug.getDOMNode().value = 'new-archive';
    slug.simulate('input');

    const name = wrapper.find('input[name="name"]');
    name.getDOMNode().value = 'New archive';
    name.simulate('input');

    const category = wrapper.find('input[name="category"]');
    category.getDOMNode().value = 'New category';
    category.simulate('input');

    const description = wrapper.find('textarea[name="description"]');
    description.getDOMNode().value = 'New description';
    description.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateArchive).toHaveBeenCalledWith(archive.jid, {
      slug: 'new-archive',
      name: 'New archive',
      category: 'New category',
      description: 'New description',
    });
  });
});
