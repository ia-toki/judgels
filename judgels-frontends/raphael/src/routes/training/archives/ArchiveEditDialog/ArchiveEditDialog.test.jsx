import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { Archive } from '../../../../modules/api/jerahmeel/archive';
import { ArchiveEditDialog } from './ArchiveEditDialog';

const archive: Archive = {
  id: 1,
  jid: 'archiveJid',
  slug: 'archive',
  name: 'Archive',
  category: 'Category',
  description: 'This is a archive',
};

describe('ArchiveEditDialog', () => {
  let onUpdateArchive: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpdateArchive = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

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
    slug.simulate('change', { target: { value: 'new-archive' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'New archive' } });

    const category = wrapper.find('input[name="category"]');
    category.simulate('change', { target: { value: 'New category' } });

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'New description' } });

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
