import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { ContestCreateDialog } from './ContestCreateDialog';

describe('ContestCreateDialog', () => {
  let onCreateContest;
  let wrapper;

  beforeEach(() => {
    onCreateContest = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      onCreateContest,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestCreateDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const slug = wrapper.find('input[name="slug"]');
    slug.getDOMNode().value = 'new-contest';
    slug.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateContest).toHaveBeenCalledWith({ slug: 'new-contest' });
  });
});
