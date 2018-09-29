import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { ContestCreateDialog } from './ContestCreateDialog';

describe('ContestCreateDialog', () => {
  let onGetContestConfig: jest.Mock<any>;
  let onCreateContest: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onGetContestConfig = jest.fn().mockReturnValue(Promise.resolve({ isAllowedToCreateContest: true }));
    onCreateContest = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      onGetContestConfig,
      onCreateContest,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestCreateDialog {...props} />
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
    slug.simulate('change', { target: { value: 'new-contest' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateContest).toHaveBeenCalledWith({ slug: 'new-contest' });
  });
});
