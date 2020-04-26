import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { ArchiveCreateDialog } from './ArchiveCreateDialog';

describe('ArchiveCreateDialog', () => {
  let onGetArchiveConfig: jest.Mock<any>;
  let onCreateArchive: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onCreateArchive = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      onGetArchiveConfig,
      onCreateArchive,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ArchiveCreateDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('create dialog form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

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

    expect(onCreateArchive).toHaveBeenCalledWith({
      slug: 'new-archive',
      name: 'New archive',
      category: 'New category',
      description: 'New description',
    });
  });
});
