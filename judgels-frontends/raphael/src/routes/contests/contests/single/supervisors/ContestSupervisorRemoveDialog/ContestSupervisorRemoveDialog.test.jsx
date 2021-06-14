import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ContestSupervisorRemoveDialog } from './ContestSupervisorRemoveDialog';

describe('ContestSupervisorRemoveDialog', () => {
  let onDeleteSupervisors;
  let wrapper;

  beforeEach(() => {
    onDeleteSupervisors = jest.fn().mockReturnValue(Promise.resolve({ deletedSupervisorProfilesMap: {} }));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteSupervisors: onDeleteSupervisors,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestSupervisorRemoveDialog {...props} />
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

    expect(onDeleteSupervisors).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
