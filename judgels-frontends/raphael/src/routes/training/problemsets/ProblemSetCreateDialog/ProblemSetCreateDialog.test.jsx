import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { ProblemSetCreateDialog } from './ProblemSetCreateDialog';

describe('ProblemSetCreateDialog', () => {
  let onGetProblemSetConfig: jest.Mock<any>;
  let onCreateProblemSet: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onCreateProblemSet = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      onGetProblemSetConfig,
      onCreateProblemSet,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ProblemSetCreateDialog {...props} />
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
    slug.simulate('change', { target: { value: 'new-problemSet' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'New problemSet' } });

    const archiveSlug = wrapper.find('input[name="archiveSlug"]');
    archiveSlug.simulate('change', { target: { value: 'New archive' } });

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'New description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateProblemSet).toHaveBeenCalledWith({
      slug: 'new-problemSet',
      name: 'New problemSet',
      archiveSlug: 'New archive',
      description: 'New description',
    });
  });
});
