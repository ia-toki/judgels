import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ContestManagerRemoveDialog } from './ContestManagerRemoveDialog';

describe('ContestManagerRemoveDialog', () => {
  let onDeleteManagers;
  let wrapper;

  beforeEach(() => {
    onDeleteManagers = jest.fn().mockReturnValue(Promise.resolve({ deletedManagerProfilesMap: {} }));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteManagers,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestManagerRemoveDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.getDOMNode().value = 'andi\n\nbudi\n caca  \n';
    usernames.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onDeleteManagers).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
