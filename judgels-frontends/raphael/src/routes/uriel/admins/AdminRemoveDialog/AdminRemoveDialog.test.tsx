import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { AdminRemoveDialog, AdminRemoveDialogProps } from './AdminRemoveDialog';

describe('AdminRemoveDialog', () => {
  let onDeleteAdmins: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onDeleteAdmins = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props: AdminRemoveDialogProps = {
      onDeleteAdmins,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <AdminRemoveDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('remove admins dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onDeleteAdmins).toHaveBeenCalledWith(['andi', 'budi', 'caca']);
  });
});
